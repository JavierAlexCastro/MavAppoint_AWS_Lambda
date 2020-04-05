package MavAppoint.mav_appoint_lambda;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import MavAppoint.controller.GetDegreeController;
import MavAppoint.controller.GetDepartmentController;
import MavAppoint.controller.GetMajorController;
import MavAppoint.controller.PostStudentController;
import MavAppoint.model.User;
import MavAppoint.model.UserStudent;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class App implements RequestStreamHandler {
	private JSONParser parser = new JSONParser();

    public void getDepartments(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda get Departments - ");
        JSONObject responseJson = new JSONObject();

        GetDepartmentController get_dept_controller = new GetDepartmentController();
        responseJson = get_dept_controller.getDepartments();
        
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
    
    public void getMajors(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda get Majors - ");
        JSONObject responseJson = new JSONObject();
        
        GetMajorController get_major_controller = new GetMajorController();
        responseJson = get_major_controller.getMajors();
        
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
    
    public void getDegrees(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda get Degrees - ");
        JSONObject responseJson = new JSONObject();
        
        GetDegreeController get_degree_controller = new GetDegreeController();
        responseJson = get_degree_controller.getDegrees();
        
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
    
	public void postUserStudent(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda post Student - ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
			JSONObject event = (JSONObject) parser.parse(reader);
			PostStudentController post_student_controller = new PostStudentController();
			responseJson = post_student_controller.registerStudent(event);
		} catch (ParseException ex) {
			headerJson.put("custom-header", "my custom header value");
			responseBody.put("Error", ex.toString());
			responseJson.put("isBase64Encoded", false);
			responseJson.put("statusCode", 500); //internal server error
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody);
		}
        
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
	
	public void sendSMTPEmail(User user) throws Exception{
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
	}
	
	public void sendSDKEmail(User user) throws Exception{
//		System.out.println("Sending email to user: " + user.getEmail());
//		String email_subject = "MavAppoint New Account Info";
//	    String email_body = String.join(
//	    	    //System.getProperty("line.separator"),
//	    	    "Hello UTA student,",
//	    	    " ",
//                "You recently created a MavAppoint account.",
//                "Here is your randomly generated password:",
//                " ",
//                user.getPassword(),
//                " ",
//                "This message was sent automatically by Amazon's Simple Email Service",
//                " ",
//                "The University of Texas at Arlington",
//                "MavAppoint System"
//	    	);
//	    
//	 // Construct an object to contain the recipient address.
//        Destination destination = new Destination().withToAddresses(new String[]{user.getEmail()});
//
//        // Create the subject and body of the message.
//        Content subject = new Content().withData(email_subject);
//        Content textBody = new Content().withData(email_body);
//        Body body = new Body().withText(textBody);
//
//        // Create a message with the specified subject and body.
//        Message message = new Message().withSubject(subject).withBody(body);
//
//        // Assemble the email.
//        SendEmailRequest request = new SendEmailRequest().withSource(email_from).withDestination(destination).withMessage(message);
//
//        try {
//            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
//
//            /*
//             * The ProfileCredentialsProvider will return your [default]
//             * credential profile by reading from the credentials file located at
//             * (C:\\Users\\Javier\\.aws\\credentials).
//             *
//             * TransferManager manages a pool of threads, so we create a
//             * single instance and share it throughout our application.
//             */
//            BasicAWSCredentials awsCreds = new BasicAWSCredentials(aws_cred_public, aws_cred_secret);
//            
//            //ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
////            try {
////                credentialsProvider.getCredentials();
////            } catch (Exception e) {
////                throw new AmazonClientException(
////                        "Cannot load the credentials from the credential profiles file. " +
////                        "Please make sure that your credentials file is at the correct " +
////                        "location (C:\\Users\\Javier\\.aws\\credentials), and is in valid format.",
////                        e);
////            }
//
//            // Instantiate an Amazon SES client, which will make the service call with the supplied AWS credentials.
//            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
//                // access status, sending limits, and Amazon SES identity-related settings are specific to a given
//                // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
//                // the US East (N. Virginia) region. Examples of other regions that Amazon SES supports are US_WEST_2
//                // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
//                .withRegion("us-west-2")
//                .build();
//
//            // Send the email.
//            client.sendEmail(request);
//            System.out.println("Email sent!");
//
//        } catch (Exception ex) {
//            System.out.println("The email was not sent.");
//            System.out.println("Error message: " + ex.getMessage());
//            ex.printStackTrace();
//        }
	}

	
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
