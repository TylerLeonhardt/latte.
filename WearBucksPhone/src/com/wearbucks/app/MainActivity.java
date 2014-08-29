package com.wearbucks.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
	// User's credentials to index database
	public static String USERNAME = "username";
	public static String PASSWORD = "password";
	public static String DEFAULTCARD = "dcard";	
	public static String LISTOFCARDS = "listofcards";	//format: "*16DigitCardNumber;CustomColor*16DigitCardNumber;CustomColor*"
	
	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set action bar color
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        
        pref = this.getPreferences(Context.MODE_PRIVATE);
        editor = pref.edit();
        
        editor.clear().commit();	//remove on launch
        
        if (pref.getString(USERNAME, null) == null || pref.getString(PASSWORD, null) == null) {        
	        //TODO: add check if already have user sharedprefs
	        Intent intent = new Intent(this, SetupInitialActivity.class);
	        startActivity(intent);
        }
        
        // Display user credentials for now
        TextView temp = (TextView) findViewById(R.id.temp);
        temp.setText(pref.getString(USERNAME, null) + " -> " + pref.getString(PASSWORD, null) + pref.getString(DEFAULTCARD, null) + pref.getString(LISTOFCARDS, null));
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
}
