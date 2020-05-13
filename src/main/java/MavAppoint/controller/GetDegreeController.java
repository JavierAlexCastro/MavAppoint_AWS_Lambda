package MavAppoint.controller;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import MavAppoint.database.DBManager;
import MavAppoint.model.Degree;

public class GetDegreeController {

	public GetDegreeController() {
		
	}
	
	public JSONObject getDegrees() {
		JSONObject responseJson = new JSONObject();
        
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
    			responseJson = formResponse("Error", "No degree types in DB", 204); //no content
    		}else {
    			responseJson = formResponse("list", degree_name_list, 200); //ok
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
	//	label: the key name for the degree array
	//	list: a list of degree names
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
