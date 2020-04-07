package MavAppoint.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStudent {
	
	private int id;
	private String student_id;
	private int degree_type;
	private String phone;
	private String last_name_initial;
	
	public UserStudent(int id, String student_id, int degree_type, String phone, String last_name_initial) {
		this.id = id;
		this.student_id = student_id;
		this.degree_type = degree_type;
		this.phone = phone;
		this.last_name_initial = last_name_initial;
	}
	
	public UserStudent(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("userId");
		this.student_id = resultSet.getString("student_Id");
		this.degree_type = resultSet.getInt("degree_type");
		this.phone = resultSet.getString("phone_num");
		this.last_name_initial = resultSet.getString("last_name_initial");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public int getDegree_type() {
		return degree_type;
	}

	public void setDegree_type(int degree_type) {
		this.degree_type = degree_type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLast_name_initial() {
		return last_name_initial;
	}

	public void setLast_name_initial(String last_name_initial) {
		this.last_name_initial = last_name_initial;
	}
	
	

}
