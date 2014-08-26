package com.wearbucks.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SetupInitialActivity extends FragmentActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_initial);
        
        // Setup the welcome screen        
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_location, new WelcomeFragment());
        ft.commit();
	}
	
	public void replaceFragment() {
		
		System.err.println("replaced fragment");
		// Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        
        // Replace the container with the new fragment
        ft.replace(R.id.frame_location, new LoginFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        
        // Execute the changes specified
        ft.commit();
	}
	
	public class WelcomeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	System.err.println("made welcome");
        	// Inflate the layout for this fragment
            View inf = inflater.inflate(R.layout.setup_welcome, container, false);
            
            // Set the button
            Button continueToLogin = (Button) inf.findViewById(R.id.continue_to_setup);    
            System.err.println("button: " + continueToLogin);
            continueToLogin.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	replaceFragment();
                }
            });
            
            return inf;
        }
    }
	
	public static class LoginFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	System.err.println("made login");
        	// Inflate the layout for this fragment
            return inflater.inflate(R.layout.setup_login, container, false);
        }
    }
	
	// disables back button in the setup
	@Override
	public void onBackPressed() {
	}
}
