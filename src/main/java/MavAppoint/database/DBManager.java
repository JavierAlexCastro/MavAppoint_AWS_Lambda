package MavAppoint.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

import MavAppoint.model.Appointment;
import MavAppoint.model.User;
import MavAppoint.model.UserAdvisor;
import MavAppoint.model.UserStudent;

public class DBManager {
	
	private static DBManager dbmgr_instance = null; 
	
	private static String db_host;
	private static String db_uname;
	private static String db_pass;
	private String url = "jdbc:mysql://";
	private Connection conn;
	private Statement stmt;
	private ResultSet resultSet;
	private PreparedStatement preparedStmt;
	
	private DBManager() throws SQLException {
		DBManager.db_host = System.getenv("DB_HOST");
		DBManager.db_uname = System.getenv("DB_USER");
		DBManager.db_pass = System.getenv("DB_PASS");
		this.url = this.url + db_host;
	}
	
	//singleton
	public static DBManager getInstance() throws SQLException { 
        if (dbmgr_instance == null) 
            dbmgr_instance = new DBManager(); 
  
        return dbmgr_instance; 
    }
	
	public void createConnection() throws SQLException {
		this.conn = DriverManager.getConnection(this.url, DBManager.db_uname, DBManager.db_pass);
	}
	
	public void createStatement() throws SQLException {
		this.stmt = this.conn.createStatement();
	}
	
	public ResultSet getDepartmentsQuery() throws SQLException {
		this.createConnection();
		this.createStatement();
		this.resultSet = this.stmt.executeQuery("SELECT * FROM department");
		return this.resultSet;
	}
	
	public ResultSet getDegreesQuery() throws SQLException {
		this.createConnection();
		this.createStatement();
		this.resultSet = this.stmt.executeQuery("SELECT * FROM degree_type");
		return this.resultSet;
	}
	
	public ResultSet getMajorsQuery() throws SQLException {
		this.createConnection();
		this.createStatement();
		this.resultSet = this.stmt.executeQuery("SELECT * FROM major");
		return this.resultSet;
	}
	
	public int insertUserQuery(User user) throws SQLException {
		String user_sql = "INSERT INTO `user` (email, password, role, validated, notification) VALUES (?,?,?,?,?)";
		this.preparedStmt = conn.prepareStatement(user_sql);
		this.preparedStmt.setString(1, user.getEmail());
		this.preparedStmt.setString(2, user.getHashedPassword());
		this.preparedStmt.setString(3, user.getRole());
		this.preparedStmt.setInt(4, user.getValidated());
		this.preparedStmt.setString(5, user.getNotification());
		return this.preparedStmt.executeUpdate();
	}
	
	public ResultSet getUserId(String email) throws SQLException  {
		this.resultSet = this.stmt.executeQuery("SELECT userId FROM `user` WHERE email='"+ email +"\'");
		return this.resultSet;
	}
	
	public int insertUserStudentQuery(UserStudent student) throws SQLException {
		String student_sql = "INSERT INTO `user_student` (userId, student_Id, degree_type, phone_num, last_name_initial) VALUES (?,?,?,?,?)";
		this.preparedStmt = conn.prepareStatement(student_sql);
		this.preparedStmt.setInt(1, student.getId());
		this.preparedStmt.setString(2, student.getStudent_id());
		this.preparedStmt.setInt(3, student.getDegree_type());
		this.preparedStmt.setString(4, student.getPhone());
		this.preparedStmt.setString(5, student.getLast_name_initial());
		return this.preparedStmt.executeUpdate();
	}
	
	public int insertUserAdvisorQuery(UserAdvisor advisor) throws SQLException {
		String student_sql = "INSERT INTO `user_advisor` (userId, pName, notification, name_low, name_high, degree_types, lead_status) VALUES (?,?,?,?,?,?,?)";
		this.preparedStmt = conn.prepareStatement(student_sql);
		this.preparedStmt.setInt(1, advisor.getId());
		this.preparedStmt.setString(2, advisor.getpName());
		this.preparedStmt.setString(3, advisor.getNotification());
		this.preparedStmt.setString(4, advisor.getName_low());
		this.preparedStmt.setString(5, advisor.getName_high());
		this.preparedStmt.setInt(6,  advisor.getDegree_types());
		this.preparedStmt.setInt(7, advisor.getLead_status());
		return this.preparedStmt.executeUpdate();
	}
	
	public int insertDepartmentUserQuery(String dept, int id) throws SQLException {
		String department_sql = "INSERT INTO `department_user` (name, userId) VALUES (?,?)";
		this.preparedStmt = conn.prepareStatement(department_sql);
		this.preparedStmt.setString(1, dept);
		this.preparedStmt.setInt(2, id);
		return this.preparedStmt.executeUpdate();
	}
	
	public int insertDegreeTypeUserQuery(String degree, int id) throws SQLException {
		String degree_sql = "INSERT INTO `degree_type_user` (name, userId) VALUES (?,?)";
		this.preparedStmt = conn.prepareStatement(degree_sql);
		this.preparedStmt.setString(1, degree);
		this.preparedStmt.setInt(2, id);
		return this.preparedStmt.executeUpdate();
	}
	
	public int insertMajorUserQuery(String major, int id) throws SQLException {
		String major_sql = "INSERT INTO `major_user` (name, userId) VALUES (?,?)";
		this.preparedStmt = conn.prepareStatement(major_sql);
		this.preparedStmt.setString(1, major);
		this.preparedStmt.setInt(2, id);
		return this.preparedStmt.executeUpdate();
	}
	
	public ResultSet getUserPasswordQuery(String email) throws SQLException {
		this.resultSet = this.stmt.executeQuery("SELECT password FROM `user` WHERE email='" + email + "\'");
		return this.resultSet;
	}
	
	public ResultSet getUserFromEmailQuery(String email) throws SQLException {
		this.resultSet = this.stmt.executeQuery("SELECT * FROM `user` WHERE email='" + email + "\'");
		return this.resultSet;
	}
	
	public ResultSet getStudentFromIdQuery(int id) throws SQLException {
		String student_sql = "SELECT * FROM `user_student` WHERE userId=?";
		this.preparedStmt = conn.prepareStatement(student_sql);
		this.preparedStmt.setInt(1, id);
		this.resultSet = this.preparedStmt.executeQuery();
		return this.resultSet;
	}
	
	public ResultSet getAdvisorFromIdQuery(int id) throws SQLException {
		String student_sql = "SELECT * FROM `user_advisor` WHERE userId=?";
		this.preparedStmt = conn.prepareStatement(student_sql);
		this.preparedStmt.setInt(1, id);
		this.resultSet = this.preparedStmt.executeQuery();
		return this.resultSet;
	}
	
	public ResultSet getAdvisingScheduleQuery(java.util.Date date) throws SQLException {
		String schedule_sql = "SELECT * FROM `advising_schedule` WHERE date=?";
		Date sql_date = new Date(date.getTime()); //may give an error
		this.preparedStmt = conn.prepareStatement(schedule_sql);
		this.preparedStmt.setDate(1, sql_date);
		this.resultSet = this.preparedStmt.executeQuery();
		return this.resultSet;
	}
	
	public ResultSet getAdvisorDepartmentQuery(int id) throws SQLException {
		String advisor_dept_sql = "SELECT * FROM `department_user` WHERE userId=?";
		this.preparedStmt = conn.prepareStatement(advisor_dept_sql);
		this.preparedStmt.setInt(1, id);
		this.resultSet = this.preparedStmt.executeQuery();
		return this.resultSet;
	}
	
	public boolean insertAppointmentQuery(Appointment appointment) throws SQLException{
		boolean success = false;
		boolean rollback_success = true;
		PreparedStatement insert_appt_type_stmt = null;
	    PreparedStatement insert_appt_stmt = null;
	    PreparedStatement update_schedule_stmt = null;
	    
	    String insert_appt_type_sql =
	            "INSERT INTO `appointment_types` (userId, type, duration) VALUES (?,?,?)";

	    String insert_appt_sql =
	            "INSERT INTO `appointments` (advisor_userId, student_userId, date, start, end, type, description, studentId, student_email, student_cell)"
	            + " VALUES (?,?,?,?,?,?,?,?,?,?)";
	    
	    String update_schedule_sql =
	    		"UPDATE `advising_schedule` SET `studentId` = ? WHERE (`id` = ?);";
	    
	    try {
	        this.conn.setAutoCommit(false);
	        insert_appt_type_stmt = this.conn.prepareStatement(insert_appt_type_sql);
	        insert_appt_stmt = this.conn.prepareStatement(insert_appt_sql);
	        update_schedule_stmt = this.conn.prepareStatement(update_schedule_sql);

	        insert_appt_type_stmt.setInt(1, appointment.getAdvisor().getId());
	        insert_appt_type_stmt.setString(2, appointment.getType());
	        insert_appt_type_stmt.setInt(3, appointment.getDuration());
	        insert_appt_type_stmt.executeUpdate();
	        
	        Date sql_date = new Date(appointment.getDate().getTime()); //may give an error
	        
	        insert_appt_stmt.setInt(1, appointment.getAdvisor().getId());
	        insert_appt_stmt.setInt(2, appointment.getStudent().getId());
	        insert_appt_stmt.setDate(3, sql_date);
	        insert_appt_stmt.setTime(4, appointment.getStart_time());
	        insert_appt_stmt.setTime(5, appointment.getEnd_time());
	        insert_appt_stmt.setString(6, appointment.getType());
	        insert_appt_stmt.setString(7, appointment.getDescription());
	        insert_appt_stmt.setString(8, appointment.getStudent().getStudent_id());
	        insert_appt_stmt.setString(9, appointment.getUser().getEmail());
	        insert_appt_stmt.setString(10,  appointment.getStudent().getPhone());
            insert_appt_stmt.executeUpdate();
            
            int[] time_slots = appointment.getTime_slots();
            for(int i = 0; i < time_slots.length; ++i) {
            	update_schedule_stmt.setString(1, appointment.getStudent().getStudent_id());
            	update_schedule_stmt.setInt(2, time_slots[i]);
            	update_schedule_stmt.executeUpdate();
            	this.conn.commit();
            }
            success = true;
	    } catch (SQLException ex ) {
	        success = false;
	        if (this.conn != null) {
	            try {
	                this.conn.rollback();
	                rollback_success = true;
	            }catch(SQLException sql_ex) {
	                rollback_success = false;
	            }
	        }
	    } finally {
	        if (insert_appt_type_stmt != null) {
	        	insert_appt_type_stmt.close();
	        }
	        if (insert_appt_stmt != null) {
	        	insert_appt_type_stmt.close();
	        }
	        this.conn.setAutoCommit(true);
	    }
	    
	    return (success && rollback_success);
	}
	
	public void closeConnection() throws SQLException {
		this.conn.close();
	}
	
	public void closeResultSet() throws SQLException {
		this.resultSet.close();
	}
	
	public void closeStatement() throws SQLException {
		this.stmt.close();
	}
	
	public void closePreparedStatement() throws SQLException {
		this.preparedStmt.close();
	}

}
