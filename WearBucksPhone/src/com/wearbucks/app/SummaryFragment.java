package com.wearbucks.app;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SummaryFragment extends Fragment {
    
	// Layout elements
	public View view;
	public TextView rewardsNumber;
	public TextView starsNumber;
	public TextView name;
	public TextView defaultCardNumber;
	public ImageView cardColor;
	
	// User's credentials
	public SharedPreferences pref;
	public SharedPreferences.Editor editor;
	
	// Card colors array (order matches order in setup_addcard.xml RadioGroup)
	TypedArray colors;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setup_summary, container, false);

        pref = MainActivity.pref;
        editor = MainActivity.editor;
        colors = getResources().obtainTypedArray(R.array.card_colors);
        
        rewardsNumber = (TextView) view.findViewById(R.id.rewards_number);
        starsNumber = (TextView) view.findViewById(R.id.stars_number);
        name = (TextView) view.findViewById(R.id.name_text);
        defaultCardNumber = (TextView) view.findViewById(R.id.default_card_number);
        cardColor = (ImageView) view.findViewById(R.id.credit_card_color);
        
        // Calculate the actual number of stars until the next reward
        String stringStars = pref.getString(MainActivity.STARS, null);
        int numStars = (stringStars == null) ? 0 : Integer.parseInt(stringStars)%12;
        editor.putString(MainActivity.STARS, "" + numStars);
        editor.commit();
        
        System.err.println("summary: " + numStars);
        
        // Set all data fields to match API response
        rewardsNumber.setText(String.valueOf(pref.getString(MainActivity.REWARDS, null)));
        starsNumber.setText(String.valueOf(numStars));
        name.setText("Welcome " + pref.getString(MainActivity.NAME, "no name").split(" ")[0] + "!");
        
        // Format card from stored value
        String fullCardNumber = pref.getString(MainActivity.DEFAULTCARD, null);
        String formattedCardNumber = fullCardNumber.substring(0, 4) + " - " + fullCardNumber.substring(4, 8) 
        		+ " - " + fullCardNumber.substring(8, 12) + " - " + fullCardNumber.substring(12);
    	defaultCardNumber.setText(formattedCardNumber);
    	
    	// Set color according to selection in setup_addcard.xml
    	String listOfCards = pref.getString(MainActivity.LISTOFCARDS, null);
    	String selectedColor = listOfCards.substring(listOfCards.indexOf(";")+1, listOfCards.indexOf("*", listOfCards.indexOf("*")+1));
    	cardColor.setImageResource(colors.getResourceId(Integer.parseInt(selectedColor), 0));
        
    	return view;
    }
}
