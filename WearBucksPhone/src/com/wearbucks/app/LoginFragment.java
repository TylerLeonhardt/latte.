package com.wearbucks.app;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	public String username;
	public String password;
	public View view;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.setup_login, container, false);
    	
    	// Set default font for password
    	EditText password = (EditText) view.findViewById(R.id.password_input);
    	password.setTypeface(Typeface.DEFAULT);
    	
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
}
