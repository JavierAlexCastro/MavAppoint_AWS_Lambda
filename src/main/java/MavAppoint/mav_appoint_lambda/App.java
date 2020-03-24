package MavAppoint.mav_appoint_lambda;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import MavAppoint.model.User;
import MavAppoint.model.UserStudent;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class App implements RequestStreamHandler {
	private JSONParser parser = new JSONParser();
	private static final String db_host = System.getenv("DB_HOST");
	private static final String db_uname = System.getenv("DB_USER");
	private static final String db_pass = System.getenv("DB_PASS");
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
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	Connection conn = DriverManager.getConnection(url, db_uname, db_pass);
            Statement stmt = conn.createStatement();
            logger.log("Connected to DB");
            
    		ResultSet resultSet = stmt.executeQuery("SELECT * FROM department");
    		
    		ArrayList<String> list = new ArrayList<String>();
    		int row_count = 0;
    		while(resultSet.next()) {
    			list.add(resultSet.getString("name"));
    			row_count += 1;
    		}
    		if(row_count == 0) {
    			responseBody.put("RequestError", "No departments in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			logger.log("Query result sucess");
    			String json_list = new Gson().toJson(list);
        		responseBody.put("list", json_list);
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 200); //ok
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}
            resultSet.close();
            stmt.close();
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
    
    public void getMajors(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda get Majors - ");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	Connection conn = DriverManager.getConnection(url, db_uname, db_pass);
            Statement stmt = conn.createStatement();
            logger.log("Connected to DB");
            
    		ResultSet resultSet = stmt.executeQuery("SELECT * FROM major");
    		
    		ArrayList<String[]> list = new ArrayList<String[]>();
    		int row_count = 0;
    		while(resultSet.next()) {
    			String[] inner_dept_array = {"", ""};
    			inner_dept_array[0] = resultSet.getString("name");
    			inner_dept_array[1] = resultSet.getString("dep_name");
    			list.add(inner_dept_array);
    			row_count += 1;
    		}
    		if(row_count == 0) {
    			responseBody.put("RequestError", "No departments in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			logger.log("Query result sucess");
    			String json_list = new Gson().toJson(list);
        		responseBody.put("list", json_list);
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 200); //ok
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}
            resultSet.close();
            stmt.close();
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
    
    public void getDegrees(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    	LambdaLogger logger = context.getLogger();
        logger.log("Invoked mav-appoint-lambda get Degrees - ");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	Connection conn = DriverManager.getConnection(url, db_uname, db_pass);
            Statement stmt = conn.createStatement();
            logger.log("Connected to DB");
            
    		ResultSet resultSet = stmt.executeQuery("SELECT * FROM degree_type");
    		
    		ArrayList<String> list = new ArrayList<String>();
    		int row_count = 0;
    		while(resultSet.next()) {
    			list.add(resultSet.getString("name"));
    			row_count += 1;
    		}
    		if(row_count == 0) {
    			responseBody.put("RequestError", "No degree types in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			logger.log("Query result sucess");
    			String json_list = new Gson().toJson(list);
        		responseBody.put("list", json_list);
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 200); //ok
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}
            resultSet.close();
            stmt.close();
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
    
	public void postUser(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
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
                                		
                                		//send email with password
                                		
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
    
    private int getIntFromObject(Object object) {
    	Long temp_long;
    	temp_long = (Long) object;
    	return temp_long.intValue();
    }
}
