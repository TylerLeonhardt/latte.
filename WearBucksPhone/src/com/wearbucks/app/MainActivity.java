package com.wearbucks.app;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


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
	public ArrayList<Card> activeCards;
	
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
    public CardAdapter adapter;

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
        }
        
        initializeCards();
 
        // 1. pass context and data to the custom adapter
        adapter = new CardAdapter(this, activeCards);
 
        // 2. Get ListView from activity_main.xml
        ListView listView = (ListView) findViewById(android.R.id.list);
 
        // 3. setListAdapter
        listView.setAdapter(adapter);
        
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
        
        //cards = (RadioGroup) findViewById(R.id.cardgroup);
    }
    
    private void initializeCards() {
		String current = pref.getString(LISTOFCARDS, "*");
		System.out.println("onCreate() currently saved: " + current);
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
				
				System.out.println("data: " + cardData[0] + " isDefault: " + cardData[2]);
				
				activeCards.add(new Card(cardData[0], Integer.parseInt(cardData[1]), def));
			}
		}
		
		System.err.println("onCreate() cards: " + activeCards);
		
	}
    
    private void saveCards() {
    	String allCards = "*";
    	for (Card c : activeCards) {
    		allCards = allCards + c.toString();
    	}
    	
    	editor.putString(LISTOFCARDS, allCards);
    	editor.commit();
    	
    	System.err.println("onDestroy() active cards: " + activeCards);
    	System.err.println("onDestroy() saved cards: " + pref.getString(LISTOFCARDS, "none :("));
    }

	public void addNewCard(String cardNumber, int idx) {
        activeCards.add(new Card(cardNumber, idx));
        adapter.notifyDataSetChanged();
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
	public void onEventFailed() {}
	
	public void showAddNewCard() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
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
			.setPositiveButton("Add Card",
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
			    	}
			    	
			    	int checkedButton = group.getCheckedRadioButtonId();
			    	View radioButton = group.findViewById(checkedButton);
			    	int idx = group.indexOfChild(radioButton);
			    	saveNewCard(cardNumber, idx);
			    	
			    	addNewCard(cardNumber, idx);
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
	
	public void saveNewCard(String cardNumber, int colorIndex) {
		String currentCards = pref.getString(LISTOFCARDS, "*");
		currentCards = currentCards + cardNumber + ";" + colorIndex + "*";
		
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
	
	
// I don't even	
	public void addCard(){
		
		
		
		String cardsString = pref.getString(LISTOFCARDS, null);
		
		if(cardsString != null){
		
		String[] cardsInfo = cardsString.split("\\*");
		
		//String newCard = cardsInfo[cardsInfo.length-2].split(";")[0];
		String newCard = cardsInfo[1].substring(0, cardsInfo[1].indexOf(";"));
		
		RadioButton rb = new RadioButton(this);
		rb.setId(Integer.parseInt(newCard.substring(newCard.length()-5, newCard.length())));
		rb.setOnClickListener(new BarcodeOnClickListener(Integer.parseInt(newCard.substring(newCard.length()-5, newCard.length()))));
		rb.setText(newCard);
		rb.setChecked(true);
		
		cards.addView(rb, cards.getChildCount());
		}
		
	}
	
	public class BarcodeOnClickListener implements OnClickListener
	{

	     int barcodeNum;
	     
	     public BarcodeOnClickListener(int n) {
	          barcodeNum = n;
	     }

	     @Override
	     public void onClick(View v)
	     {
	    	 new BarcodeAsyncTask(barcodeNum,getApplicationContext()).execute();
	     }

	}
	
	public void showError(String error, String helpText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
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
				
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void sendNotification(int barcodeNumber, Bitmap barcodeImage){
    	//create intents
    	final Intent emptyIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    			
    	//create big notification action
    	//Intent dismissIntent = new Intent(this, );
    	
    	
    	//expanded barcode image notification
    	NotificationCompat.BigPictureStyle notiStyle = new 
    	        NotificationCompat.BigPictureStyle();
    	
    	notiStyle.setBigContentTitle("WearBucks");
    	notiStyle.setSummaryText("Barcode for card ending in " + ("" + barcodeNumber).substring(("" + barcodeNumber).length()-4));
    	notiStyle.bigPicture( barcodeImage );
    	
    	NotificationManager notiManager = (NotificationManager) systemService;
    	notiManager.cancel(NOTIFICATION_ID);
    	
    	//////
    	PendingIntent dismissPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context,
				MainActivity.class), 0);
    	
    	//compact notification showing stats
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(context)
    		    .setSmallIcon(R.drawable.wearbucks_logo)
    		    .setContentTitle("WearBucks")
    		    .setContentText("card (" + ("" + barcodeNumber).substring(("" + barcodeNumber).length()-4) + ")")
    		    .setContentIntent(pendingIntent)
    		    .setStyle(notiStyle)
    		    //.setOngoing(true)
    		    .addAction(R.drawable.wearbucks_logo, "dismiss", dismissPendingIntent)
    		    ;
    	
    	//build and send notification
    	notiManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
