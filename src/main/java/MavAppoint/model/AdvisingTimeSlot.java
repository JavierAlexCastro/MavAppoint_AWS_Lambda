package MavAppoint.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

public class AdvisingTimeSlot {
	private int id;
	private int userId;
	private Date date;
	private Time time_start;
	private Time time_end;
	private String student_id;
	
	public AdvisingTimeSlot(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("id");
		this.userId = resultSet.getInt("userId");
		this.date = resultSet.getDate("date");
		this.time_start = resultSet.getTime("start");
		this.time_end = resultSet.getTime("end");
		this.student_id = resultSet.getString("studentId");
	}
	
	public AdvisingTimeSlot(Date date) {
		this.date = date;		
	}
	
	public AdvisingTimeSlot(Integer id) {
		this.id = id;		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getTime_start() {
		return time_start;
	}

	public void setTime_start(Time time_start) {
		this.time_start = time_start;
	}

	public Time getTime_end() {
		return time_end;
	}

	public void setTime_end(Time time_end) {
		this.time_end = time_end;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}
	
}
