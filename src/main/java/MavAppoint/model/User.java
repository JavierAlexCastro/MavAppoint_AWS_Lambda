package MavAppoint.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import MavAppoint.password.HashPassword;
import MavAppoint.password.RandomPasswordGenerator;

public class User {
	private int user_id;
	private String email;
	private String password;
	private String hashed_password;
	private String role;
	private int validated;
	private String notification;
	
	public User(String email, String role) {
		this.email = email;
		this.password = RandomPasswordGenerator.genpass();
		this.hashed_password = HashPassword.hashpass(this.password);
		this.role = role;
		this.validated = 0; //default to not validated on creation
		this.notification = "false"; //default to not notify on creation
	}
	
	public User(ResultSet resultSet) throws SQLException {
		this.user_id = resultSet.getInt("userId");
		this.email = resultSet.getString("email");
		this.hashed_password = resultSet.getString("password");
		this.role = resultSet.getString("role");
		this.validated = resultSet.getInt("validated");
		this.notification = resultSet.getString("notification");
	}
	
	public User(int id, String email) {
		this.user_id = id;
		this.email = email;
	}
	
	public int getUser_id() {
		return this.user_id;
	}
	
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getHashedPassword() {
		return hashed_password;
	}

	public void setHashedPassword(String password) {
		this.hashed_password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getValidated() {
		return validated;
	}

	public void setValidated(int validated) {
		this.validated = validated;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}	
	
}
