package MavAppoint.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdvisor {
	private int id;
	private String pName;
	private String notification;
	private String name_low;
	private String name_high;
	private String department;
	private int degree_types;
	private int lead_status;
	
	public UserAdvisor(int id, String pName, String department) {
		this.id = id;
		this.pName = pName;
		this.department = department;
		this.notification = "day"; //hardcoded due to lack of info
		this.name_low = "A"; //hardcoded due to lack of info
		this.name_high = "Z"; //hardcoded due to lack of info
		this.degree_types = 7; //hardcoded due to lack of info
		this.lead_status = 1; //hardcoded due to lack of info
	}
	
	public UserAdvisor(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("userId");
		this.pName = resultSet.getString("pName");
		this.notification = resultSet.getString("notification");
		this.name_low = resultSet.getString("name_low");
		this.name_high = resultSet.getString("name_high");
		this.degree_types = resultSet.getInt("degree_types");
		this.lead_status = resultSet.getInt("lead_status");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getName_low() {
		return name_low;
	}

	public void setName_low(String name_low) {
		this.name_low = name_low;
	}

	public String getName_high() {
		return name_high;
	}

	public void setName_high(String name_high) {
		this.name_high = name_high;
	}
	
	public String getDepartment() {
		return this.department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}

	public int getDegree_types() {
		return degree_types;
	}

	public void setDegree_types(int degree_types) {
		this.degree_types = degree_types;
	}

	public int getLead_status() {
		return lead_status;
	}

	public void setLead_status(int lead_status) {
		this.lead_status = lead_status;
	}
	
	

}
