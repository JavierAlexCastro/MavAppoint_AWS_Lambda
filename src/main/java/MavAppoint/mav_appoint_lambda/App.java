package MavAppoint.mav_appoint_lambda;

import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import MavAppoint.controller.GetDegreeController;
import MavAppoint.controller.GetDepartmentController;
import MavAppoint.controller.GetMajorController;
import MavAppoint.controller.PostAdvisorController;
import MavAppoint.controller.PostStudentController;
import MavAppoint.controller.PostUserLoginController;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

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

	public void postUserLogin(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda post User Login - ");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	JSONObject event = (JSONObject) parser.parse(reader);
        	PostUserLoginController post_user_login_controller = new PostUserLoginController();
        	responseJson = post_user_login_controller.login(event);
        }catch(ParseException ex) {
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

	public void postUserAdvisor(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda post Advisor - ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
			JSONObject event = (JSONObject) parser.parse(reader);
			PostAdvisorController post_advisor_controller = new PostAdvisorController();
			responseJson = post_advisor_controller.createAdvisor(event);
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
	
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
