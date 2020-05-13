package MavAppoint.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.Appointment;
import MavAppoint.model.User;
import MavAppoint.model.UserAdvisor;
import MavAppoint.model.UserStudent;
import MavAppoint.util.Util;

public class PostAppointmentController {
	
	public PostAppointmentController() {
		
	}
	
	public JSONObject registerStudent(JSONObject event) {
		JSONObject responseJson = new JSONObject();
        
        try {
        	if(checkRequestBody(event)) {
        		
        		if(validateRequestBody(event)) {
	        		
	        		DBManager dbmgr = DBManager.getInstance();
	        		dbmgr.createConnection();
	        		
	        		UserAdvisor advisor = new UserAdvisor(((Long) event.get("advisor_user_id")).intValue());
	        		User user = new User(((Long) event.get("student_user_id")).intValue(), (String) event.get("student_email"));
	        		UserStudent student = new UserStudent(((Long) event.get("student_user_id")).intValue(), (String) event.get("student_id"), 
	        				(String) event.get("student_phone"));
	        		try {
	        			Appointment appointment = new Appointment((String) event.get("type"), ((Long) event.get("duration")).intValue(), 
		        				(String) event.get("date"), (String) event.get("start_time"), (String) event.get("end_time"),
		        				(String) event.get("description"), user, advisor, student, (JSONArray) event.get("time_slots"));
	        			
	        			//before inserting it might be a good idea to also check if appointment.timeslots are available in the first place
	        			//	that is, they don't have a studentId associated with them in the 'advising_schedule' table. Don't trust the user.
	        			
	        			//insert appointment --------------------------------
		        		boolean insert_appointment_result = dbmgr.insertAppointmentQuery(appointment);
		        		if(insert_appointment_result) {
		        			dbmgr.closeConnection();
		        			responseJson = formResponse("Success", true, 200); //ok
		        		}else {
		        			responseJson = formResponse("Error", "Error creating appointment", 500); //internal server error
		        			dbmgr.closeConnection();
		        		}
	        		}catch(Exception ex) {
	        			responseJson = formResponse("Error", ex.toString(), 500); //internal server error
	        			dbmgr.closeConnection();
	        		}	        		
	        	}else {
	        		//generic error message. Should be better ideally
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
		return (event.containsKey("type")&& event.containsKey("duration") && event.containsKey("advisor_user_id")
        			&& event.containsKey("student_user_id") && event.containsKey("date") && event.containsKey("start_time")
        			&& event.containsKey("end_time") && event.containsKey("description") && event.containsKey("student_id")
        			&& event.containsKey("student_email") && event.containsKey("student_phone") && event.containsKey("time_slots"));
		
	}
	
	private boolean validateRequestBody(JSONObject event) {
		return ( 
				//check if 'student_user_id' is null first. If it isn't validate
				((event.get("student_user_id") == null) ? true:Util.validateUserId(String.valueOf((Long) event.get("student_user_id"))))
				//check if 'type' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("type") == null) ? false:Util.validateAppType((String)event.get("type")))
				//check if 'duration' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("duration") == null) ? false:Util.validateDuration(String.valueOf((Long) event.get("duration"))))
				//check if 'advisor_user_id' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("advisor_user_id") == null) ? false:Util.validateUserId(String.valueOf((Long) event.get("advisor_user_id"))))
				//check if 'date' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("date") == null) ? false:Util.validateDate((String)event.get("date")))
				//check if 'start_time' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("start_time") == null) ? false:Util.validateTime((String)event.get("start_time")))
				//check if 'end_time' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("end_time") == null) ? false:Util.validateTime((String)event.get("end_time")))
				//check if 'description' is null first. If it isn't validate
				&& ((event.get("description") == null) ? true:Util.validateDescription((String)event.get("description")))
				//check if 'student_id' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("student_id") == null) ? false:Util.validateStudentId((String)event.get("student_id")))
				//check if 'student_email' is null first. If it isn't validate
				&& ((event.get("student_email") == null) ? true:Util.validateEmail((String)event.get("student_email")))
				//check if 'student_phone' is null first. If it isn't validate
				&& ((event.get("student_phone") == null) ? true:Util.validatePhoneNumber((String)event.get("student_phone")))
				//check if 'time_slots' is null first. If it is, fail validation because it shouldn't be
				&& ((event.get("time_slots") == null) ? false:Util.validateTimeSlots((JSONArray)event.get("time_slots")))
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
	

}
