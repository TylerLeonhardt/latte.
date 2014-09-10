package com.wearbucks.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {
	
	public View view;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.setup_welcome, container, false);
    	
    	TextView textview = (TextView) view.findViewById(R.id.welcome_text);
    	Typeface font = Typeface.createFromAsset(inflater.getContext().getAssets(), "fonts/WeibeiSC.otf");
    	textview.setTypeface(font);
        
    	
    	return view;
    }
}
