package MavAppoint.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	DBManager dbmgr = DBManager.getInstance();
        	ResultSet resultSet = dbmgr.getMajorsQuery();
    		
        	ArrayList<Major> major_list = new ArrayList<Major>();
    		ArrayList<List<String>> major_and_dept_list = new ArrayList<List<String>>();

    		while(resultSet.next()) {
    			String[] inner_dept_array = {"", ""};
    			Department dept = new Department(resultSet.getString("dep_name"));
    			Major major = new Major(resultSet.getString("name"), dept);
    			inner_dept_array[0] = major.getName();
    			inner_dept_array[1] = major.getDepartment().getName();
    			major_list.add(major);
    			major_and_dept_list.add(Arrays.asList(inner_dept_array));
    		}
    		if(major_list.isEmpty()) {
    			responseBody.put("Error", "No departments in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 204); //no content
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			JSONArray json_array = new JSONArray();
    			json_array.addAll(major_and_dept_list);
        		responseBody.put("list", json_array);
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 200); //ok
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}
            resultSet.close();
            dbmgr.closeResultSet();
            dbmgr.closeStatement();
            dbmgr.closeConnection();
        }catch(Exception ex) {
        	headerJson.put("custom-header", "my custom header value");
            responseBody.put("Error", ex.toString());
        	responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", 500); //internal server error
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody);
        }
        
        return responseJson;
	}

}
