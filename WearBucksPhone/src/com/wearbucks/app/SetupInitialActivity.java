package com.wearbucks.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SetupInitialActivity extends FragmentActivity {
	
	public LoginFragment loginFragment; 
	public AddCardFragment addCardFragment;
	
	// User's credentials
	public SharedPreferences pref;
	public SharedPreferences.Editor editor;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_initial);
        
        // Set action bar color
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        
        loginFragment = new LoginFragment();
        addCardFragment = new AddCardFragment();
        
        pref = MainActivity.pref;
        editor = MainActivity.editor;
        
        if (savedInstanceState == null) {
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_location, new WelcomeFragment());
            ft.commit();
        }
	}
	
	public void replaceFragment(View v) {
		
		int viewId = v.getId();
		
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        
        if(viewId == R.id.continue_to_login){
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
            ft.replace(R.id.frame_location, loginFragment);
            
        } else if(viewId == R.id.continue_to_add_card){
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        	
        	if(loginFragment.isValid()) {
                ft.replace(R.id.frame_location, addCardFragment);
        	} else {
        		showError("Incorrect login", "Please check username and password");
        	}
        	
        } else if(viewId == R.id.continue_to_account_summary){  
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        	
        	if(addCardFragment.isValid()) {
                ft.replace(R.id.frame_location, new SummaryFragment());
        	} else {
        		showError("Incorrect card", "Please check card number");
        	}
        	
        } else if(viewId == R.id.continue_to_main){
        	saveUserData();
        	
        	Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        	
        } else if(viewId == R.id.back_to_welcome){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, new WelcomeFragment());
            
        } else if(viewId == R.id.back_to_login){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, loginFragment);
            
        } else if(viewId == R.id.back_to_addcard){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, addCardFragment);
        }
        
        ft.commit();
	}
	
	public void saveUserData() {
		editor.putString(MainActivity.USERNAME, loginFragment.username);
		editor.putString(MainActivity.PASSWORD, loginFragment.password);
		editor.putString(MainActivity.DEFAULTCARD, addCardFragment.cardNumber);
		editor.putString(MainActivity.LISTOFCARDS, "*" + addCardFragment.cardNumber + ";" + addCardFragment.selectedColor + "*");
		
		editor.commit();
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
	
	// disables back button in the setup
	@Override
	public void onBackPressed() {
	}
}
