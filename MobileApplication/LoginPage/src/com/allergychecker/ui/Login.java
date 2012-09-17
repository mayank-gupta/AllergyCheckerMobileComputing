package com.allergychecker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.allergychecker.ProxyServerConnector;
import com.allergychecker.barcode.IntentIntegrator;
import com.allergychecker.barcode.IntentResult;

/** This class contains the Login form logic of the application.
 *  This class authenticates user using User's Google health 
 *  logging information
 * */
public class Login extends Activity {
	/** Called when the activity is first created. */
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnCancel;
	private TextView lblResult;
	
	/* String to check if Login is Successful or not*/
	private String authenticationProcessInformation = new String();	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*	Layout file called	*/
		setContentView(R.layout.main);							
		/*	Obtaining information entered by the user	*/
		etUsername = (EditText) findViewById(R.id.username);
		etPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.login_button);
		btnCancel = (Button) findViewById(R.id.cancel_button);
		lblResult = (TextView) findViewById(R.id.result);
			
		/*	Action on clicking login button	*/
		btnLogin.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				System.out.println("Login Button Clicked");

				String userName = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				
				ProxyServerConnector connector = ProxyServerConnector.getProxyServerConnector();
				try {
					boolean isLoginSuccessful = connector.loginIntoGoogleHealthDatabase(userName, password);
					if(isLoginSuccessful == true){
						// go to next page
						System.out.println("Invoking intent");
						startActivityForResult(new Intent(Login.this,com.allergychecker.ui.Menu.class), -1);
						authenticationProcessInformation = new String("Login Successful");
					}else{
						authenticationProcessInformation = new String("Login Unsuccessful. Please enter correct user name and password");
						showDialog();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					authenticationProcessInformation = new String("Error in connecting to server. Please try after some time.");
					showDialog();
					e1.printStackTrace();
				}
			}
		});

		/*	Action on clicking cancel button	*/
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

	}
	
	/*	Catching result back from second screen	*/
	@Override
	public  void onActivityResult(int requestcode, int resultcode, Intent intent){
		 IntentResult scanResult = IntentIntegrator.parseActivityResult(requestcode, resultcode, intent);
		 if (scanResult != null) {
			 finish();
		 }
	}
	
	/*	Pop-up to intimate user in case of any failure for login	*/
	public void showDialog(){
		        Toast.makeText(Login.this,authenticationProcessInformation,	Toast.LENGTH_SHORT).show();
	  }

}