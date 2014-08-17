package com.wearbucks.app;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView mTextView;
	private final String USER_AGENT = "Mozilla/5.0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		String res = "hi";
		try {
			res = sendGet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final String doThis = res;
		
		System.err.println("===== " + doThis);
		
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				mTextView = (TextView) stub.findViewById(R.id.text);
				
				mTextView.setText(doThis);
			}
		});
	}
	
	// HTTP GET request
		private String sendGet() throws Exception {
	 
			String url = "http://www.emeraldsiren.com/tylerl0706@gmail.com/lancer0706/glance";
	 
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			System.out.println(EntityUtils.getContentCharSet(entity));
	        
			System.err.println("~~~~ " + response.toString());
			
	        return response.toString();
	 
		}
}
