package com.wearbucks.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddCardFragment extends Fragment {
	
	public View view;
	public String cardNumber;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.setup_addcard, container, false);
    }
    
    public boolean isValid() {
    	String cardNumber = ((EditText) view.findViewById(R.id.card_input)).getText().toString();
    	
    	//TODO: make a better validation check
    	if(cardNumber.length() < 16) {
    		return false;
    	}
    	
    	this.cardNumber = cardNumber;
    	
    	return true;
    }
}
