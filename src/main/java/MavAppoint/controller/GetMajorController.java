package MavAppoint.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.Department;
import MavAppoint.model.Major;

public class GetMajorController {
	
	public GetMajorController() {
		
	}
	
	public JSONObject getMajors() {
		JSONObject responseJson = new JSONObject();
        
        try {
        	DBManager dbmgr = DBManager.getInstance();
        	ResultSet resultSet = dbmgr.getMajorsQuery();
    		
        	ArrayList<Major> major_list = new ArrayList<Major>();
    		ArrayList<List<String>> major_and_dept_list = new ArrayList<List<String>>();

    		while(resultSet.next()) { //iterate through majors
    			String[] inner_dept_array = {"", ""}; //formatted as {major, department}
    			Department dept = new Department(resultSet.getString("dep_name"));
    			Major major = new Major(resultSet.getString("name"), dept);
    			inner_dept_array[0] = major.getName();
    			inner_dept_array[1] = major.getDepartment().getName();
    			major_list.add(major);
    			major_and_dept_list.add(Arrays.asList(inner_dept_array));
    		}
    		if(major_list.isEmpty()) {
    			responseJson = formResponse("Error", "No departments in DB", 204); //no content
    		}else {
    			responseJson = formResponse("list", major_and_dept_list, 200); //ok
    		}
            resultSet.close();
            dbmgr.closeResultSet();
            dbmgr.closeStatement();
            dbmgr.closeConnection();
        }catch(Exception ex) {
        	responseJson = formResponse("Error", ex.toString(), 500); //internal server error
        }
        
        return responseJson;
	}
	
	//Parameters:
	//	label: the key name for the major array
	//	list: a list of [major, department] pairs
	//	code: the status code for the response
	//for success - returning a JSONArray
	private JSONObject formResponse(String label, ArrayList<List<String>> list, int code) {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        JSONArray json_array = new JSONArray();
		json_array.addAll(list);
		responseBody.put(label, json_array);
        headerJson.put("custom-header", "my custom header value");
        responseJson.put("isBase64Encoded", false);
        responseJson.put("statusCode", code);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody);
        
        return responseJson;
	}
	
	//for errors - returning an error message
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

}
