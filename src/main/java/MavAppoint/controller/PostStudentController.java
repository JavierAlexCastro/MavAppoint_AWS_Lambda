package MavAppoint.controller;

import java.sql.ResultSet;

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
import MavAppoint.model.UserStudent;

public class PostStudentController {
	private static String aws_cred_public;
	private static String aws_cred_secret;
	private static String email_from;
	
	public PostStudentController() {
		PostStudentController.aws_cred_public = System.getenv("AWS_CRED_PUBLIC");
		PostStudentController.aws_cred_secret = System.getenv("AWS_CRED_SECRET");
		PostStudentController.email_from = System.getenv("EMAIL_FROM");
	}
	
	public JSONObject registerStudent(JSONObject event) {
		JSONObject responseJson = new JSONObject();
        
        try {
        	if(checkRequestBody(event)) {
        		User user = new User((String) event.get("email"), "student");
        		
        		DBManager dbmgr = DBManager.getInstance();
        		dbmgr.createConnection();
        		
        		//insert user --------------------------------
        		int insert_user_result = dbmgr.insertUserQuery(user);
        		if(insert_user_result > 0) {
        			dbmgr.closePreparedStatement();
        			String degree = (String) event.get("degree");
            		int degree_type = getDegreeType(degree);
            		UserStudent student = new UserStudent(0,(String)event.get("student_id"), degree_type, (String)event.get("phone"), (String)event.get("initial"));
            		
            		//get user id --------------------------------
            		dbmgr.createStatement();
            		ResultSet resultSetUserId = dbmgr.getUserId(user.getEmail());
            		if(resultSetUserId.next()) {
            			student.setId(resultSetUserId.getInt("userId"));
            			dbmgr.closeResultSet();
            			dbmgr.closeStatement();
            			
            			//insert student --------------------------------
            			int insert_student_result = dbmgr.insertUserStudentQuery(student);
            			if(insert_student_result > 0) {
            				dbmgr.closePreparedStatement();
            				
            				//insert department user --------------------------------
            				int insert_dept_result = dbmgr.insertDepartmentUserQuery((String) event.get("department"), student.getId());
            				if(insert_dept_result > 0) {
            					dbmgr.closePreparedStatement();
            					
            					//insert degree user --------------------------------
            					int insert_degree_result = dbmgr.insertDegreeTypeUserQuery(degree, student.getId());
            					if(insert_degree_result > 0) {
            						dbmgr.closePreparedStatement();
            						
            						//insert major user --------------------------------
            						int insert_major_result = dbmgr.insertMajorUserQuery((String) event.get("major"), student.getId());
            						if(insert_major_result > 0) {
            							dbmgr.closePreparedStatement();
            							dbmgr.closeConnection();
            							
            							//everything succeeded
            							//send email with password --------------------------------
            							sendSDKEmail(user); //comment out when NAT Gateway disabled
            							responseJson = formResponse("Success", true, 200); //ok
            							
            						}else {
            							responseJson = formResponse("Error", "Query for major user insertion failed", 500); //internal server error
                    					dbmgr.closeConnection();
            						}
            					}else {
            						responseJson = formResponse("Error", "Query for degree type user insertion failed", 500); //internal server error
                					dbmgr.closeConnection();
            					}
            				}else {
            					responseJson = formResponse("Error", "Query for department user insertion failed", 500); //internal server error
            					dbmgr.closeConnection();
            				}
            			}else {
            				responseJson = formResponse("Error", "Query for student insertion failed", 500); //internal server error
            				dbmgr.closeConnection();
            			}
            		}else {
            			responseJson = formResponse("Error", "Query for user id retrieval failed", 500); //internal server error
            			dbmgr.closeConnection();
            		}
        		}else {
        			responseJson = formResponse("Error", "Query for user insertion failed", 500); //internal server error
        			dbmgr.closeConnection();
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
		return (event.get("department") != null && event.get("degree") != null && event.get("major") != null && event.get("initial") != null
        			&& event.get("student_id") != null && event.get("phone") != null && event.get("email") != null);
		
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
	
	//works for now, but not safe as this values are being hardcoded
	private int getDegreeType(String degree) {
		int degree_type;
		if(degree.equals("Bachelors")) {
			degree_type = 1;
		}else if(degree.equals("Masters")) {
			degree_type = 2;
		}else if(degree.equals("Doctorate")) {
			degree_type = 3;
		}else {
			degree_type = -1;
		}
		
		return degree_type;
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
	
//	public void sendSMTPEmail(User user) throws Exception{
//		System.out.println("Sending email to user: " + user.getEmail());
//		String email_subject = "MavAppoint New Account Info";
//	    String email_body = String.join(
//	    	    System.getProperty("line.separator"),
//	    	    "<html>",
//                "<head></head>",
//                "<body>",
//                "<h3>Hello UTA student,</h3>",
//                "<p>You recently created a MavAppoint account. <br /> ",
//                "Here is your randomly generated password: <br />",
//                "<br />",
//                user.getPassword(),
//                "<br />",
//                "<br />",
//                "The University of Texas at Arlington",
//                "<br />",
//                "MavAppoint System",
//                "<ber />",
//                "This message was sent automatically by Amazon's Simple Email Service",
//                "</p>",
//                "</body>",
//                "</html>"
//	    	);
//	    // Create a Properties object to contain connection configuration information.
//	    Properties props = System.getProperties();
//    	props.put("mail.transport.protocol", "smtps");
//    	props.put("mail.smtps.port", smtp_port);
//    	props.put("mail.smtp.ssl.enable", "true");
//    	props.put("mail.smtps.auth", "true");
//    	props.put("mail.debug", "true");
//    	
//
//    	
//    	// Create a Session object to represent a mail session with the specified properties. 
//    	Session session = Session.getDefaultInstance(props);
//    	
//    	// Used to debug SMTP issues
//        session.setDebug(true);
//
//        // Create a message with the specified information. 
//        MimeMessage msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress(email_from, email_from_name));
//        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//        msg.setSubject(email_subject);
//        msg.setContent(email_body,"text/html");
//        
//        // Create a transport.
//        Transport transport = session.getTransport("smtps");
//                    
//        // Send the message.
//        try
//        {
//            System.out.println("Sending email....");
//            System.out.println("Transport isConnected: " + transport.isConnected());
//            // Connect to Amazon SES using the SMTP username and password you specified above.
//            transport.connect(smtp_host, smtp_user, smtp_pass);
//            System.out.println("Connected...");
//            // Send the email.
//            System.out.println("Recipient count" + msg.getAllRecipients().length);
//            transport.sendMessage(msg, msg.getAllRecipients());
//            System.out.println("Email sent!");
//        }
//        catch (Exception ex) {
//            System.out.println("The email was not sent.");
//            System.out.println("Error message: " + ex.getMessage());
//            throw ex;
//        }
//        finally
//        {
//            // Close and terminate the connection.
//            transport.close();
//        }
//	}

}
