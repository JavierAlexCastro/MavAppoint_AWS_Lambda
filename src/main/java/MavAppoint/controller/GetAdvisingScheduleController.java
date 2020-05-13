package MavAppoint.controller;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.AdvisingTimeSlot;
import MavAppoint.model.UserAdvisor;

public class GetAdvisingScheduleController {
	
	public GetAdvisingScheduleController() {
		
	}
	
	public JSONObject getSchedule(JSONObject event) {
		JSONObject responseJson = new JSONObject();
		try {
			if(checkRequestBody(event)) {
				SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = date_format.parse((String) event.get("date"));
				
				DBManager dbmgr = DBManager.getInstance();
				dbmgr.createConnection();
				
	        	ResultSet resultSet = dbmgr.getAdvisingScheduleQuery(date);
	        	ArrayList<AdvisingTimeSlot> advising_schedule = new ArrayList<AdvisingTimeSlot>(); //list of time slots
	        	ArrayList<UserAdvisor> advisor_list = new ArrayList<UserAdvisor>(); //list of advisors. No duplicates
	        	JSONArray advising_schedule_json = new JSONArray(); //json formatted list of time slots. Includes advisor info
	        	
	        	while(resultSet.next()) { //iterate over each timeslot
	        		JSONObject timeslot_json = new JSONObject(); //json formatted individual time slot
	        		AdvisingTimeSlot advising_time_slot = new AdvisingTimeSlot(resultSet); //individual time slot
	        		
	        		JSONObject advisor_json = getAdvisorJSON(advisor_list, advising_time_slot.getUserId()); //json formatted individual advisor
	        		if(advisor_json.isEmpty()) { //if advisor is not in the list of unique advisors
	        			ResultSet resultSetAdvisor = dbmgr.getAdvisorForTimeslotQuery(advising_time_slot.getUserId());
        				if(resultSetAdvisor.next()) {
        					UserAdvisor advisor = new UserAdvisor(resultSetAdvisor);
        					advisor_list.add(advisor); //add to list of advisors
        					advisor_json = populateAdvisorJSON(advisor); //populate json object with advisor info
        				}else {
        					responseJson = formResponse("Error", "Query for retrieving advisor info failed", 500); //internal server error
							dbmgr.closePreparedStatement();
							dbmgr.closeConnection();
							break;
        				}
	        		}
	        		
	        		//populate json object with time slot and advisor info
	        		timeslot_json.put("id", advising_time_slot.getId());
	        		timeslot_json.put("advisor", advisor_json);
	        		timeslot_json.put("date", advising_time_slot.getDate());
	        		timeslot_json.put("start", advising_time_slot.getTime_start());
	        		timeslot_json.put("end", advising_time_slot.getTime_end());
	        		timeslot_json.put("studentId", advising_time_slot.getStudent_id());
	        		advising_schedule.add(advising_time_slot); //add to list
	        		advising_schedule_json.add(timeslot_json); //add to json object
	        	}
	        	
	        	if(advising_schedule.isEmpty()) {
	    			responseJson = formResponse("Error", "No advising schedule for specified date in DB", 204); //no content
	    		}else {
	    			responseJson = formResponse("schedule", advising_schedule_json, 200); //ok
	    		}
	        	
	            dbmgr.closeResultSet();
	            dbmgr.closePreparedStatement();
	            dbmgr.closeConnection();
			 }else {
				 responseJson = formResponse("Error", "Expected request body arguments not found", 400); //bad request
			 }
		}catch(Exception ex) {
			responseJson = formResponse("Error", ex.toString(), 500); //internal server error
		}
		return responseJson;
	}
	
	//Parameters:
	//	advisor: a UserAdvisor object to extract it's info
	//populates JSONObject with advisor info
	private JSONObject populateAdvisorJSON(UserAdvisor advisor) {
		JSONObject advisor_json = new JSONObject();
		advisor_json.put("id", advisor.getId());
		advisor_json.put("name", advisor.getpName());
		advisor_json.put("degree_types", advisor.getDegree_types());
		advisor_json.put("lead_status", advisor.getLead_status());
		advisor_json.put("department", advisor.getDepartment());
		return advisor_json;
	}
	
	//Parameters:
	//	list: a list with all unique advisors. Initially empty
	//	id: the id of an advisor
	//returns JSONObject with Advisor info - empty if advisor not in list
	private JSONObject getAdvisorJSON(ArrayList<UserAdvisor> list, int id) {
		JSONObject advisor_json = new JSONObject();
		if(list.isEmpty()) {
			return advisor_json;
		}
		for(UserAdvisor advisor: list) { //iterate over unique advisors
			if(advisor.getId() == id) { //if the advisor is already on the list, populate the json formatted advisor
				advisor_json.put("id", advisor.getId());
				advisor_json.put("name", advisor.getpName());
				advisor_json.put("degree_types", advisor.getDegree_types());
				advisor_json.put("lead_status", advisor.getLead_status());
				advisor_json.put("department", advisor.getDepartment());
			}
		}
		
		return advisor_json;
	}
			
	//check if key is in body and make sure it's not null 
	private boolean checkRequestBody(JSONObject event) {
		return (event.get("date") != null);
	}
	
	//for errors - when response body is a string
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
	
	//for success when response body is JSONArray
	private JSONObject formResponse(String label, JSONArray list, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
		responseBody.put(label, list);
        headerJson.put("custom-header", "my custom header value");
        responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
}
