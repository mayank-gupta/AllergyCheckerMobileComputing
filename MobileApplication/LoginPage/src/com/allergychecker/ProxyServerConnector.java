package com.allergychecker;
/*Proxy Server Connection class definition*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.allergychecker.utils.Constants;


public class ProxyServerConnector {
	
	private static ProxyServerConnector connector = null;
	
	HttpClient httpclient = null;
    HttpPost httppost = null;
    /*Constructor*/
    private ProxyServerConnector(){
    	
    }
    /*
     * Returns the connection to the proxy server
     * 
     * */
    public static ProxyServerConnector getProxyServerConnector(){
    	if(connector == null){
    		connector = new ProxyServerConnector();
    		if(connector.prepareServerConnection() == false){
    			return null;
    		}
    	}
    	return connector;
    }
    
    /*
     * Connecting to the proxy server
     * 
     * */
	private boolean prepareServerConnection(){
		try {
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost("http://10.128.33.228:8081/AllergyCheckerProxyServer/AllergyChecker");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * Checking if the product contains the allergic ingredients
	 * 
	 * */
	public String isProductContainsAllergicIngredient(String searchValue, boolean isBarcode) {        
		System.out.println("In function isProductContainsAllergicIngredient");
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            
            if(isBarcode == true){
            	nameValuePairs.add(new BasicNameValuePair(Constants.BARCODE, searchValue));
            	 nameValuePairs.add(new BasicNameValuePair(Constants.USER_ACTION, Constants.CHECK_ALLERGY));
            }else{
            	nameValuePairs.add(new BasicNameValuePair(Constants.PRODUCT_NAME, searchValue));
            	nameValuePairs.add(new BasicNameValuePair(Constants.USER_ACTION, Constants.CHECK_ALLERGY_TEXT));
            }
            
           
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            
            System.out.println("NEW OUTPUT: " + str.toString());
            
            return str.toString();

        } catch (ClientProtocolException e) {
            System.out.println("ClientProtocolException : "+e);
        } catch (IOException e) {
            System.out.println("IOException : "+e);
        }
        
        return null;
    }
	
	
	/*
	 * Logging into the Google health
	 * 
	 * */
	public boolean loginIntoGoogleHealthDatabase(String userName, String password) throws Exception{
		System.out.println("in function loginIntoGoogleHealthDatabase");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(Constants.USER_ACTION,Constants.LOGIN ));
        nameValuePairs.add(new BasicNameValuePair(Constants.USER_NAME,userName));
        nameValuePairs.add(new BasicNameValuePair(Constants.PASSWORD,password));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
     // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        StringBuilder authenticationStatus = new StringBuilder();
        String line = null;

        while((line = reader.readLine()) != null){
        	authenticationStatus.append(line + "\n");
        }
        in.close();
        
        System.out.println("authenticationStatus : "+authenticationStatus.toString());
        System.out.println("authenticationStatus : "+authenticationStatus.toString().trim());
        
        
        if(authenticationStatus.toString().trim().equalsIgnoreCase(Constants.LOGIN_SUCCESSFUL)){
        	System.out.println("Returning true");
        	return true;
        }else{
        	if(authenticationStatus.toString().trim().equalsIgnoreCase(Constants.OPERATION_FAILED)){
            	System.out.println("error occurred at server while processing request");
            	throw new Exception("error occurred at server while processing request");
            }else{
            	return false;
            }
        }
    }
}
