package MavAppoint.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
	
	private static DBManager dbmgr_instance = null; 
	
	private static String db_host;
	private static String db_uname;
	private static String db_pass;
	private String url = "jdbc:mysql://";
	private Connection conn;
	private Statement stmt;
	private ResultSet resultSet;
	
	private DBManager() throws SQLException {
		DBManager.db_host = System.getenv("DB_HOST");
		DBManager.db_uname = System.getenv("DB_USER");
		DBManager.db_pass = System.getenv("DB_PASS");
		this.url = this.url + db_host;
	}
	
	//singleton
	public static DBManager getInstance() throws SQLException 
    { 
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
	
	public void closeConnection() throws SQLException {
		this.conn.close();
	}
	
	public void closeResultSet() throws SQLException {
		this.resultSet.close();
	}
	
	public void closeStatement() throws SQLException {
		this.stmt.close();
	}

}
