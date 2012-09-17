package com.allergychecker.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.utils.DatabaseConnectionParameters;

public class AllergySystemDatabaseConnection {
	private Connection databaseConnection;
	

	
	public boolean prepareConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

			databaseConnection = DriverManager.getConnection(DatabaseConnectionParameters.DATABASE_URL,DatabaseConnectionParameters.DATABASE_USER_NAME, DatabaseConnectionParameters.DATABASE_PASSWORD);

			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public Connection getDatabaseConnection(){
		if(databaseConnection == null){
			if (prepareConnection() == false){
				return null;
			}
		}
		return databaseConnection;
	}
}
