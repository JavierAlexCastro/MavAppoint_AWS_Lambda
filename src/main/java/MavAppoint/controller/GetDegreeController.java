package MavAppoint.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.Degree;
import MavAppoint.model.Department;

public class GetDegreeController {

	public GetDegreeController() {
		
	}
	
	public JSONObject getDegrees() {
		JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        JSONObject headerJson = new JSONObject();
        
        try {
        	DBManager dbmgr = DBManager.getInstance();
        	ResultSet resultSet = dbmgr.getDegreesQuery();
        	
        	ArrayList<Degree> degree_list = new ArrayList<Degree>();
        	ArrayList<String> degree_name_list = new ArrayList<String>();
    		
    		while(resultSet.next()) {
    			Degree degree = new Degree(resultSet.getString("name"));
    			degree_list.add(degree);
    			degree_name_list.add(degree.getName());
    		}
    		if(degree_list.isEmpty()) {
    			responseBody.put("RequestError", "No degree types in DB");
                headerJson.put("custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", 400); //bad request
                responseJson.put("headers", headerJson);
                responseJson.put("body", responseBody);
    		}else {
    			JSONArray json_array = new JSONArray();
    			json_array.addAll(degree_name_list);
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
            responseBody.put("ServerError", ex.toString());
        	responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", 500); //internal server error
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody);
        }
        
        return responseJson;
	}
}
