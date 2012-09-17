package com.allergychecker;


import java.util.ArrayList;

import com.allergychecker.dataaccess.AllergySystemDatabaseConnection;
import com.allergychecker.dataaccess.BarcodeDatabaseDAO;
import com.allergychecker.dataaccess.FoodIngredientDatabaseDAO;
import com.google.gdata.util.AuthenticationException;

/*
 * This class contains business logic of AlergyChecker application.
 * It finds out whether particular product contains allergic material or not.
 * */
public class AllergyChecker {
	/*
	 * This object holds database connection to AllergyChecker application database
	 * */
	
	AllergySystemDatabaseConnection dbConnection = null;
	
	/*
	 * allergyList contains allergies list of particular person. 
	 * */
	ArrayList<String> allergyList = null;
	
	/*
	 * This function inputs username and password. It then connects to Google health server and fetches 
	 * user profile. It then extracts allergy list from the received input profile.
	 * */
	public boolean initialize(String userName, String password) throws Exception{
		dbConnection = new AllergySystemDatabaseConnection();		
		GoogleHealthConnector connector = new GoogleHealthConnector();
		connector.connectToGoogleHealth(userName, password);
		allergyList = connector.getAllergyList();
		if(allergyList == null){
			throw new Exception("Unable to retrieve allergu list from Google health profile");
		}
		return false;
	}
	
	/*
	 * This function finds out whether particular product contains allergic ingredient or not.
	 * input: barcode of the product to be checked.
	 * Output: Function returns 'true' if product contains allergic ingredient else it returns false.
	 * function throws Exception in case of failure
	 * */
	public boolean isFoodProductContainAllergy(String barcode) throws Exception{
		String productName = new BarcodeDatabaseDAO(dbConnection).getProductName(barcode);
		System.out.println("Product Name: "+productName);
		if(productName == null){
			throw new Exception("Product does not exists in the database.");
		}
		
		ArrayList<String> ingredientList = new FoodIngredientDatabaseDAO(dbConnection).getIngredients(productName);
		System.out.println("Ingredient List: "+ingredientList);
		if(ingredientList == null){
			throw new Exception("Ingredient list does not exists in the database.");
		}
		
		System.out.println("allergyList    : "+allergyList);
		
		for(String allergy: allergyList){
			if(ingredientList.contains(allergy.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * This function finds out whether particular product contains allergic ingredient or not.
	 * input: name of the product to be checked.
	 * Output: Function returns 'true' if product contains allergic ingredient else it returns false.
	 * function throws Exception in case of failure.
	 * */
	public boolean isFoodProductContainAllergicIngredient(String productName) throws Exception{

		ArrayList<String> ingredientList = new FoodIngredientDatabaseDAO(dbConnection).getIngredients(productName);
		System.out.println("Ingredient List: "+ingredientList);
		if(ingredientList == null){
			throw new Exception("Ingredient list does not exists in the database.");
		}
		
		System.out.println("allergyList    : "+allergyList);
		
		for(String allergy: allergyList){
			if(ingredientList.contains(allergy.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) throws Exception{
		AllergyChecker checker = new AllergyChecker();

		try {
			checker.initialize("scoe.sagar@gmail.com","Ritchie");
			System.out.println("Is product contain allergetic item: "+checker.isFoodProductContainAllergy("12345"));
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
}
