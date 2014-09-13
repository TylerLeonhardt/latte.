package com.wearbucks.app;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
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
import android.widget.Toast;


public class MainActivity extends ListActivity implements OnRefreshListener, RequestEventListener{
	
	// User's credentials to index database
	public static String NAME = "name";
	public static String REWARDS = "rewards";
	public static String STARS = "stars";
	public static String BALANCE = "balance";
	
	public static String USERNAME = "username";
	public static String PASSWORD = "password";
	public static String DEFAULTCARD = "dcard";	
	// Stores a set of Card
	public static String LISTOFCARDS = "listofcards";	//format: "*16DigitCardNumber;CustomColor*16DigitCardNumber;CustomColor*"
	
	// Actual list of cards storing locally
	public static ArrayList<Card> activeCards;
	
	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;
	
	public final static int NOTIFICATION_ID = 12;
	
	public TextView temp;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	// Layout elements
	public TextView rewardsNumber;
	public TextView starsNumber;
	public TextView balanceNumber;
	
	public RadioGroup cards;
	
	public static Context context;
	public static Object systemService;
	
	/** Items entered by the user is stored in this ArrayList variable */
    public ArrayList<String> list;
 
    /** Declaring an ArrayAdapter to set items to ListView */
    public static CardAdapter adapter;
    
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        systemService = getSystemService(Context.NOTIFICATION_SERVICE);
        
        // SharedPreferences
        pref = this.getPreferences(Context.MODE_PRIVATE);
        editor = pref.edit();
        
        //editor.clear().commit();	//remove on launch
        
        // Run setup on first launch
        if (pref.getString(USERNAME, null) == null || pref.getString(PASSWORD, null) == null) {        
	        //TODO: add check if already have user sharedprefs
	        Intent intent = new Intent(this, SetupInitialActivity.class);
	        startActivity(intent);

        }else{
        	new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), this, systemService).execute();
        	
        }
        
        initializeCards();
 
        // 1. pass context and data to the custom adapter
        adapter = new CardAdapter(this, activeCards);
 
        // 2. Get ListView from activity_main.xml
        listView = (ListView) findViewById(android.R.id.list);
 
        // 3. setListAdapter
        listView.setAdapter(adapter);
        
        // Set listview's footer for copyright info
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.copyright_footer, null, false);
        listView.addFooterView(footerView);
        
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
    
    private void initializeCards() {
		String current = pref.getString(LISTOFCARDS, "*");
		activeCards = new ArrayList<Card>();
		
		// Empty; no cards saved yet (usually not possible)
		if (current.length() == 1) {
		} else {
			String[] cards = current.split("\\*");
			
			for (int i = 1; i < cards.length; i++) {
				
				// Get data from it
				String[] cardData = cards[i].split(";");
				
				for (String s : cardData) System.out.print(" --- " + s);
				
				boolean def = cardData[2].equals("1") ? true : false;
				
				activeCards.add(new Card(cardData[0], Integer.parseInt(cardData[1]), def));
			}
		}		
	}
    
    private static void saveCards() {
    	String allCards = "*";
    	for (Card c : activeCards) {
    		allCards = allCards + c.toString();
    	}
    	
    	editor.putString(LISTOFCARDS, allCards);
    	editor.commit();
    }

	public static void addNewCard(String cardNumber, int idx) {
        if (activeCards.size() == 0) {
        	activeCards.add(new Card(cardNumber, idx, true));
        } else {
        	activeCards.add(new Card(cardNumber, idx));
        }
        adapter.notifyDataSetChanged();
    }
	
	public static void deleteCard(final String cardNumber) {
		
		if (activeCards.size() == 1) {
			showDeleteError("Error", "Can't remove your only card!");
		} else {
			new AlertDialog.Builder(context)
		    .setTitle("Removing card")
		    .setMessage("Are you sure you want to remove this card from latte.?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	for(Card c : activeCards) {
		    			if(c.getShortNumber().equals(cardNumber)) {
		    				activeCards.remove(c);
		    				
		    				//make the next card a default automatically
		    				if (c.isDefault() && !activeCards.isEmpty()) {
		    					setDefault(activeCards.get(0));
		    				}
		    				
		    				break;
		    				
		    			}
		    		}
		    		
		    		saveCards();
		    		adapter.notifyDataSetChanged();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
		}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        saveCards();
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
    
    private void updateDataViews() {        
//    	String getBalance = pref.getString(BALANCE, null);
//    	String balanceString;
//    	if (getBalance != null ) {
//    		Double balanceFormatted = Double.valueOf(getBalance);
//    		balanceString = String.format("%.2f", balanceFormatted);
//    	} else {
//    		balanceString = "$0.00";
//    	}
    	
    	rewardsNumber.setText(pref.getString(REWARDS, ""));
    	//balanceNumber.setText("$" + balanceString);
    	balanceNumber.setText(pref.getString(BALANCE, "$0.00"));
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

	@Override
	public void onEventFailed() {}
	
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
				    	
				    	saveNewCard(cardNumber, idx);
				    	
				    	addNewCard(cardNumber, idx);
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
	
	public static void saveNewCard(String cardNumber, int colorIndex) {
		String currentCards = pref.getString(LISTOFCARDS, "*");
		currentCards = currentCards + cardNumber + ";" + colorIndex + ";0*";
		
		editor.putString(LISTOFCARDS, currentCards);
		editor.commit();
		
		System.err.println(pref.getString(LISTOFCARDS, null));
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
	
	public static void setDefault(Card card) {
		//set all to false (there is some optimization here)
		for(Card d : activeCards) d.setDefault(false);
		
		//set the new default
		card.setDefault(true);
		
		//save the data
		editor.putString(DEFAULTCARD, card.getCardNumber());
		saveCards();
		
		adapter.notifyDataSetChanged();
		

		Toast.makeText(context, "Default card set!", Toast.LENGTH_SHORT).show();
		
		//create the new notification
		new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), context, systemService).execute();
	}
	
	public void makeDefault(View v){
		
		TextView cardShort = (TextView) ((View) v.getParent()).findViewById(R.id.card_short_number);
		
		for(Card c: activeCards){
			//Checks if the button corresponds to a certain card
			if(cardShort.getText().toString().equals(c.getShortNumber())){
				
				//If it's not the default...
				if(!c.isDefault()){
					setDefault(c);
					
				}else{
					//if it is the default, send a notification
					new BarcodeAsyncTask(pref.getString(DEFAULTCARD, null), this, systemService).execute();
				}
			}
		}
	}
}
