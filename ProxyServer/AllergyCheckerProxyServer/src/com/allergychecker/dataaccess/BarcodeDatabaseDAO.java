package com.allergychecker.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class provides Database Access Object for Barcode database.
 * It takes barcode number of product and returns name of the product as a output.
 * */
public class BarcodeDatabaseDAO {
	
	/*
	 * Application database connection object.
	 * */
	AllergySystemDatabaseConnection dbConnection = null;
		
	public BarcodeDatabaseDAO(AllergySystemDatabaseConnection connection){
		dbConnection = connection;
	}
		
	/*
	 * Returns database connection object
	 * */
	public Connection getDatabaseConnection(){
		return dbConnection.getDatabaseConnection();
	}
	
	/*
	 * This function inputs barcode number of product and returns the name of the product. 
	 * It returns 'null' in case of failure.  
	 * */
	public String getProductName(String barcode){
		System.out.println("Product Name: "+barcode);
		
		Connection databaseConnection = getDatabaseConnection();
		
		if(databaseConnection == null){
			// log
			return null;
		}
		
		try {
			PreparedStatement dbStatement = databaseConnection.prepareStatement("select * from barcode_product_name_mapping where barcode=" + "?");		
			dbStatement.setString (1, barcode);
			
			ResultSet resultSet = dbStatement.executeQuery();
			
			String productName = null;
			if(resultSet.next()){
				productName = resultSet.getString("product_name");			
			}
			
			return productName;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String args[]){
		AllergySystemDatabaseConnection connection = new AllergySystemDatabaseConnection();
		connection.prepareConnection();
		BarcodeDatabaseDAO dao = new BarcodeDatabaseDAO(connection);
		System.out.println("Product Name: "+dao.getProductName("12345"));;
	}
}
