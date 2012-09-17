package com.allergychecker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gdata.client.Query;
import com.google.gdata.client.health.*;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.health.ProfileFeed;
import com.google.gdata.data.health.ProfileEntry;
import com.google.gdata.util.AuthenticationException; /*import com.google.gdata.data.Feed;
import com.google.gdata.data.Entry;
import com.google.gdata.data.TextContent;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.AuthenticationException;
 */
import com.google.gdata.util.ServiceException;
import com.utils.XMLParser;


/*
 * This Class contains Google Health server connectivity logic.
 * It connects to Google Health server and retrieves Google Health profile of given user. 
 * */

public class GoogleHealthConnector {
	
	
	private HealthService service;
	
	/*
	 * profileList contains all profiles of user.  
	 * */
	private List<String> profileList;

	/*
	 * Feed URL to retrieve profile list of user.  
	 * */
	public static String PROFILE_LIST_FEED_PATH = "https://www.google.com/health/feeds/profile/list?digest=true";
	
	/*
	 * Feed URL to retrieve select one profile from the list based upon the user choice.  
	 * */
	public static String PROFILE_FEED_PATH = "http://www.google.com/health/feeds/"+ "profile/";
	
	/*
	 * This function retrieves the profile list of user. It store the list in 'profileList' attribute.
	 * It returns true if operation is successful else returns false.
	 * */
	private boolean readProfileList() {

		Feed profileFeed;
		try {
			profileFeed = service.getFeed(new URL(PROFILE_LIST_FEED_PATH),Feed.class);
			profileList = new ArrayList<String>();

			for (Entry profileListEntry : profileFeed.getEntries()) {
				profileList.add(((TextContent) profileListEntry.getContent()).getContent().getPlainText());
			}
			return true;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	/*
	 * This function connects to Google Health profile of user. It initialize the HealthService object.
	 * funtion throws AuthenticationException if it fails to connect to Google Health Server.
	 * */
	public void connectToGoogleHealth(String userName, String password) throws AuthenticationException {
		if(service == null){
			service = new HealthService("Allergy Checker Health Service");
			service.setUserCredentials(userName, password);
		}
	}

	/*
	 * This function connects to Google Health profile of user. It then extracts the allergy list from 
	 * the received object and return it.
	 * 
	 * output: List of allergy that person has. It returns null in case of failure.
	 * Function throws IllegalStateException if function is called before initializing Google Health Service. 
	 * */
	public ArrayList<String> getAllergyList() throws IllegalStateException{
		
		if(service == null){			
			throw new IllegalStateException("First login into the system with your valid username and password");
		}
		
		try {
			if(readProfileList() == false){
				// log
				return null;
			}
			
			ArrayList<String> allergyList = new ArrayList<String>();
			
			for (String profileID : profileList) {
				Query query = new Query(new URL(PROFILE_FEED_PATH + "ui/"+ profileID));
				
				
				// following statement is necessary so that query will return single
				// Atom entry that contains all of the CCR data in profile
				query.addCustomParameter(new Query.CustomParameter("digest","true"));

				// query.addCustomParameter(new Query.CustomParameter("alert",
				// "true"));

				ProfileFeed result = service.getFeed(query, ProfileFeed.class);

				// We used the digest=true parameter, so there should only
				// be a single Atom entry that contains all of the CCR data in
				// profile.

				XMLParser parser = new XMLParser();

				for (ProfileEntry entry : result.getEntries()) {
					String healthProfileData = entry.getContinuityOfCareRecord().getXmlBlob().getBlob();
					System.out.println("healthProfileData : " + healthProfileData);
					ArrayList<String> receivedAllergyListInProfile = parser.getAllergy(healthProfileData);
					if(receivedAllergyListInProfile != null ){					
						allergyList.addAll(receivedAllergyListInProfile);
					}
					
				}

				System.out.println("__________________________________________");

				for (String allergyName : allergyList) {
					System.out.println("Name: " + allergyName);
				}
			}
			
			return allergyList;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}

	public static void main(String[] args) {
		try {
			GoogleHealthConnector connector = new GoogleHealthConnector();
			connector.connectToGoogleHealth("scoe.sagar@gmail.com","Ritchie");
			connector.getAllergyList();
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		}
	}
}
