package com.wearbucks.app;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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
        if (id == R.id.new_card) {
        	showAddNewCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void updateDataViews() {        
    	String getBalance = pref.getString(BALANCE, null);
    	String balanceString;
    	if (getBalance != null ) {
    		Double balanceFormatted = Double.valueOf(getBalance);
    		balanceString = String.format("%.2f", balanceFormatted);
    	} else {
    		balanceString = "$0.00";
    	}
    	
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
	
	public void showAddNewCard() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_new_card, null);
		
		View cardView = promptsView.findViewById(R.id.add_new_card_generic_popup); 
		
		// Get all card elements
        EditText cardNumber1 = ((EditText) cardView.findViewById(R.id.card_input_1));
        EditText cardNumber2 = ((EditText) cardView.findViewById(R.id.card_input_2));
        EditText cardNumber3 = ((EditText) cardView.findViewById(R.id.card_input_3));
        EditText cardNumber4 = ((EditText) cardView.findViewById(R.id.card_input_4));
        
        EditText[] listOfSegments = {cardNumber1, cardNumber2, cardNumber3, cardNumber4};
        		
		for (int i = 0; i < listOfSegments.length; i++) {
			setListenerSegment(listOfSegments, i);
		}

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Add Card",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				
			    }
			  })
			  .setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					
				    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void setListenerSegment(final EditText[] listOfSegments, final int position) {
    	listOfSegments[position].addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	
	            if (listOfSegments[position].getText().toString().length() == AddCardFragment.LENGTH_SEGMENT && position != AddCardFragment.NUMBER_OF_SEGMENTS-1) {
	            	listOfSegments[position+1].requestFocus();
	            }
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
	    }); 
    }

}
