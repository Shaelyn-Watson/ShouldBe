package app.there.shouldbe;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

/**
 * This is a wrapper class for the Connection class using our database information
 * @author Alexis Emperador
 */
public class DatabaseConnection {
	
	private final static String DATABASE_URL = "ec2-54-225-101-199.compute-1.amazonaws.com";
	private final static String DATABASE_NAME = "d5puq82aq6a28e";
	private final static Integer DATABASE_PORT = 5432;
	private Connection conn;
	
	
	/**
	 * Constructor
	 */
	public void DataBaseConnection() {
		try {
			conn = getConnection();
		}
		catch (Exception e) {
			Log.e("DATABASE CONNECTION", "Could not get connection");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets connection to ShouldBe's database
	 * @return Connection to database
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws URISyntaxException, SQLException {
		URI dbUri = new URI(System.getenv(DATABASE_URL));
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1]; 
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
		
		return DriverManager.getConnection(dbUrl, username, password);
		
	}
	
	
	/**
	 * Checks this connection to database
	 * @return false if connection is null, true otherwise
	 */
	public boolean checkConnection() {
		return this.conn != null ? true : false;
	}
	
	/**
	 * Returns connection metadata
	 * @return String with connection metadata
	 * @throws SQLException 
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.conn.getMetaData();
	}
	
	/**
	 * Returns statement to execute sql
	 * @return Statement to execute sql
	 * @throws SQLException 
	 */
	public Statement createStatement() throws SQLException {
		return this.conn.createStatement();
	}
	
	/**
	 *  Closes this connection to the database
	 * @throws SQLException 
	 */
	public void close() throws SQLException {
		this.conn.close();
	}

}
