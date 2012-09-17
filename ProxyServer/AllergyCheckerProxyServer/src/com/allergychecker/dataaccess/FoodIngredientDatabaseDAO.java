package com.allergychecker.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * This class provides Database Access Object for Food Ingredient database.
 * It takes food name and returns ingredient list containing in particular product as output.
 * */

public class FoodIngredientDatabaseDAO {
	
	/*
	 * Database connection object
	 * */
	AllergySystemDatabaseConnection dbConnection = null;
	
	public FoodIngredientDatabaseDAO(AllergySystemDatabaseConnection connection){
		dbConnection = connection;
	}
	
	/*
	 * Returns database connection object.
	 * */
	private Connection getDatabaseConnection(){
		return dbConnection.getDatabaseConnection();
	}
	
	/*
	 * This function inputs product name and returns ingredient list as output. Function returns 'null' in case of failure.
	 * */
	public ArrayList<String> getIngredients(String productName) {
		Connection databaseConnection = getDatabaseConnection();

		if (databaseConnection == null) {
			// log
			return null;
		}

		try {
			PreparedStatement dbStatement = databaseConnection.prepareStatement("select * from product_ingredients where product_name="+ "?");
			
			dbStatement.setString(1, productName);

			ResultSet resultSet = dbStatement.executeQuery();
			
			String ingredientString = null;
			
			if (resultSet.next()) {
				ingredientString = resultSet.getString("ingredients");
			}
			
			if(ingredientString != null && ingredientString.length() != 0){
				String ingredients[] = ingredientString.split(",");
				
				if(ingredients.length != 0){
					ArrayList<String> ingredientsList = new ArrayList<String>();
					
					for(String item : ingredients){
						ingredientsList.add(item.toLowerCase());
					}
					
					return ingredientsList;
				}				
			}	

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public static void main(String args[]){
		AllergySystemDatabaseConnection connection = new AllergySystemDatabaseConnection();
		connection.prepareConnection();
		FoodIngredientDatabaseDAO dao = new FoodIngredientDatabaseDAO(connection);
		System.out.println("List : "+dao.getIngredients("temp"));;
		
	}
}
