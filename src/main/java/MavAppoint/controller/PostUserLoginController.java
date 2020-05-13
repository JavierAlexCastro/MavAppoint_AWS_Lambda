package MavAppoint.controller;

import java.sql.ResultSet;

import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.User;
import MavAppoint.model.UserAdvisor;
import MavAppoint.model.UserStudent;
import MavAppoint.password.HashPassword;
import MavAppoint.util.Util;

public class PostUserLoginController {
	
	public PostUserLoginController() {
		
	}
	
	public JSONObject login(JSONObject event) {
		JSONObject responseJson = new JSONObject();
		
		try {
			if(checkRequestBody(event)) {
				String login_email = (String) event.get("email");
				String login_pass = HashPassword.hashpass((String) event.get("password"));
				
				if(Util.validateEmail(login_email)) {
					DBManager dbmgr = DBManager.getInstance();
					dbmgr.createConnection();
					dbmgr.createStatement();
					ResultSet resultSetLogin = dbmgr.getUserPasswordQuery(login_email);
					
					if(resultSetLogin.next()) {
						if(login_pass.equals(resultSetLogin.getString("password"))) {
							//successful login
							resultSetLogin.close();
							//retrieve user -----------
							//maybe it is best to do this from the beginning instead of simply
							//	retrieving the password and then retrieving the user
							ResultSet resultSetUser = dbmgr.getUserFromEmailQuery(login_email); //email is unique attribute in DB so this works fine
							if(resultSetUser.next()) {
		            			User user = new User(resultSetUser);
		            			resultSetUser.close();
		            			dbmgr.closeStatement();
		            			//retrieve student -----------
		            			if(user.getRole().equals("student")) {
		            				ResultSet resultSetStudent = dbmgr.getStudentFromIdQuery(user.getUser_id());
		            				if(resultSetStudent.next()) {
		            					UserStudent student = new UserStudent(resultSetStudent);
		            					resultSetStudent.close();
		            					
		            					responseJson = formResponse(user, student, 200); //ok
										dbmgr.closePreparedStatement();
										dbmgr.closeConnection();
		            				}else {
		            					responseJson = formResponse("Error", "Query for retrieving student info failed", 500); //internal server error
										dbmgr.closePreparedStatement();
										dbmgr.closeConnection();
		            				}
		            			//retireve advisor ------------
		            			}else if(user.getRole().equals("advisor")) {
		            				ResultSet resultSetAdvisor = dbmgr.getAdvisorFromIdQuery(user.getUser_id());
		            				if(resultSetAdvisor.next()) {
		            					UserAdvisor advisor = new UserAdvisor();
		            					advisor.populateAdvisorForLogin(resultSetAdvisor);
		            					resultSetAdvisor.close();
		            					
		            					responseJson = formResponse(user, advisor, 200); //ok
										dbmgr.closePreparedStatement();
										dbmgr.closeConnection();
		            				}else {
		            					responseJson = formResponse("Error", "Query for retrieving advisor info failed", 500); //internal server error
										dbmgr.closePreparedStatement();
										dbmgr.closeConnection();
		            				}
								//admin role - user info only -------------
		            			}else if(user.getRole().equals("admin")) {
		            				responseJson = formResponse(user, 200); //ok
		            				dbmgr.closeConnection();
		            			//unknown role
		            			}else {
		            				responseJson = formResponse("Error", "Invalid role - not student, not advisor, not admin", 500); //internal server error
		            				dbmgr.closeStatement();
									dbmgr.closeConnection();
		            			}
							}else {
								responseJson = formResponse("Error", "Query for retrieving user info failed", 500); //internal server error
								dbmgr.closeStatement();
								dbmgr.closeConnection();
							}
						}else {
							responseJson = formResponse("Error", "Credentials do not match", 409); //conflict
							dbmgr.closeStatement();
							dbmgr.closeConnection();
						}
					}else {
						responseJson = formResponse("Error", "Credentials do not match", 409); //conflict
						//responseJson = formResponse("Error", "Query for credentials check failed", 500); //internal server error
						dbmgr.closeStatement();
						dbmgr.closeConnection();
					}
				}else {
					responseJson = formResponse("Error", "Invalid email - Email is not in the correct format", 400); //bad request
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
		return (event.get("email") != null && event.get("password") != null);
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
	
	//for success where role is Student
	private JSONObject formResponse(User user, UserStudent student, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        headerJson.put("custom-header", "my custom header value");
        responseBody.put("id", user.getUser_id());
        responseBody.put("email", user.getEmail());
        responseBody.put("role", user.getRole());
        responseBody.put("validated", user.getValidated());
        responseBody.put("notification", user.getNotification());
        responseBody.put("student_id", student.getStudent_id());
        responseBody.put("degree_type", student.getDegree_type());
        responseBody.put("phone", student.getPhone());
        responseBody.put("last_name_initial", student.getLast_name_initial());
    	responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
	
	//for success where role is Advisor
	private JSONObject formResponse(User user, UserAdvisor advisor, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        headerJson.put("custom-header", "my custom header value");
        responseBody.put("id", user.getUser_id());
        responseBody.put("email", user.getEmail());
        responseBody.put("role", user.getRole());
        responseBody.put("validated", user.getValidated());
        responseBody.put("notification", user.getNotification());
        responseBody.put("pName", advisor.getpName());
        responseBody.put("advisor_notification", advisor.getNotification());
        responseBody.put("name_low", advisor.getName_low());
        responseBody.put("name_high", advisor.getName_high());
        responseBody.put("name_low", advisor.getName_low());
        responseBody.put("degree_types", advisor.getDegree_types());
        responseBody.put("lead_status", advisor.getLead_status());
    	responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
	
	//for success where role is admin
	private JSONObject formResponse(User user, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        headerJson.put("custom-header", "my custom header value");
        responseBody.put("id", user.getUser_id());
        responseBody.put("email", user.getEmail());
        responseBody.put("role", user.getRole());
        responseBody.put("validated", user.getValidated());
        responseBody.put("notification", user.getNotification());
    	responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
}
