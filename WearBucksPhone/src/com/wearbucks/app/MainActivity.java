package com.wearbucks.app;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;


@SuppressLint("InflateParams")
public class MainActivity extends ListActivity implements OnRefreshListener, RequestEventListener{
	
	public static final String NAME = "name";
	public static final String REWARDS = "rewards";
	public static final String STARS = "stars";
	public static final String BALANCE = "balance";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String DEFAULTCARD = "dcard";	
	public static final String LISTOFCARDS = "listofcards"; //format: "*16DigitCardNumber;CustomColor;isDefault*16DigitCardNumber;CustomColor;isDefault*"
	
	//for sending notifications
	public final static int NOTIFICATION_ID = 12341234;
	
	//for accessing shared prefs
	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;
	
	//For misc services
	public static Context context;
	public static Object systemService;
	
	// Actual list of cards storing locally and it's listview adapter
	public static ArrayList<Card> activeCards;
    public static CardAdapter adapter;
	
    //layouts and views
	private PullToRefreshLayout mPullToRefreshLayout;
	private TextView rewardsNumber;
	private TextView starsNumber;
	private TextView balanceNumber;
    private ListView listView;

    /***************************************/
    /**            Overrides              **/
    /***************************************/
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //important variables set
        context = this;
        systemService = getSystemService(Context.NOTIFICATION_SERVICE);
        pref = this.getPreferences(Context.MODE_PRIVATE);
        editor = pref.edit();
    
        // If no data exists in Shared prefs, then start the Setup Activity. Else send a notification
        if (pref.getString(USERNAME, null) == null || pref.getString(PASSWORD, null) == null) {        
	        //TODO: add check if already have user sharedprefs
	        Intent intent = new Intent(this, SetupInitialActivity.class);
	        startActivity(intent);

        }else{
        	new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), this, systemService).execute();
        	
        }
        
        //creates the card array list
        CardManager.initializeCards();
 
        //sets up the adapter
        setupListView();
        
        //changes the action bar
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
        
        CardManager.saveCards();
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
        } else if (id == R.id.send_noti) {
        	new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), this, systemService).execute();
            return true;
        } else if (id == R.id.about_app) {
        	showAboutApp();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pull to refresh listener
     */
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		
		String request = "{\"username\":\"" + pref.getString(USERNAME, null) + "\",\"password\":\"" + pref.getString(PASSWORD, null) + "\"}";
		
		new AccountAsyncTask(this, request, mPullToRefreshLayout).execute();
		
	}
	
	/**
	 * Called when the Account Async Task comes back with the right data
	 */
	@Override
	public void onEventCompleted(JSONObject js) {
		// TODO Auto-generated method stub
		try {
    		
    		Double balanceFormatted = js.getDouble("dollar_balance");
			String moneyString = NumberFormat.getCurrencyInstance().format(balanceFormatted);
    			
			editor.putString(BALANCE, moneyString);
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

	/**
	 * Called when the Account Async Task comes back with the wrong data. Impossible.
	 */
	@Override
	public void onEventFailed() {}
	
	/**
	 * Disable soft back button during setup; forces user to use our navigation
	 */
	@Override
	public void onBackPressed() {}
	
	/*************************************************************/
	
	
	
	/**
	 *sets up the adapter
	 */
    private void setupListView(){
    	// 1. pass context and data to the custom adapter
        adapter = new CardAdapter(this, activeCards);
 
        // 2. Get ListView from activity_main.xml
        listView = (ListView) findViewById(android.R.id.list);
 
        // 3. setListAdapter
        listView.setAdapter(adapter);
        
        // Set listview's footer for copyright info
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.copyright_footer, null, false);
        listView.addFooterView(footerView);
    }
    
    
	
	/**
	 * updates the views
	 */
    private void updateDataViews() {        
    	
    	rewardsNumber.setText(pref.getString(REWARDS, ""));
    	balanceNumber.setText(pref.getString(BALANCE, "$0.00"));
    	starsNumber.setText(pref.getString(STARS, ""));
    }
    
    
	/**
	 * Shows the popup to add a new card
	 */
	public static void showAddNewCard() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.add_new_card, null);
		
		View cardView = promptsView.findViewById(R.id.add_new_card_generic_popup); 
		
		// Get all card elements
        final EditText cardNumber1 = ((EditText) cardView.findViewById(R.id.card_input_1));
        final EditText cardNumber2 = ((EditText) cardView.findViewById(R.id.card_input_2));
        final EditText cardNumber3 = ((EditText) cardView.findViewById(R.id.card_input_3));
        final EditText cardNumber4 = ((EditText) cardView.findViewById(R.id.card_input_4));
        
        final RadioGroup group = (RadioGroup) cardView.findViewById(R.id.card_color_group);
        
        EditText[] listOfSegments = {cardNumber1, cardNumber2, cardNumber3, cardNumber4};
        		
		for (int i = 0; i < listOfSegments.length; i++) {
			setListenerSegment(listOfSegments, i);
		}

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder
			.setCancelable(false)
			.setNeutralButton("Add Card",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	String cardSegment1 = cardNumber1.getText().toString();
			    	String cardSegment2 = cardNumber2.getText().toString();
			    	String cardSegment3 = cardNumber3.getText().toString();
			    	String cardSegment4 = cardNumber4.getText().toString();
			    	
			    	String cardNumber = "" + cardSegment1 + cardSegment2 + cardSegment3 + cardSegment4;
			    	
			    	//TODO: make a better validation check
			    	if(cardNumber.length() < 16) {
			    		showError("Incorrect card", "Please check card number");
			    	} else {
			    	
				    	int checkedButton = group.getCheckedRadioButtonId();
				    	View radioButton = group.findViewById(checkedButton);
				    	int idx = group.indexOfChild(radioButton);
				    	
				    	CardManager.saveNewCard(cardNumber, idx);
				    	
				    	CardManager.addNewCard(cardNumber, idx);
				    	dialog.dismiss();
			    	}
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
	
	/**
	 * requests focus to next portion of cardnumber
	 * @param listOfSegments
	 * @param position
	 */
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

	/**
	 * Shows the About pop up
	 */
	public static void showAboutApp() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.about_app, null);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Close",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	/**
	 * Shows an error popup
	 * @param error
	 * @param helpText
	 */
	public static void showError(String error, String helpText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.error_message, null);

		alertDialogBuilder.setView(promptsView);

		final TextView details = (TextView) promptsView.findViewById(R.id.error_details);
		final TextView help = (TextView) promptsView.findViewById(R.id.error_help);
		details.setText(error);
		help.setText(helpText);

		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Try Again",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	showAddNewCard();
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	/**
	 * Shows the delete error
	 * @param error
	 * @param helpText
	 */
	public static void showDeleteError(String error, String helpText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.error_message, null);

		alertDialogBuilder.setView(promptsView);

		final TextView details = (TextView) promptsView.findViewById(R.id.error_details);
		final TextView help = (TextView) promptsView.findViewById(R.id.error_help);
		details.setText(error);
		help.setText(helpText);

		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Okay",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	/**
	 * Finds the card that you want to make the default and calls CardManager.setDefault
	 * @param v
	 */
	public void makeDefault(View v){
		
		TextView cardShort = (TextView) ((View) v.getParent()).findViewById(R.id.card_short_number);
		
		for(Card c: activeCards){
			//Checks if the button corresponds to a certain card
			if(cardShort.getText().toString().equals(c.getShortNumber())){
				
				//If it's not the default...
				if(!c.isDefault()){
					CardManager.setDefault(c);
					
				}else{
					//if it is the default, send a notification
					new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), this, systemService).execute();
				}
			}
		}
	}
}
