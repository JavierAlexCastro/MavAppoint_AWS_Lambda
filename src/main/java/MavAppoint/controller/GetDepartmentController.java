package MavAppoint.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
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
    			responseBody.put("RequestError", "No departments in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			JSONArray json_array = new JSONArray();
    			json_array.addAll(dept_name_list);
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
        }catch(SQLException ex) {
        	headerJson.put("custom-header", "my custom header value");
            responseBody.put("ServerError", ex.toString());
        	responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", 500); //internal server error
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody);
        }catch(Exception ex) {
        	headerJson.put("custom-header", "my custom header value");
            responseBody.put("ServerError", ex.toString());
        	responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", 500); //internal server error
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody);
        }
        
        return responseJson;
	}
}
