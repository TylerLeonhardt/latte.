package com.wearbucks.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class HandleNotificationActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	notiManager.cancel(MainActivity.NOTIFICATION_ID);
    	
    	finish();
	}
}