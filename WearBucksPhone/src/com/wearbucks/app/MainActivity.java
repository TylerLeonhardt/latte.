package com.wearbucks.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements OnRefreshListener, RequestEventListener{
	
	// User's credentials to index database
	public static String NAME = "name";
	public static String REWARDS = "rewards";
	public static String STARS = "stars";
	public static String BALANCE = "balance";
	
	public static String USERNAME = "username";
	public static String PASSWORD = "password";
	public static String DEFAULTCARD = "dcard";	
	public static String LISTOFCARDS = "listofcards";	//format: "*16DigitCardNumber;CustomColor*16DigitCardNumber;CustomColor*"
	
	private String data_name = null;
	private String data_rewards = null;
	private String data_stars = null;
	private String data_balance = null;
	
	private String data_username = null;
	private String data_password = null;
	private String data_defaultcard = null;	
	private String data_listofcards = null;
	
	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;
	
	public TextView temp;
	private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set action bar color
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        
        pref = this.getPreferences(Context.MODE_PRIVATE);
        editor = pref.edit();
        
        this.loadResources();
        
        //editor.clear().commit();	//remove on launch
        if (data_username == null || data_password == null) {        
	        //TODO: add check if already have user sharedprefs
	        Intent intent = new Intent(this, SetupInitialActivity.class);
	        startActivity(intent);
        }
        
        printValues();
        
        
        //this.loadResources();
        
        // Display user credentials for now
        temp = (TextView) findViewById(R.id.scrollTextView);
        temp.setText(USERNAME + " -> " + PASSWORD + DEFAULTCARD + LISTOFCARDS);
        
      ///You will setup the action bar with pull to refresh layout
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
          .allChildrenArePullable()
          .listener(this)
          .setup(mPullToRefreshLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.saveResources();
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

    private void loadResources(){
    	data_name = pref.getString(NAME, null);
    	data_rewards = pref.getString(REWARDS, null);
    	data_stars = pref.getString(STARS, null);
    	data_balance = pref.getString(BALANCE, null);
    	data_username = pref.getString(USERNAME, null);
    	data_password = pref.getString(PASSWORD, null);
    	data_defaultcard = pref.getString(DEFAULTCARD, null);	
    	data_listofcards = pref.getString(LISTOFCARDS, null);
    }
    
    private void saveResources(){
    	editor.putString(USERNAME, data_username);
        editor.putString(PASSWORD, data_password);
        editor.putString(NAME, data_name);
        editor.putString(REWARDS, data_rewards);
        editor.putString(DEFAULTCARD, data_defaultcard);
        editor.putString(STARS, data_stars);
        editor.putString(LISTOFCARDS, data_listofcards);
        editor.putString(BALANCE, data_balance);
    }
    

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		
		String request = "{\"username\":\""+data_username+"\",\"password\":\""+data_password+"\"}";
		
		new AccountAsyncTask(this, request, mPullToRefreshLayout).execute();
		
	}

	@Override
	public void onEventCompleted(JSONObject js) {
		// TODO Auto-generated method stub
		System.out.println("IT WORKED\n" + js.toString());
		
    	try {
    		String stringBalance = js.getString("dollar_balance");
			data_balance = stringBalance.substring(0, stringBalance.indexOf(".") + 2);
			data_name = js.getString("customer_name");
	    	data_rewards = js.getString("rewards");
	    	String stringStars = js.getString("stars");
	        data_stars = "" + ((stringStars == null) ? 0 : Integer.parseInt(stringStars)%12);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	printValues();
	}
	
	//temp
	public void printValues(){
		System.out.println("\n"+data_name);
		System.out.println(data_username);
		System.out.println(data_password);
		System.out.println(data_stars);
		System.out.println(data_rewards);
		System.out.println(data_balance);
		System.out.println(data_defaultcard);
		System.out.println(data_listofcards);
	}

	@Override
	public void onEventFailed() {
		// TODO Add Popup to login
		
	}

}
