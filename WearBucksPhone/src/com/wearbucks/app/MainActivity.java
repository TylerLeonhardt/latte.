package com.wearbucks.app;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;
	
	public TextView temp;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	// Layout elements
	public TextView rewardsNumber;
	public TextView starsNumber;
	public TextView balanceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        pref = this.getPreferences(Context.MODE_PRIVATE);
        editor = pref.edit();
        
        //editor.clear().commit();	//remove on launch
        if (pref.getString(USERNAME, null) == null || pref.getString(PASSWORD, null) == null) {        
	        //TODO: add check if already have user sharedprefs
	        Intent intent = new Intent(this, SetupInitialActivity.class);
	        startActivity(intent);
        }
        
        String nameActionBar = pref.getString(NAME, "");
        if (nameActionBar.equals("")) {
        	getActionBar().setTitle("  Welcome!");
        } else {
        	getActionBar().setTitle("  Welcome, " + nameActionBar.split(" ")[0] + "!");
        }
        
        // Display user credentials for now
        rewardsNumber = (TextView) findViewById(R.id.rewards_number_main);
        starsNumber = (TextView) findViewById(R.id.stars_number_main);
        balanceNumber = (TextView) findViewById(R.id.balance_main);
        
        updateDataViews();
        
        // You will setup the action bar with pull to refresh layout
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
          .allChildrenArePullable()
          .listener(this)
          .setup(mPullToRefreshLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    
    private void updateDataViews() {        
    	Double balanceFormatted = Double.valueOf(pref.getString(BALANCE, null));
		String balanceString = String.format("%.2f", balanceFormatted);	
		
    	rewardsNumber.setText(pref.getString(REWARDS, ""));
    	balanceNumber.setText("$" + balanceString);
    	starsNumber.setText(pref.getString(STARS, ""));
    }
    

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		
		String request = "{\"username\":\"" + pref.getString(USERNAME, null) + "\",\"password\":\"" + pref.getString(PASSWORD, null) + "\"}";
		
		new AccountAsyncTask(this, request, mPullToRefreshLayout).execute();
		
	}

	@Override
	public void onEventCompleted(JSONObject js) {
		// TODO Auto-generated method stub
		try {
    		
    		Double balanceFormatted = js.getDouble("dollar_balance");
    		String balanceString = String.format("%.2f", balanceFormatted);	
    			
			editor.putString(BALANCE, balanceString);
			editor.putString(NAME, js.getString("customer_name"));
			editor.putString(REWARDS, js.getString("rewards"));
	    	String stringStars = js.getString("stars");
	    	int numStars = (stringStars == null) ? 0 : Integer.parseInt(stringStars)%12;
	        editor.putString(STARS, "" + numStars);
	        System.err.println("main: " + numStars);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	editor.commit();
    	updateDataViews();
	}

	@Override
	public void onEventFailed() {
		// TODO Add Popup to login		
	}

}
