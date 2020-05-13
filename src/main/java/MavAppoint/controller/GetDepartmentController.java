package MavAppoint.controller;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.Department;

public class GetDepartmentController {
	
	public GetDepartmentController() {
		
	}
	
	public JSONObject getDepartments() {
		JSONObject responseJson = new JSONObject();
        
        try {
        	DBManager dbmgr = DBManager.getInstance();
        	ResultSet resultSet = dbmgr.getDepartmentsQuery();
    		
        	ArrayList<Department> department_list = new ArrayList<Department>();
        	ArrayList<String> dept_name_list = new ArrayList<String>();
        	
    		while(resultSet.next()) {
    			Department dept = new Department(resultSet.getString("name"));
    			department_list.add(dept);
    			dept_name_list.add(dept.getName());
    		}
    		
    		if(department_list.isEmpty()) {
    			responseJson = formResponse("Error", "No departments in DB", 204); //no content
    		}else {
    			responseJson = formResponse("list", dept_name_list, 200); //ok
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
	//	label: the key name for the department array
	//	list: a list of department names
	//	code: the status code for the response
	//for success - returning a JSONArray
	private JSONObject formResponse(String label, ArrayList<String> list, int code) {
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
