package com.wearbucks.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DismissReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        
        System.err.println("bundle: " + notificationId);
        
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
        
        System.err.println("end onReceive");
    }
}
