package MavAppoint.util;

import org.json.simple.JSONArray;

public class Util {
	
	public static boolean validateEmail(String email){
		return email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
	}
	
	public static boolean validatePhoneNumber(String phoneNumber){
		return phoneNumber.matches("^\\d{3}-\\d{3}-\\d{4}");
	}
	
	public static boolean validateStudentId(String studentId){
		return studentId.matches("^100\\d{7}") || studentId.matches("^6000\\d{6}");
	}
	
	public static boolean validateName(String name) {
		return name.matches("^[a-zA-Z. _-]{1,32}$"); //potentially unneeded since using prepared statements
	}
	
	public static boolean validateUserId(String id) {
		return id.matches("^\\d+$");
	}
	
	public static boolean validateDepartment(String department) {
		return department.matches("^[a-zA-Z. _-]{1,45}$");
	}
	
	public static boolean validateDegree(String degree) {
		return degree.matches("^[a-zA-Z. _-]{1,45}$");
	}
	
	public static boolean validateMajor(String major) {
		return major.matches("^[a-zA-Z. _-]{1,45}$");
	}
	
	public static boolean validateInitial(String initial) {
		return initial.matches("^[a-zA-Z]{1}$");
	}
	
	public static boolean validateAppType(String apptype) {
		return apptype.matches("^[a-zA-Z0-9. _-]{1,45}$"); //potentially unneeded since using prepared statements
	}
	
	public static boolean validateDuration(String duration) {
		return duration.matches("^\\d+$");
	}
	
	public static boolean validateDate(String date) {
		return date.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$");
	}
	
	public static boolean validateTime(String time) {
		return time.matches("^\\d{2}:\\d{2}:\\d{2}$");
	}
	
	public static boolean validateDescription(String description) {
		return description.matches("^[\\w.' ,-]{0,100}$"); //potentially unneeded since using prepared statements
	}
	
	public static boolean validateTimeSlots(JSONArray time_slots) {
		boolean result = true;
		for(Object item: time_slots) {
			if(!(item instanceof Long)) { //must be a number value
				result = false;
			}
		}
		return result;
	}
	
	public static String addTime(String hour, String minute, int add){
		String result = "";
		try{
		int h = Integer.parseInt(hour);
		int m = Integer.parseInt(minute);
		if (m + add >= 60){
			m = m+add-60;
			h++;
		}
		else{
			m = m+add;
		}
		result = h+":"+m;
		}
		catch(Exception e){
			
		}
		return result;
	}
	
	public static String convertDate(String d){
		if (d.equals("Jan")){
			return "1";
		}if (d.equals("Feb")){
			return "2";
		}if (d.equals("Mar")){
			return "3";
		}if (d.equals("Apr")){
			return "4";
		}if (d.equals("May")){
			return "5";
		}if (d.equals("Jun")){
			return "6";
		}if (d.equals("Jul")){
			return "7";
		}if (d.equals("Aug")){
			return "8";
		}if (d.equals("Sep")){
			return "9";
		}if (d.equals("Oct")){
			return "10";
		}if (d.equals("Nov")){
			return "11";
		}if (d.equals("Dec")){
			return "12";
		}
		return null;
	}

}
