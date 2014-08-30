package com.wearbucks.app;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	public String username;
	public String password;
	public View view;
	
	public Button backButton;
	public Button continueButton;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.setup_login, container, false);
    	
    	// Set default font for password
    	EditText password = (EditText) view.findViewById(R.id.password_input);
    	password.setTypeface(Typeface.DEFAULT);
    	
    	// Get buttons to disable / enable later
    	backButton = (Button) view.findViewById(R.id.back_to_welcome);
    	continueButton = (Button) view.findViewById(R.id.continue_to_add_card);
    	
    	// Inflate the layout for this fragment
        return view;
    }
    
    public boolean isValid() {
    	String username = ((EditText) view.findViewById(R.id.username_input)).getText().toString();
    	String password = ((EditText) view.findViewById(R.id.password_input)).getText().toString();
    	
    	//TODO: make a better validation check
    	if(username.equals("") || password.equals("") || username.equals(null) || password.equals(null)) {
    		return false;
    	}
    	
    	this.username = username;
    	this.password = password;
    	
    	return true;
    }
    
    public void disableButtons() {
    	backButton.setEnabled(false);
    	continueButton.setEnabled(false);
    }
    
    public void enableButtons() {
    	backButton.setEnabled(true);
    	continueButton.setEnabled(true);
    }
    
    public String getJsonString(){
    	
    	return "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
    }
}
