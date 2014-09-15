package com.bbz.latte;

import java.text.NumberFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class SetupInitialActivity extends FragmentActivity implements RequestEventListener {

	// All fragmant set up screens
	public LoginFragment loginFragment;
	public AddCardFragment addCardFragment;
	public FragmentTransaction ft;
	public JSONObject response;

	// Shared preferences
	public SharedPreferences pref;
	public SharedPreferences.Editor editor;
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_initial);

		// Create an instance of the fragments that take user input, allowing to
		// store state if the user navigates back to screen
		loginFragment = new LoginFragment();
		addCardFragment = new AddCardFragment();

		// Get resources
		ft = getSupportFragmentManager().beginTransaction();
		pref = MainActivity.pref;
		editor = MainActivity.editor;
		context = this;

		// Show welcome screen
		if (savedInstanceState == null) {
			ft.replace(R.id.frame_location, new WelcomeFragment());
			ft.commit();
		}
//        SpannableString s = new SpannableString("latte.");
//        s.setSpan(new TypefaceSpan(this, "orator.ttf"), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle(s);
	}

	/**
	 * Replaces current fragment (v) with new fragment based on which button is
	 * pressed
	 */
	public void replaceFragment(View v) {

		// Get current view
		int viewId = v.getId();

		// Get resources
		ft = getSupportFragmentManager().beginTransaction();

		// Set animation between fragment transitions
		ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);

		// Go to LoginFragment
		if (viewId == R.id.continue_to_login) {
			ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
			ft.replace(R.id.frame_location, loginFragment);
			ft.commit();

		} else if (viewId == R.id.continue_to_add_card) { // Go to
															// AddCardFragment
			ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);

			try {
				// Only if data from LoginFragment is valid and receives okay
				// from API
				if (loginFragment.isValid()) {
					loginFragment.disableButtons();
					Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT)
							.show();
					new AccountAsyncTask(this, loginFragment.getJsonString(), null).execute();
				} else { // Show error message
					showError("Incorrect login", "Please check username and password");
					loginFragment.enableButtons();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (viewId == R.id.continue_to_account_summary) { // Go to
																	// SummaryFragment
			ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);

			// Only if data from AddCardFragment is valid
			if (addCardFragment.isValid()) {
				saveUserData();
				ft.replace(R.id.frame_location, new SummaryFragment());
				ft.commit();
			} else { // Show error message
				showError("Incorrect card", "Please check card number");
			}

		} else if (viewId == R.id.continue_to_main) { // Go to MainActivity			
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);

		} else if (viewId == R.id.back_to_welcome) { // Go back to
														// WelcomeFragment
			ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
			ft.replace(R.id.frame_location, new WelcomeFragment());
			ft.commit();

		} else if (viewId == R.id.back_to_login) { // Go back to LoginFragment
			ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
			ft.replace(R.id.frame_location, loginFragment);
			ft.commit();

		} else if (viewId == R.id.back_to_addcard) { // Go back to
														// AddCardFragment
			ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
			ft.replace(R.id.frame_location, addCardFragment);
			ft.commit();
		}
	}

	/**
	 * Save initial user data in SharedPreferences
	 */
	public void saveUserData() {
		// User-inputted data
		editor.putString(MainActivity.USERNAME, loginFragment.username);
		editor.putString(MainActivity.PASSWORD, loginFragment.password);
		editor.putString(MainActivity.DEFAULTCARD, addCardFragment.cardNumber);
		editor.putString(MainActivity.LISTOFCARDS, "*" + addCardFragment.cardNumber + ";"
				+ addCardFragment.selectedColor + ";1*");

		// JSON API response data
		try {
			// Format money
			//If it's a new user who hasn't even added a card to their account it will no longer crash
			if(response.getString("dollar_balance").length() >= 10){
				editor.putString(MainActivity.BALANCE, "0.00");
			}else{
				Double balanceFormatted = response.getDouble("dollar_balance");
				String moneyString = NumberFormat.getCurrencyInstance().format(balanceFormatted);
				editor.putString(MainActivity.BALANCE, moneyString);
			}

			editor.putString(MainActivity.NAME, response.getString("customer_name"));
			editor.putString(MainActivity.STARS, response.getString("stars"));
			editor.putString(MainActivity.REWARDS, response.getString("rewards"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		editor.commit();
	}

	/**
	 * Show custom popup error message with corresponding text
	 */
	public void showError(String error, String helpText) {
		// Get resources
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.error_message, null);

		// Set custom layout
		alertDialogBuilder.setView(promptsView);

		// Get layout elements
		final TextView details = (TextView) promptsView.findViewById(R.id.error_details);
		final TextView help = (TextView) promptsView.findViewById(R.id.error_help);
		details.setText(error);
		help.setText(helpText);

		// Close the error message
		alertDialogBuilder.setCancelable(false).setPositiveButton("Try Again",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		// Show message
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	@SuppressLint("WrongViewCast") private void showOverlayTutorial() {
		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setContentView(R.layout.overlay_tutorial);

		LinearLayout layout = (LinearLayout) dialog
				.findViewById(R.id.llOverlay_activity);
		layout.setBackgroundColor(Color.TRANSPARENT);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);

			}

		});

		dialog.show();
	}

	/**
	 * Disable soft back button during setup; forces user to use our navigation
	 */
	@Override
	public void onBackPressed() {
	}

	/**
	 * Show AddCardFragment when JSON API call is successful (used for validating)
	 */
	@Override
	public void onEventCompleted(JSONObject js) {
		// Save user data
		ft.replace(R.id.frame_location, addCardFragment);
		ft.commit();

		response = js;
	}

	/**
	 * Show error message when JSON API call is unsuccessful
	 */
	@Override
	public void onEventFailed() {
		showError("Incorrect login", "Please check username and password");
		loginFragment.enableButtons();
	}

}
