package com.allergychecker.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.allergychecker.AllergyChecker;
import com.google.gdata.util.AuthenticationException;
import com.utils.Constants;

/**
 * This Servlet implements PROXY SERVER for AllergyChecker application.
 */
public class AllergyCheckerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public AllergyCheckerServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Initialize");

		System.out.println(" Parameter map: "+request.getParameterMap().keySet());
		
		/*
		 * userAction variable contains action to be performed.
		 * Following actions can be performed
		 * LOGIN: login into Google Health Database
		 * CHECK_ALLERGY: finds out allergic ingredient in particular product.
		 * */
		String userAction = (String)request.getParameter(Constants.USER_ACTION.getValue());
		
		/*
		 * If no user action received from the client then stop further processing and return error code. 
		 * */
		if(userAction == null){
			response.getWriter().println(Constants.INVALID_USER_ACTION.getValue());
			return;
		}
		
		/*
		 * Created session for each user. getSession() returns session object if it is already exists else creates new session.
		 * */
		HttpSession session = request.getSession(true);
		
		/*
		 * Check if session is created first time or not.
		 * */
		if(session.isNew()){
			System.out.println("New session");
			AllergyChecker checker = new AllergyChecker();
			
			// Store ApplicationChecker object into session. This object will be used to process subsequent user requests.
			session.setAttribute(Constants.ALLERGY_CHECKER_SESSION_OBJECT.getValue(), checker);			
		}
		
		AllergyChecker checker = (AllergyChecker) session.getAttribute(Constants.ALLERGY_CHECKER_SESSION_OBJECT.getValue());
		
		/*
		 * If user request is LOGIN.
		 * */
		if(userAction.equalsIgnoreCase(Constants.LOGIN.getValue())){			
			try {
				String userName = (String) request.getParameter(Constants.USER_NAME.getValue());
				String password = (String) request.getParameter(Constants.PASSWORD.getValue());
				
				// login into the Google Health server. Initialize AllergyChecker object.
				checker.initialize(userName, password);
				System.out.println("Returning true");
				
				// returns true if operation is successful
				response.getWriter().println(Constants.LOGIN_SUCCESSFUL.getValue());
			} catch (AuthenticationException e) {
				System.out.println("Returning false");
				
				// returns false if operation is unsuccessful due to invalid username or password
				response.getWriter().println(Constants.LOGIN_FAILED.getValue());
				
				// destroy the session as operation is failed
				session.invalidate();
				e.printStackTrace();				
			}catch (Exception e) {
				System.out.println("Returning false");
				
				// returns error code if operation is unsuccessful due to some internal error
				response.getWriter().println(Constants.ERROR_IN_CONNECTING_TO_SERVER.getValue());
				
				// destroy the session as operation is failed
				session.invalidate();
				e.printStackTrace();
			}		
			return;
		}			
		
		
		/*
		 * If user request is CHECK_ALLERGY. In this case barcode number is passed in request.
		 * We find out the name of the product from its barcode number and then check whether it contains allergic 
		 * ingredient or not.
		 * */
		if(userAction.equalsIgnoreCase(Constants.CHECK_ALLERGY.getValue())){
			try{
				System.out.println("Checking product for allergy");
				if(checker == null){
					response.getWriter().println(Constants.SERVER_ERROR.getValue());
					return;
				}
				
				// read the barcode number from the request
				String barcode = (String)request.getParameter(Constants.BARCODE_NUMBER.getValue());
				
				// check if the product contains allergic ingredient or not.
				boolean isProductContainsAllergicIngredient = checker.isFoodProductContainAllergy(barcode);
				
				if(isProductContainsAllergicIngredient == true){
					System.out.println("Allergetic ingredient found in product");
					
					// returns true if product contains allergic ingredient or not
					response.getWriter().println(Constants.TRUE.getValue());
				}else{
					System.out.println("Allergetic ingredient not found in product");
					
					// returns false if product does not contain allergic ingredient or not
					response.getWriter().println(Constants.FALSE.getValue());
				}
			}catch(Exception e){
				System.out.println("Error occured for checking product for allergic ingredient");
				// returns error code if operation is unsuccessful due to some internal error.
				response.getWriter().println(Constants.CHECK_ALLERGY_OPERATION_FAILED.getValue());			
			}			
			return;
		}
		
		
		/*
		 * If user request is CHECK_ALLERGY_TEXT. In this case product name is passed in request.
		 * We check whether particular product contains allergic ingredients or not.
		 * */
		if(userAction.equalsIgnoreCase(Constants.CHECK_ALLERGY_TEXT.getValue())){
			
			try{
				System.out.println("Checking allergic ingredients in product given product name.");
				
				// read the product name from the request
				String productName = (String)request.getParameter(Constants.PRODUCT_NAME.getValue());
				
				System.out.println("Product Name received: "+productName);
				
				// check whether particular product contains allergic ingredient or not.
				boolean isProductContainsAllergicIngredient = checker.isFoodProductContainAllergicIngredient(productName);
				
				
				if(isProductContainsAllergicIngredient == true){
					System.out.println("Allergy causing ingredient found in product");
					
					// returns true if product contains allergic ingredient
					response.getWriter().println(Constants.TRUE.getValue());
				}else{
					System.out.println("Allergy causing ingredient not found in product");
					
					// returns false if product does not contains allergic ingredient
					response.getWriter().println(Constants.FALSE.getValue());
				}				
			}catch(Exception e){
				System.out.println("Error occured for checking product for allergic ingredient");
				
				// returns error code if operation is failed due to some internal error.
				response.getWriter().println(Constants.CHECK_ALLERGY_OPERATION_FAILED.getValue());							
			}
			return;
		}
		System.out.println("Servlet execution completed");
	}
}
