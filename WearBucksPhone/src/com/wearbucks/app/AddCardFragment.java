package com.wearbucks.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

public class AddCardFragment extends Fragment {
	
	public View view;
	public final int LENGTH_SEGMENT = 4;
	public final int NUMBER_OF_SEGMENTS = 4;
	
	public String cardNumber;
	public int selectedColor = 0;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_addcard, container, false);
        
        final EditText cardNumber1 = ((EditText) view.findViewById(R.id.card_input_1));
        final EditText cardNumber2 = ((EditText) view.findViewById(R.id.card_input_2));
        final EditText cardNumber3 = ((EditText) view.findViewById(R.id.card_input_3));
        final EditText cardNumber4 = ((EditText) view.findViewById(R.id.card_input_4));
        
        EditText[] listOfSegments = {cardNumber1, cardNumber2, cardNumber3, cardNumber4};
        		
		for (int i = 0; i < listOfSegments.length; i++) {
			setListenerSegment(listOfSegments, i);
		}
    	
    	return view;
    }
    
    public void setListenerSegment(final EditText[] listOfSegments, final int position) {
    	listOfSegments[position].addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	
	        	System.err.println("== " + listOfSegments[position].getText().toString().length());
	        	
	            if (listOfSegments[position].getText().toString().length() == LENGTH_SEGMENT && position < NUMBER_OF_SEGMENTS) {
	            	listOfSegments[position+1].requestFocus();
	            } else if (listOfSegments[position].getText().toString().length() == 0 && position != 0) {
	            	listOfSegments[position-1].requestFocus();
	            }
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
	    }); 
    }
    
    public boolean isValid() {
    	String cardNumber1 = ((EditText) view.findViewById(R.id.card_input_1)).getText().toString();
    	String cardNumber2 = ((EditText) view.findViewById(R.id.card_input_2)).getText().toString();
    	String cardNumber3 = ((EditText) view.findViewById(R.id.card_input_3)).getText().toString();
    	String cardNumber4 = ((EditText) view.findViewById(R.id.card_input_4)).getText().toString();
    	
    	String cardNumber = cardNumber1 + cardNumber2 + cardNumber3 + cardNumber4;
    	
    	//TODO: make a better validation check
    	if(cardNumber.length() < 16) {
    		return false;
    	}
    	
    	this.cardNumber = cardNumber;
    	
    	RadioGroup group = (RadioGroup) view.findViewById(R.id.card_color_group);
    	int checkedButton = group.getCheckedRadioButtonId();
    	View radioButton = group.findViewById(checkedButton);
    	int idx = group.indexOfChild(radioButton);
    	
    	selectedColor = idx;
    	
    	return true;
    }
}
