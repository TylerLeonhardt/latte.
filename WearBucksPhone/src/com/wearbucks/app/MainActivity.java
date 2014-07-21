package com.wearbucks.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	public TextView response;
	public String API;
	public String APIimage;
	public ImageView imageResponse;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        response = (TextView) findViewById(R.id.response);
        imageResponse = (ImageView) findViewById(R.id.barcode);
        API = "http://emeraldsiren.com/USERNAME/PASSWORD/glance";
        APIimage = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=CODE39&qrdata_type=text&qr_btext_text=&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=9999999999999999&height=50&scale=2&bgcolor=%23ffffff&color=%23000000&file=&type=jpg&folder=";
        //call AsynTask to perform network operation on separate thread
     	new HttpAsyncTask().execute(API);
     	new HttpImageAsyncTask().execute(APIimage);
    }
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
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

    private class HttpImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            return GETImage(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(Bitmap result) {
        	Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        	imageResponse.setImageBitmap(result);

       }
    }
    
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	    if (networkInfo != null && networkInfo.isConnected()) 
    	    	return true;
    	    else
    	    	return false;	
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        	response.setText(result);
       }
    }
}
