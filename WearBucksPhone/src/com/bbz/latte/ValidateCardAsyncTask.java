package com.bbz.latte;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class ValidateCardAsyncTask extends AsyncTask<String, Void, Void> {

	private boolean invalid;
	private RequestEventListener callback;
	private DialogInterface dialog;
	private String cardnumber;
	private String pin;
	private int idx;

	
	public ValidateCardAsyncTask(RequestEventListener callback, DialogInterface d) {
		this.callback = callback;
		dialog = d;
		
	}
	
	public ValidateCardAsyncTask(DialogInterface d, int color) {
		dialog = d;
		idx = color;
	}

	@Override
	protected void onPostExecute(Void aVoid) {

		if(!MainActivity.isConnected()){
			Toast.makeText(MainActivity.context, "No Internet Connection", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		
		
		if(dialog != null && !invalid){
			CardManager.saveNewCard(cardnumber, idx, pin);
			
			CardManager.addNewCard(cardnumber, idx, pin);
			
			for(int i = 0; i < MainActivity.activeCards.size(); i++){
				if(!MainActivity.activeCards.get(i).getPin().equals("nopin"))
					new BalAsyncTask().execute(i);
			}
			
			dialog.dismiss();
		}
		else {
			MainActivity.showError("Incorrect card", "Please check card number and pin");
		}
		if(callback != null) callback.onValidateCard(!invalid);
		
	}

	@Override
	protected Void doInBackground(String... params) {
		
		if(!MainActivity.isConnected())
			return null;
		
		cardnumber = params[0];
		pin = params[1];
		// TODO Auto-generated method stub
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost("https://morning-island-3422.herokuapp.com/cardbalance");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		StringEntity se = null;
		String hr = null;
		
		String send = "{\"cardnumber\":\"" + params[0] +
				"\",\"pinnumber\":\"" + params[1] + 
				"\"}";
		
		try {
			se = new StringEntity(send);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		httpost.setEntity(se);
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");

		try {
			hr = client.execute(httpost, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject js = null;
		boolean v = false;
		try {
			js = new JSONObject(hr);
			v = js.getBoolean("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		invalid = v;
		return null;
	}
	
}
