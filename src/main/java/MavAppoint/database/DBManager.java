package MavAppoint.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import MavAppoint.model.User;
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
