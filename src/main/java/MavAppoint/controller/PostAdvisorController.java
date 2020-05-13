package MavAppoint.controller;

import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import MavAppoint.database.DBManager;
import MavAppoint.model.User;
import MavAppoint.model.UserAdvisor;
import MavAppoint.util.Util;

public class PostAdvisorController {
	private static String aws_cred_public;
	private static String aws_cred_secret;
	private static String email_from;
	
	public PostAdvisorController() {
		PostAdvisorController.aws_cred_public = System.getenv("AWS_CRED_PUBLIC");
		PostAdvisorController.aws_cred_secret = System.getenv("AWS_CRED_SECRET");
		PostAdvisorController.email_from = System.getenv("EMAIL_FROM");
	}
	
	public JSONObject createAdvisor(JSONObject event) {
		JSONObject responseJson = new JSONObject();
		 try {
			 if(checkRequestBody(event)) {
				 if(validateRequestBody(event)) {
					User user = new User((String) event.get("email"), "advisor");
					UserAdvisor advisor = new UserAdvisor((String) event.get("name"), (String) event.get("department"));
		        	
	        		DBManager dbmgr = DBManager.getInstance();
	        		dbmgr.createConnection();
	        		
	        		boolean insert_advisor_result = dbmgr.insertAdvisorQuery(user, advisor);
	        		if(insert_advisor_result) {
	        			dbmgr.closeConnection();
    					//everything succeeded
						//send email with password --------------------------------
						//sendSDKEmail(user); //comment out when NAT Gateway disabled
    					responseJson = formResponse("Success", true, 200); //ok
	        		}else {
	        			responseJson = formResponse("Error", "Error registering advisor. DB error", 500); //internal server error
	        			dbmgr.closeConnection();
	        		}
				 }else {
					 responseJson = formResponse("Error", "Input validation failed, please use valid data", 400); //bad request
				 }
				 
			 }else {
				 responseJson = formResponse("Error", "Expected request body arguments not found", 400); //bad request
			 }
			 
		 }catch(Exception ex) {
			 responseJson = formResponse("Error", ex.toString(), 500); //internal server error
		 }
		 return responseJson;
	}
	
	private boolean checkRequestBody(JSONObject event) {
		return (event.containsKey("department") && event.containsKey("name") && event.containsKey("email"));
	}
	
	private boolean validateRequestBody(JSONObject event) {
		return (
				//check if 'department' is null first. If it is, fail because it shouldn't be. Otherwise validate
				((event.get("department") == null) ? false:Util.validateDepartment((String) event.get("department")))
				//check if 'name' is null first. If it is, fail because it shouldn't be. Otherwise validate.
				&& ((event.get("name") == null) ? false:Util.validateName((String) event.get("name")))
				//check if 'email' is null first. If it is, fail, because it shouldn't be. Otherwise validate
				&& ((event.get("email") == null) ? false:Util.validateEmail((String) event.get("email")))
				);
	}
	
	//for errors - where msg is type String
	private JSONObject formResponse(String label, String msg, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        headerJson.put("custom-header", "my custom header value");
        responseBody.put(label, msg);
    	responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
	
	//for success where msg is type boolean
	private JSONObject formResponse(String label, boolean msg, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        headerJson.put("custom-header", "my custom header value");
        responseBody.put(label, msg);
    	responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
	
	private void sendSDKEmail(User user) throws Exception{
		String email_subject = "MavAppoint New Account Info";
	    String email_body = String.join(
    	    System.getProperty("line.separator"),
    	    "Hello UTA student,",
    	    " ",
            "You recently created a MavAppoint account.",
            "Here is your randomly generated password:",
            " ",
            user.getPassword(),
            " ",
            "This message was sent automatically by Amazon's Simple Email Service",
            " ",
            "The University of Texas at Arlington",
            "MavAppoint System"
    	);
	    
        Destination destination = new Destination().withToAddresses(new String[]{user.getEmail()});

        Content subject = new Content().withData(email_subject);
        Content textBody = new Content().withData(email_body);
        Body body = new Body().withText(textBody);
        Message message = new Message().withSubject(subject).withBody(body);

        SendEmailRequest request = new SendEmailRequest().withSource(email_from).withDestination(destination).withMessage(message);

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(aws_cred_public, aws_cred_secret);

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion("us-west-2")
            .build();

        // Send the email.
        client.sendEmail(request);

	}

}
