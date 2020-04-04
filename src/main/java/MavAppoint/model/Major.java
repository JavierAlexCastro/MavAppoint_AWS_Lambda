package MavAppoint.model;

public class Major {
	
	private String name;
	private Department dept;
	
	public Major(String name, Department dept) {
		this.name = name;
		this.dept = dept;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDepartment(Department dept) {
		this.dept = dept;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Department getDepartment() {
		return this.dept;
	}
	
}
