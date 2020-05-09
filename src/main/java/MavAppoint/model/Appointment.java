package MavAppoint.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;

public class Appointment {
	private int id;
	private User user; //will be refactored to use inheritance later
	private String type;
	private int duration;
	private UserAdvisor advisor;
	private UserStudent student;
	private Date date;
	private Time start_time;
	private Time end_time;
	private String description;
	private int[] time_slots;
	
	public Appointment(String type, Integer duration, String date, String start_time, String end_time, 
			String description, User user, UserAdvisor advisor, UserStudent student, JSONArray time_slots) throws ParseException {
		this.type = type;
		this.duration = duration;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		this.date = formatter.parse(date);
		this.start_time = Time.valueOf(start_time);
		this.end_time = Time.valueOf(end_time);
		this.description = description;
		this.user = user;
		this.advisor = advisor;
		this.student = student;
		this.time_slots = new int[time_slots.size()];
		populateTimeSlots(time_slots);
	}
	
	private void populateTimeSlots(JSONArray time_slots) {
		for(int i = 0; i < time_slots.size(); ++i) {
			this.time_slots[i] = ((Long) time_slots.get(i)).intValue();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public UserAdvisor getAdvisor() {
		return advisor;
	}

	public void setAdvisor(UserAdvisor advisor) {
		this.advisor = advisor;
	}

	public UserStudent getStudent() {
		return student;
	}

	public void setStudent(UserStudent student) {
		this.student = student;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getStart_time() {
		return start_time;
	}

	public void setStart_time(Time start_time) {
		this.start_time = start_time;
	}

	public Time getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Time end_time) {
		this.end_time = end_time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int[] getTime_slots() {
		return this.time_slots;
	}
	
	public void setTime_slots(int[] time_slots) {
		this.time_slots = time_slots;
	}
	
}
