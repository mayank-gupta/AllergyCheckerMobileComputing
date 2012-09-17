package com.allergychecker.ui;

//import java.util.logging.ConsoleHandler;
import com.allergychecker.*;
import com.allergychecker.barcode.*;
import com.allergychecker.utils.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

/** This class contains the logic for second screen of the application.
 * It is used to get product name or to get the barcode from the user.
 * */
public class Menu extends Activity {
	/*Definition of variables used and accessed from layout file menu.xml*/
	private Button barscanner;
	private Button Cancel;
	private Button Enter;
	private EditText etproductName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.out.println("In Menu SCREEN");
		super.onCreate(savedInstanceState);
		System.out.println("In Menu SCREEN 1");
		setContentView(R.layout.menu);
		System.out.println("In Menu SCREEN 2");

		/*Getting values inserted from the layout file*/
		
		etproductName = (EditText) findViewById(R.id.title);
		barscanner = (Button) findViewById(R.id.Button);
		Cancel = (Button) findViewById(R.id.Cancel);
		Enter = (Button) findViewById(R.id.Enter);
		
		/*Action to be taken on clicking "Scan a barcode button"*/
		barscanner.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentIntegrator.initiateScan(Menu.this);//Calling Zebra Crossing activity to scan a barcode
			}
		});
		
		/*Action to be taken on clicking back button by ending the current activity*/
		Cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
					finish();
			}
		});
		
		/*Action to be taken when user enters product name and clicks the button*/
		Enter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String productName = etproductName.getText().toString();
				/*Connecting to proxy server*/
				ProxyServerConnector serverConnector = ProxyServerConnector.getProxyServerConnector();
				String value = serverConnector.isProductContainsAllergicIngredient(productName, false);
				System.out.println("product name text : " + value);
				/*Checking item entered by the user with database*/
				if (value.trim().equalsIgnoreCase(Constants.TRUE)) {
						Toast.makeText(Menu.this,"CAUTION : Allergic ingredient found in the product",	Toast.LENGTH_LONG).show();
				} else {
					/*Handling exceptions*/
					if (value.trim().equalsIgnoreCase(Constants.OPERATION_FAILED)) {
						Toast.makeText(Menu.this,"ERROR : unable to perform request operation. Please try after some time",	Toast.LENGTH_LONG).show();
					} else {					
						Toast.makeText(Menu.this,"OK : No allergic ingredients found",	Toast.LENGTH_LONG).show();						
					}
				}
			}
		});

	}

	/*Catching values returned by Zebra Crossing barcode activity*/
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			// Handling barcode scan result
			String barcode = scanResult.getContents();
			ProxyServerConnector serverConnector = ProxyServerConnector.getProxyServerConnector();
			String value = serverConnector.isProductContainsAllergicIngredient(barcode, true);
			//Checking barcode returned from scanner with user's allergies
			if (value.trim().equalsIgnoreCase(Constants.TRUE)) {
					Toast.makeText(Menu.this,"CAUTION : Allergic ingredient found in the product",	Toast.LENGTH_LONG).show();
			} else {
				if (value.trim().equalsIgnoreCase(Constants.OPERATION_FAILED)) {
					Toast.makeText(Menu.this,"ERROR : unable to perform request operation. Please try after some time",	Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(Menu.this,"OK : No allergic ingredients found",	Toast.LENGTH_LONG).show();
				}				
			}
		}
	}
}
