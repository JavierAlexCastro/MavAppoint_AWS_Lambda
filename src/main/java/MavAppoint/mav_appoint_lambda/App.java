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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import MavAppoint.controller.GetDegreeController;
import MavAppoint.controller.GetDepartmentController;
import MavAppoint.controller.GetMajorController;
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
	private static final String db_host = System.getenv("DB_HOST");
	private static final String db_uname = System.getenv("DB_USER");
	private static final String db_pass = System.getenv("DB_PASS");
	private static final String email_from = System.getenv("EMAIL_FROM");
	private static final String email_pass = System.getenv("EMAIL_PASS");
	private static final String email_from_name = "MavAppoint";
	private static final String smtp_user = System.getenv("SMTP_USER");
	private static final String smtp_pass = System.getenv("SMTP_PASS");
	private static final String smtp_host = System.getenv("SMTP_HOST");
	private static final String smtp_port = System.getenv("SMTP_PORT");
	private static final String aws_cred_public = System.getenv("AWS_CRED_PUBLIC");
	private static final String aws_cred_secret = System.getenv("AWS_CRED_SECRET");
	
	private String url = "jdbc:mysql://" + db_host;

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda in JAVA code - ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	Connection conn = DriverManager.getConnection(url, db_uname, db_pass);
            Statement stmt = conn.createStatement();
            logger.log("Connected to DB");
            
            JSONObject event = (JSONObject) parser.parse(reader);
            
        	if (event.get("id") != null) {
        		logger.log("User ID is not null");
        		int userId = getIntFromObject(event.get("id"));
        		ResultSet resultSet = stmt.executeQuery("SELECT * FROM user where userId="+userId);
        		if(resultSet.next()) {
        			logger.log("Query result sucess");
        			String email = resultSet.getString("email");
            		String role = resultSet.getString("role");
            		logger.log("Successfully executed query.  Email: " + email);
            		logger.log("Successfully executed query.  Role: " + role);
            		
            		responseBody.put("email", email);
            		responseBody.put("role", role);
                    headerJson.put("custom-header", "my custom header value");
                    responseJson.put("isBase64Encoded", false);
                    responseJson.put("statusCode", 200); //ok
                    responseJson.put("headers", headerJson);
                    responseJson.put("body", responseBody);
        		}else {
        			responseBody.put("RequestError", "No records for provided id");
                    headerJson.put("custom-header", "my custom header value");
                    responseJson.put("isBase64Encoded", false);
                    responseJson.put("statusCode", 400); //bad request
                    responseJson.put("headers", headerJson);
                    responseJson.put("body", responseBody);
        		}
        		resultSet.close();
        		stmt.close();
        		conn.close();
        	}else {
        		responseBody.put("RequestError", "Expected id attribute, but not found");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
        	}
            //}
        }catch(Exception ex) {
        	headerJson.put("custom-header", "my custom header value");
            responseBody.put("ServerError", ex.toString());
        	responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", 500); //internal server error
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody);
        }
        
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }

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
        logger.log("Invoked mav-appoint-lambda in JAVA code - ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        try {
        	Connection conn = DriverManager.getConnection(url, db_uname, db_pass);
            logger.log("Connected to DB");
            
            JSONObject event = (JSONObject) parser.parse(reader);
            
        	if (event.get("department") != null && event.get("degree") != null && event.get("major") != null && event.get("initial") != null
        			&& event.get("student_id") != null && event.get("phone") != null && event.get("email") != null) {
        		logger.log("parameters are not null");
        		//create user
        		User user = new User((String) event.get("email"), "student"); //hardcoding user created to always be student for our usecase
        		
        		//---------------- Insert User
        		String user_sql = "INSERT INTO `user` (email, password, role, validated, notification) VALUES (?,?,?,?,?)";
        		PreparedStatement stmtUser = conn.prepareStatement(user_sql);
        		stmtUser.setString(1, user.getEmail());
        		stmtUser.setString(2, user.getHashedPassword());
        		stmtUser.setString(3, user.getRole());
        		stmtUser.setInt(4, user.getValidated());
        		stmtUser.setString(5, user.getNotification());
        		int result_user = stmtUser.executeUpdate();
        		
        		if(result_user > 0) {
        			logger.log("Query for user insertion was a sucess");
            		logger.log("Successfully executed query.  Email: " + user.getEmail());
            		logger.log("Successfully executed query.  Pass: " + user.getPassword());
            		logger.log("Successfully executed query.  Hashed: " + user.getHashedPassword());
            		
            		String degree = (String) event.get("degree");
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
            		
            		//create student with placeholder id value 0
            		UserStudent student = new UserStudent(0,(String)event.get("student_id"), degree_type, (String)event.get("phone"), (String)event.get("initial"));
  
            		//---------------- Retrieve User.id for Student insertion
            		Statement stmtUserId = conn.createStatement();
            		ResultSet resultSetUserId = stmtUserId.executeQuery("SELECT userId FROM `user` WHERE email='"+user.getEmail()+"\'");
            		
            		if(resultSetUserId.next()) {
            			logger.log("Query for user id retrieval was a sucess");
                		logger.log("Successfully executed query.  ID: " + String.valueOf(resultSetUserId.getInt("userId")));
                		logger.log("Successfully executed query.  Email: " + user.getEmail());
                		
                		student.setId(resultSetUserId.getInt("userId"));
                		
                		//---------------- Insert Student
                		String student_sql = "INSERT INTO `user_student` (userId, student_Id, degree_type, phone_num, last_name_initial) VALUES (?,?,?,?,?)";
                		PreparedStatement stmtUserStudent = conn.prepareStatement(student_sql);
                		stmtUserStudent.setInt(1, student.getId());
                		stmtUserStudent.setString(2, student.getStudent_id());
                		stmtUserStudent.setInt(3, student.getDegree_type());
                		stmtUserStudent.setString(4, student.getPhone());
                		stmtUserStudent.setString(5, student.getLast_name_initial());
                		int result_student = stmtUserStudent.executeUpdate();
                		
                		if(result_student > 0) {
                			logger.log("Query for student insertion was a sucess");
                    		logger.log("Successfully executed query.  ID: " + student.getStudent_id());
                    		logger.log("Successfully executed query.  Phone: " + student.getPhone());
                    		
                    		//---------------- Insert Department User
                    		String department_sql = "INSERT INTO `department_user` (name, userId) VALUES (?,?)";
                    		PreparedStatement stmtDepartmentUser = conn.prepareStatement(department_sql);
                    		stmtDepartmentUser.setString(1, (String) event.get("department"));
                    		stmtDepartmentUser.setInt(2, student.getId());
                    		int result_department = stmtDepartmentUser.executeUpdate();
                    		
                    		if(result_department > 0) {
                    			logger.log("Query for department user insertion was a sucess");
                        		logger.log("Successfully executed query.  Dept: " + (String) event.get("department"));
                        		
                        		//---------------- Insert Degree Type User
                        		String degree_sql = "INSERT INTO `degree_type_user` (name, userId) VALUES (?,?)";
                        		PreparedStatement stmtDegreeUser = conn.prepareStatement(degree_sql);
                        		stmtDegreeUser.setString(1, degree);
                        		stmtDegreeUser.setInt(2, student.getId());
                        		int result_degree = stmtDegreeUser.executeUpdate();
                        		if(result_degree > 0) {
                        			logger.log("Query for degree user insertion was a sucess");
                            		logger.log("Successfully executed query.  Degree: " + (String) event.get("degree"));
                            		
                            		//---------------- Insert Major User
                            		String major_sql = "INSERT INTO `major_user` (name, userId) VALUES (?,?)";
                            		PreparedStatement stmtMajorUser = conn.prepareStatement(major_sql);
                            		stmtMajorUser.setString(1, (String) event.get("major"));
                            		stmtMajorUser.setInt(2, student.getId());
                            		int result_major = stmtMajorUser.executeUpdate();
                            		
                            		if(result_major > 0) {
                            			logger.log("Query for major user insertion was a sucess");
                                		logger.log("Successfully executed query.  Major: " + (String) event.get("major"));
                                		//All queries succeeded
                                		try {
                                			sendSDKEmail(user);
                                		}catch(Exception ex) {
                                			logger.log("SES Error:" + ex.toString());
                                		}
//                                		AWSLambdaClient lambdaClient = new AWSLambdaClient();
//                                		JSONObject requestBody = new JSONObject();
//                                        requestBody.put("pass", user.getPassword());
//                                		try {
//                                            InvokeAsyncRequest invokeRequest = new InvokeAsyncRequest();
//                                            invokeRequest.setFunctionName("SendEmailPassword");
//                                            invokeRequest.setInvokeArgs(requestBody.toString());
//                                            //invokeRequest.setPayload(user.getPassword());
//
//                                            context.getLogger().log("Before Invoke");
//                                            lambdaClient.invokeAsync(invokeRequest);
//                                        } catch (Exception e) {
//                                            // TODO: handle exception
//                                        }

                                		//start send email segment ---------------------
//                                		// Recipient's email ID needs to be mentioned.
//                                        String to = "javieralexcastro95@gmail.com";
//
//                                        // Sender's email ID needs to be mentioned
//                                        String from = email_from;
//
//                                        // Assuming you are sending email from through gmails smtp
//                                        String host = "smtp.gmail.com";
//
//                                        // Get system properties
//                                        Properties properties = System.getProperties();
//
//                                        // Setup mail server
//                                        properties.put("mail.smtp.host", host);
//                                        properties.put("mail.smtp.port", "465");
//                                        properties.put("mail.smtp.ssl.enable", "true");
//                                        properties.put("mail.smtp.auth", "true");
//
//                                        // Get the Session object.// and pass username and password
//                                        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//
//                                            protected PasswordAuthentication getPasswordAuthentication() {
//
//                                                return new PasswordAuthentication(email_from, email_pass);
//
//                                            }
//
//                                        });
//
//                                        // Used to debug SMTP issues
//                                        session.setDebug(true);
//
//                                        try {
//                                            // Create a default MimeMessage object.
//                                            MimeMessage message = new MimeMessage(session);
//
//                                            // Set From: header field of the header.
//                                            message.setFrom(new InternetAddress(from));
//
//                                            // Set To: header field of the header.
//                                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//                                            // Set Subject: header field
//                                            message.setSubject("MavAppoint New Account Info");
//                                            
//                                            String email_body_html = "<html>"
//                                                    + "<head></head>"
//                                                    + "<body>"
//                                                    + "<h3>Hello UTA student,</h3>"
//                                                    + "<p>You recently created a MavAppoint account. <br /> "
//                                                    + "Here is your randomly generated password: <br />"
//                                                    + "<br />" + user.getPassword() + "<br />"
//                                                    + "<br />" + "The University of Texas at Arlington" + "<br />"
//                                                    + "MavAppoint System" + "</p>"
//                                                    + "</body>"
//                                                    + "</html>";
//
//                                            // Now set the actual message
//                                            //message.setText("This is actual message");
//                                            // Send the actual HTML message.
//                                            message.setContent(email_body_html, "text/html");
//
//                                            System.out.println("sending...");
//                                            // Send message
//                                            Transport.send(message);
//                                            System.out.println("Sent message successfully....");
//                                            logger.log("email sent");
//                                            //prepare response
//                                            responseBody.put("success", true);
//                                            headerJson.put("custom-header", "my custom header value");
//                                            responseJson.put("isBase64Encoded", false);
//                                            responseJson.put("statusCode", 201); //creation success
//                                            responseJson.put("headers", headerJson);
//                                            responseJson.put("body", responseBody);
//                                        } catch (MessagingException ex) {
//                                        	responseBody.put("RequestError", "Failed to send email");
//                                            headerJson.put("custom-header", "my custom header value");
//                                            responseJson.put("isBase64Encoded", false);
//                                            responseJson.put("statusCode", 500); //bad request
//                                            responseJson.put("headers", headerJson);
//                                            responseJson.put("body", responseBody);
//                                        }
                                        
                                        //finish send email segment ---------------
                                      //prepare response
                                        responseBody.put("success", true);
                                        headerJson.put("custom-header", "my custom header value");
                                        responseJson.put("isBase64Encoded", false);
                                        responseJson.put("statusCode", 201); //creation success
                                        responseJson.put("headers", headerJson);
                                        responseJson.put("body", responseBody);
                            		}else {
                            			responseBody.put("RequestError", "Query for major user insertion failed");
                                        headerJson.put("custom-header", "my custom header value");
                                        responseJson.put("isBase64Encoded", false);
                                        responseJson.put("statusCode", 400); //bad request
                                        responseJson.put("headers", headerJson);
                                        responseJson.put("body", responseBody);
                            		}
                            		stmtMajorUser.close();
                            		
                        		}else {
                        			responseBody.put("RequestError", "Query for degree user insertion failed");
                                    headerJson.put("custom-header", "my custom header value");
                                    responseJson.put("isBase64Encoded", false);
                                    responseJson.put("statusCode", 400); //bad request
                                    responseJson.put("headers", headerJson);
                                    responseJson.put("body", responseBody);
                        		}
                        		stmtDegreeUser.close();
                    		}else {
                    			responseBody.put("RequestError", "Query for department user insertion failed");
                                headerJson.put("custom-header", "my custom header value");
                                responseJson.put("isBase64Encoded", false);
                                responseJson.put("statusCode", 400); //bad request
                                responseJson.put("headers", headerJson);
                                responseJson.put("body", responseBody);
                    		}
                    		stmtDepartmentUser.close();
                		}else {
                			responseBody.put("RequestError", "Query for student insertion failed");
                            headerJson.put("custom-header", "my custom header value");
                            responseJson.put("isBase64Encoded", false);
                            responseJson.put("statusCode", 400); //bad request
                            responseJson.put("headers", headerJson);
                            responseJson.put("body", responseBody);
                		}
                		stmtUserStudent.close();
                		
            		}else {
            			responseBody.put("RequestError", "Query for user id retrieval failed");
                        headerJson.put("custom-header", "my custom header value");
                        responseJson.put("isBase64Encoded", false);
                        responseJson.put("statusCode", 400); //bad request
                        responseJson.put("headers", headerJson);
                        responseJson.put("body", responseBody);
            		}
            		resultSetUserId.close();
            		stmtUserId.close();
            		
        		}else {
        			responseBody.put("RequestError", "Query for user insertion failed");
                    headerJson.put("custom-header", "my custom header value");
                    responseJson.put("isBase64Encoded", false);
                    responseJson.put("statusCode", 400); //bad request
                    responseJson.put("headers", headerJson);
                    responseJson.put("body", responseBody);
        		}
        		stmtUser.close();
        	}else {
        		responseBody.put("RequestError", "Expected department, degree, major, initial, id, phone and email, but not found");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
        	}
        	conn.close();
        }catch(Exception ex) {
        	headerJson.put("custom-header", "my custom header value");
            responseBody.put("ServerError", ex.toString());
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
		System.out.println("Sending email to user: " + user.getEmail());
		String email_subject = "MavAppoint New Account Info";
	    String email_body = String.join(
	    	    //System.getProperty("line.separator"),
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
	    
	 // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(new String[]{user.getEmail()});

        // Create the subject and body of the message.
        Content subject = new Content().withData(email_subject);
        Content textBody = new Content().withData(email_body);
        Body body = new Body().withText(textBody);

        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);

        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(email_from).withDestination(destination).withMessage(message);

        try {
            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

            /*
             * The ProfileCredentialsProvider will return your [default]
             * credential profile by reading from the credentials file located at
             * (C:\\Users\\Javier\\.aws\\credentials).
             *
             * TransferManager manages a pool of threads, so we create a
             * single instance and share it throughout our application.
             */
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(aws_cred_public, aws_cred_secret);
            
            //ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
//            try {
//                credentialsProvider.getCredentials();
//            } catch (Exception e) {
//                throw new AmazonClientException(
//                        "Cannot load the credentials from the credential profiles file. " +
//                        "Please make sure that your credentials file is at the correct " +
//                        "location (C:\\Users\\Javier\\.aws\\credentials), and is in valid format.",
//                        e);
//            }

            // Instantiate an Amazon SES client, which will make the service call with the supplied AWS credentials.
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
                // access status, sending limits, and Amazon SES identity-related settings are specific to a given
                // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
                // the US East (N. Virginia) region. Examples of other regions that Amazon SES supports are US_WEST_2
                // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
                .withRegion("us-west-2")
                .build();

            // Send the email.
            client.sendEmail(request);
            System.out.println("Email sent!");

        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            ex.printStackTrace();
        }
	}
	
	public void sendPasswordEmail(String pass) {
//		final String password = pass;
//		new Thread(new Runnable() { 
//            public void run() 
//            { 
//            	// Recipient's email ID needs to be mentioned.
//                String to = "javieralexcastro95@gmail.com";
//
//                // Sender's email ID needs to be mentioned
//                String from = email_from;
//
//                // Assuming you are sending email from through gmails smtp
//                String host = "smtp.gmail.com";
//
//                // Get system properties
//                Properties properties = System.getProperties();
//
//                // Setup mail server
//                properties.put("mail.smtp.host", host);
//                properties.put("mail.smtp.port", "465");
//                properties.put("mail.smtp.ssl.enable", "true");
//                properties.put("mail.smtp.auth", "true");
//
//                // Get the Session object.// and pass username and password
//                Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//
//                    protected PasswordAuthentication getPasswordAuthentication() {
//
//                        return new PasswordAuthentication(email_from, email_pass);
//
//                    }
//
//                });
//
//                // Used to debug SMTP issues
//                session.setDebug(true);
//
//                try {
//                    // Create a default MimeMessage object.
//                    MimeMessage message = new MimeMessage(session);
//
//                    // Set From: header field of the header.
//                    message.setFrom(new InternetAddress(from));
//
//                    // Set To: header field of the header.
//                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//                    // Set Subject: header field
//                    message.setSubject("MavAppoint New Account Info");
//                    
//                    String email_body_html = "<html>"
//                            + "<head></head>"
//                            + "<body>"
//                            + "<h3>Hello UTA student,</h3>"
//                            + "<p>You recently created a MavAppoint account. <br /> "
//                            + "Here is your randomly generated password: <br />"
//                            + "<br />" + password + "<br />"
//                            + "<br />" + "The University of Texas at Arlington" + "<br />"
//                            + "MavAppoint System" + "</p>"
//                            + "</body>"
//                            + "</html>";
//
//                    // Now set the actual message
//                    //message.setText("This is actual message");
//                    // Send the actual HTML message.
//                    message.setContent(email_body_html, "text/html");
//
//                    System.out.println("sending...");
//                    // Send message
//                    Transport.send(message);
//                    System.out.println("Sent message successfully....");
//                } catch (MessagingException ex) {
//                	ex.printStackTrace();
//                }
//            } 
//        }).start(); 
	}
	
	public void sendPasswordEmail(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		
	
	}
    
    private int getIntFromObject(Object object) {
    	Long temp_long;
    	temp_long = (Long) object;
    	return temp_long.intValue();
    }
}
