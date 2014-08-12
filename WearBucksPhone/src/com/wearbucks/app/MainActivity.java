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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
	public EditText username, password, cardNumber;
	public Button request;
	
	//URLs to fetch from
	public String API;
	public String APIimage;
	
	//The data fetched
	public ImageView imageResponse;
	public TextView response;
	
	public boolean responseDone;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //FIND ALL THE VIEWS
        response = (TextView) findViewById(R.id.response);
        request = (Button) findViewById(R.id.request);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        imageResponse = (ImageView) findViewById(R.id.barcode);
                
        //Request Listener
        request.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {
            	//close keyboard
            	InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
            	inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            	
            	//no response yet
            	responseDone = false;
            	
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
            }
        });
    }
    
    //checks if the username editfield and the the password editfield have text values
    //returns false if it doesnt true otherwise
    public boolean validateCredentials(){
    	if(username.getText().toString().equals("") || password.getText().toString().equals(""))
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
        	
        	API = "http://emeraldsiren.com/" + username.getText().toString() + "/" + password.getText().toString() + "/glance";
        	APIimage = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=CODE39&qrdata_type=text&qr_btext_text=&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=" + CN + "&height=245&scale=&bgcolor=%23ffffff&color=%23000000&file=&type=jpg&folder=";
        	
        	System.err.println("=== " + API);
        	
        	Toast.makeText(getBaseContext(), "Updating...", Toast.LENGTH_LONG).show();
        	
        	//call AsynTask to perform network operation on separate thread
        	new HttpImageAsyncTask().execute(APIimage);
        	new HttpAsyncTask().execute(API);
    	}
    }
    
    public Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); 
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
   
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
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
    
    private void sendNotification(){
    	//create new notification
    	final Intent emptyIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    			
    	//expanded barcode image notification
    	NotificationCompat.BigPictureStyle notiStyle = new 
    	        NotificationCompat.BigPictureStyle();
    	
    	notiStyle.setBigContentTitle("WearBucks");
    	notiStyle.setSummaryText("Barcode for card ending in " + cardNumber.getText().toString().substring(cardNumber.getText().toString().length()-4));
    	notiStyle.bigPicture( ((BitmapDrawable) imageResponse.getDrawable()).getBitmap() );
    	
    	//compact notification showing stats
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(this)
    		    .setSmallIcon(R.drawable.wearbucks_logo)
    		    .setContentTitle("WearBucks")
    		    .setContentText(response.getText().toString())
    		    .setContentIntent(pendingIntent)
    		    .setStyle(notiStyle)
    		    .setOngoing(true)
    		    ;
    	
    	//build and send notification
    	NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.notify(99, mBuilder.build());
    }
}
