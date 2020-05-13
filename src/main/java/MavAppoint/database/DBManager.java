package MavAppoint.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

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
	
	public ResultSet getAdvisorForTimeslotQuery(int id) throws SQLException {
		String advisor_info_sql = "SELECT * FROM user_advisor INNER JOIN department_user ON user_advisor.userId = department_user.userId WHERE user_advisor.userId = ?";
		this.preparedStmt = conn.prepareStatement(advisor_info_sql);
		this.preparedStmt.setInt(1, id);
		this.resultSet = this.preparedStmt.executeQuery();
		return this.resultSet;
	}
	
	public boolean insertStudentQuery(User user, UserStudent student, String department, String degree, String major) throws SQLException {
		boolean success = false;
		boolean rollback_success = true;
		PreparedStatement insert_user_stmt = null;
		PreparedStatement insert_student_stmt = null;
		PreparedStatement insert_user_dept_stmt = null;
		PreparedStatement insert_user_degree_stmt = null;
		PreparedStatement insert_user_major_stmt = null;
		ResultSet userResultSet = null;

		String insert_user_sql =
				"INSERT INTO `user` (email, password, role, validated, notification) VALUES (?,?,?,?,?)";

		String insert_student_sql =
				"INSERT INTO `user_student` (userId, student_Id, degree_type, phone_num, last_name_initial) VALUES (?,?,?,?,?)";

		String insert_user_dept_sql =
				"INSERT INTO `department_user` (name, userId) VALUES (?,?)";

		String insert_user_degree_sql =
				"INSERT INTO `degree_type_user` (name, userId) VALUES (?,?)";

		String insert_user_major_sql =
				"INSERT INTO `major_user` (name, userId) VALUES (?,?)";
		
		try {
			this.conn.setAutoCommit(false);
	    	insert_user_stmt = this.conn.prepareStatement(insert_user_sql, Statement.RETURN_GENERATED_KEYS);
	    	insert_student_stmt = this.conn.prepareStatement(insert_student_sql);
	    	insert_user_dept_stmt = this.conn.prepareStatement(insert_user_dept_sql);
	    	insert_user_degree_stmt = this.conn.prepareStatement(insert_user_degree_sql);
	    	insert_user_major_stmt = this.conn.prepareStatement(insert_user_major_sql);

			insert_user_stmt.setString(1, user.getEmail());
			insert_user_stmt.setString(2, user.getHashedPassword());
			insert_user_stmt.setString(3, user.getRole());
			insert_user_stmt.setInt(4, user.getValidated());
			insert_user_stmt.setString(5, user.getNotification());
			int user_affected_rows = insert_user_stmt.executeUpdate();
			
			if(user_affected_rows == 1) {
				userResultSet = insert_user_stmt.getGeneratedKeys();
		    	if(userResultSet.next()) {
		    		student.setId(userResultSet.getInt(1)); //retrieve auto-generated userId
		    	}
		    	
		    	insert_student_stmt.setInt(1, student.getId());
		    	insert_student_stmt.setString(2, student.getStudent_id());
		    	insert_student_stmt.setInt(3, student.getDegree_type());
		    	insert_student_stmt.setString(4, student.getPhone());
		    	insert_student_stmt.setString(5, student.getLast_name_initial());
		    	insert_student_stmt.executeUpdate();
		    	
		    	insert_user_dept_stmt.setString(1, department);
		    	insert_user_dept_stmt.setInt(2, student.getId());
		    	insert_user_dept_stmt.executeUpdate();
		    	
		    	insert_user_degree_stmt.setString(1, degree);
		    	insert_user_degree_stmt.setInt(2, student.getId());
		    	insert_user_degree_stmt.executeUpdate();
		    	
		    	insert_user_major_stmt.setString(1, major);
		    	insert_user_major_stmt.setInt(2, student.getId());
		    	insert_user_major_stmt.executeUpdate();
		    	
		    	this.conn.commit(); //commit everything
	    		success = true;
			}else {
				success = false;
	    		try {
	    			//maybe close prepared statements?
	                this.conn.rollback();
	                rollback_success = true;
	            }catch(SQLException sql_ex) {
	                rollback_success = false;
	            }
			}
			
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
	        if (insert_user_stmt != null) {
	        	insert_user_stmt.close();
	        }
	        if (insert_student_stmt != null) {
	        	insert_student_stmt.close();
	        }
	        if (insert_user_dept_stmt != null) {
	        	insert_user_dept_stmt.close();
	        }
	        if (insert_user_degree_stmt != null) {
	        	insert_user_degree_stmt.close();
	        }
	        if (insert_user_major_stmt != null) {
	        	insert_user_major_stmt.close();
	        }
	        if (userResultSet != null) {
	        	userResultSet.close();
	        }
	        this.conn.setAutoCommit(true);
	    }
	    
	    return (success && rollback_success);
	}
	
	public boolean insertAdvisorQuery(User user, UserAdvisor advisor) throws SQLException {
		boolean success = false;
		boolean rollback_success = true;
		PreparedStatement insert_user_stmt = null;
	    PreparedStatement insert_advisor_stmt = null;
	    PreparedStatement insert_user_dept_stmt = null;
	    ResultSet userResultSet = null;
	    
	    String insert_user_sql =
	    		"INSERT INTO `user` (email, password, role, validated, notification) VALUES (?,?,?,?,?)";

	    String insert_advisor_sql =
	    		"INSERT INTO `user_advisor` (userId, pName, notification, name_low, name_high, degree_types, lead_status) VALUES (?,?,?,?,?,?,?)";
	    
	    String insert_user_dept_sql =
	    		"INSERT INTO `department_user` (name, userId) VALUES (?,?)";
	    
	    try {
	    	this.conn.setAutoCommit(false);
	    	insert_user_stmt = this.conn.prepareStatement(insert_user_sql, Statement.RETURN_GENERATED_KEYS);
	    	insert_advisor_stmt = this.conn.prepareStatement(insert_advisor_sql);
	    	insert_user_dept_stmt = this.conn.prepareStatement(insert_user_dept_sql);
	    	
	    	insert_user_stmt.setString(1, user.getEmail());
	    	insert_user_stmt.setString(2, user.getHashedPassword());
	    	insert_user_stmt.setString(3, user.getRole());
	    	insert_user_stmt.setInt(4, user.getValidated());
	    	insert_user_stmt.setString(5, user.getNotification());
	    	int insert_user_affected_rows = insert_user_stmt.executeUpdate();
	    	
	    	if(insert_user_affected_rows == 1) { //user insertion successful
	    		userResultSet = insert_user_stmt.getGeneratedKeys();
		    	if(userResultSet.next()) {
		    		advisor.setId(userResultSet.getInt(1)); //retrieve auto-generated userId
		    	}
		    	
		    	insert_advisor_stmt.setInt(1, advisor.getId());
		    	insert_advisor_stmt.setString(2, advisor.getpName());
		    	insert_advisor_stmt.setString(3, advisor.getNotification());
		    	insert_advisor_stmt.setString(4, advisor.getName_low());
		    	insert_advisor_stmt.setString(5, advisor.getName_high());
		    	insert_advisor_stmt.setInt(6,  advisor.getDegree_types());
		    	insert_advisor_stmt.setInt(7, advisor.getLead_status());
		    	insert_advisor_stmt.executeUpdate();

	    		insert_user_dept_stmt.setString(1, advisor.getDepartment());
	    		insert_user_dept_stmt.setInt(2, advisor.getId());
	    		insert_user_dept_stmt.executeUpdate();
	    		
	    		this.conn.commit(); //commit everything
	    		success = true;
	    	}else {
	    		success = false;
	    		try {
	    			//maybe close prepared statements?
	                this.conn.rollback();
	                rollback_success = true;
	            }catch(SQLException sql_ex) {
	                rollback_success = false;
	            }
	    	}
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
	        if (insert_user_stmt != null) {
	        	insert_user_stmt.close();
	        }
	        if (insert_advisor_stmt != null) {
	        	insert_advisor_stmt.close();
	        }
	        if (insert_user_dept_stmt != null) {
	        	insert_user_dept_stmt.close();
	        }
	        if (userResultSet != null) {
	        	userResultSet.close();
	        }
	        this.conn.setAutoCommit(true);
	    }
	    
	    return (success && rollback_success);
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
	        if (update_schedule_stmt != null) {
	        	update_schedule_stmt.close();
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
