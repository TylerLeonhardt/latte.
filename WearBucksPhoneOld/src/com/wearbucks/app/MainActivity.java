package com.wearbucks.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	//form
	public EditText cardNumber;
	public Button addNewCard;
	
	//URLs to fetch from
	public String API;
	public String APIimage;
	
	//The data fetched
	public ImageView imageResponse;
	public TextView response;
	
	//Notification ID
	public static final int NOTIFICATION_ID = 99;
	
	//stored preferences
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String CARDNUMBER = "CARDNUMBER";
	public String username, password, listOfCards = "";
	
	//Storing data
	SharedPreferences pref;
	Editor editor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Storing data
    	pref = getApplicationContext().getSharedPreferences("WearBucksPref", 0);
    	editor = pref.edit();
    	
    	pref.edit().clear().commit();	//testing only; remove
        
        //clear notification
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	notiManager.cancel(NOTIFICATION_ID);
        
        //FIND ALL THE VIEWS
        response = (TextView) findViewById(R.id.response);
        addNewCard = (Button) findViewById(R.id.addNewCard);
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        imageResponse = (ImageView) findViewById(R.id.barcode);
                
        
        //check if logged in
        if (pref.getString(USERNAME, null) == null || pref.getString(PASSWORD, null) == null) {
        	System.err.println("nothing saved yet");
        	getCreds();
        }
        
        //add empty card number to initialize list
        editor.putString(CARDNUMBER, null);
        editor.commit();
        
        //Request Listener
        addNewCard.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {
            	//close keyboard
            	InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
            	inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            	
            	//fetches the data and pushes it to the view
            	//One param: boolean whether the username and password fields are filled in
            	try {
					fetch(validateCredentials());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	addCard();
            }
        });
    }
    
    private void addCard(){
    	//show dialog
    	// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.activity_addcard, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		
		final EditText inputCardNumber = (EditText) promptsView.findViewById(R.id.cardNumber);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Save Card",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				//get user input for card number
			    //valid card number
			    //add it to the SharedPreference list
			    listOfCards = listOfCards + "" + inputCardNumber.getText().toString() + ";";
			    editor.putString(CARDNUMBER, listOfCards);
			    editor.commit();
			    
			    System.err.println(pref.getString(CARDNUMBER, null));
			    
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
    }
    
    private void getCreds(){
    	// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.activity_login, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText un = (EditText) promptsView.findViewById(R.id.usernameInput);
		final EditText pass = (EditText) promptsView.findViewById(R.id.passwordInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Log In",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				//result.setText(userInput.getText());
			    editor.putString(USERNAME, un.getText().toString());
			    editor.putString(PASSWORD, pass.getText().toString());
			    editor.commit();
			    
			    username = pref.getString(USERNAME, null);
		        password = pref.getString(PASSWORD, null);
			    }
			  })
			/*.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  })*/;

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
    }
    
    //checks if the username editfield and the the password editfield have text values
    //returns false if it doesnt true otherwise
    public boolean validateCredentials(){
    	if(username.equals("") || password.equals(""))
    		return false; 
    	return true;
    }
    
    //if the edit texts are empty it will do nothing and Toast "Invalid Login..."
    //else it will Toast updating..., fetch the data, and push it to the view
    public void fetch(boolean validated) throws ClientProtocolException, IOException{
    	if(!validated){ 
    		
    		Toast.makeText(getBaseContext(), "Invalid Login...", Toast.LENGTH_SHORT).show(); 
    		
    	}
    	else{
        	//build API
        	String CN = cardNumber.getText().toString().replaceAll("[^0-9]", "");
        	
        	API = "http://emeraldsiren.com/" + username + "/" + password + "/glance";
        	//APIimage = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=QRCODE&qrdata_type=text&qr_btext_text=&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=" + CN + "&height=245&scale=&bgcolor=%23ffffff&color=%23000000&file=&type=jpg&folder=";
        	APIimage = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=QRCODE&qrdata_type=text&qr_btext_text=" + CN + "&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=123&height=500&scale=1&bgcolor=%23ffffff&color=%23000000&file=&folder=";
        	System.err.println("=== " + API);
        	
        	Toast.makeText(getBaseContext(), "Updating...", Toast.LENGTH_LONG).show();
        	
        	//call AsynTask to perform network operation on separate thread
        	new HttpImageAsyncTask().execute(APIimage);
        	new HttpAsyncTask().execute(API);
    	}
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Show which account the user is logged into and allow them the log out
        int id = item.getItemId();
        if (id == R.id.action_myaccount) {
        	
        	// get prompts.xml view
    		LayoutInflater li = LayoutInflater.from(this);
    		View promptsView = li.inflate(R.layout.activity_myaccount, null);

    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
    				this);

    		// set prompts.xml to alertdialog builder
    		alertDialogBuilder.setView(promptsView);

    		final TextView details = (TextView) promptsView.findViewById(R.id.myAccountDetails);
    		details.setText("Logged in as: " + username + " (password: " + password + ")");

    		// set dialog message
    		alertDialogBuilder
    			.setCancelable(false)
    			.setPositiveButton("Log Out",
    			  new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog,int id) {
    				// get user input and set it to result
    				// edit text
    				//result.setText(userInput.getText());
    			    editor.putString(USERNAME, null);
    			    editor.putString(PASSWORD, null);
    			    editor.commit();
    			    
    			    getCreds();
    			    
    			    username = pref.getString(USERNAME, null);
    		        password = pref.getString(PASSWORD, null);
    			    }
    			  })
    			.setNegativeButton("Close",
    			  new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog,int id) {
    				dialog.cancel();
    			    }
    			  });

    		// create alert dialog
    		AlertDialog alertDialog = alertDialogBuilder.create();

    		// show it
    		alertDialog.show();
        	
            return true;
        } else if (id == R.id.action_addcard) {
        	addCard();
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //HTTP GET FOR BARCODE
    public static Bitmap GETImage(String url){
		InputStream inputStream = null;
		Bitmap result = null;
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if(inputStream != null)
				result = BitmapFactory.decodeStream(inputStream);
			else
				result = null;

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

    //ASYNC TASK FOR BARCODE
    private class HttpImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            return GETImage(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(Bitmap result) {
        	imageResponse.setImageBitmap(result);
       }
    }
    
    //HTTP GET FOR TEXT
    public static String GET(String url){
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

    //CONVERTS INPUT STREAM TO STRING
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    
    //ASYNC TASK FOR TEXT
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	response.setText(result);
        	
        	//create new notification
        	sendNotification();
       }
    }
    
    @SuppressLint("NewApi") private void sendNotification(){
    	//create intents
    	final Intent emptyIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    			
    	//create big notification action
    	//Intent dismissIntent = new Intent(this, );
    	
    	
    	//expanded barcode image notification
    	NotificationCompat.BigPictureStyle notiStyle = new 
    	        NotificationCompat.BigPictureStyle();
    	
    	notiStyle.setBigContentTitle("WearBucks");
    	
    	//change to primary card (default first card in list)
    	notiStyle.setSummaryText("Barcode for card ending in " + cardNumber.getText().toString().substring(cardNumber.getText().toString().length()-4));
    	notiStyle.bigPicture( ((BitmapDrawable) imageResponse.getDrawable()).getBitmap() );
    	
    	NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	notiManager.cancel(NOTIFICATION_ID);
    	
    	//////
    	PendingIntent dismissPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				MainActivity.class), 0);
    	
    	//compact notification showing stats
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(this)
    		    .setSmallIcon(R.drawable.wearbucks_logo)
    		    .setContentTitle("WearBucks")
    		    .setContentText(response.getText().toString() + "\n" + "card (" + cardNumber.getText().toString().substring(cardNumber.getText().toString().length()-4) + ")")
    		    .setContentIntent(pendingIntent)
    		    .setStyle(notiStyle)
    		    //.setOngoing(true)
    		    .addAction(R.drawable.wearbucks_logo, "dismiss", dismissPendingIntent)
    		    ;
    	
    	//build and send notification
    	notiManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
