package com.bbz.latte;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.Toast;

public class BalAsyncTask extends AsyncTask<Integer,Void,Void>{

	public BalAsyncTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		
		if(!MainActivity.isConnected()){
			return null;
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost("https://morning-island-3422.herokuapp.com/cardbalance");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		StringEntity se = null;
		String hr = null;
		
		String send = "{\"cardnumber\":\"" + MainActivity.activeCards.get(params[0]).getCardNumber() +
				"\",\"pinnumber\":\"" + MainActivity.activeCards.get(params[0]).getPin() + 
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
		String s = null;
		try {
			js = new JSONObject(hr);
			s = js.getString("cardbalance");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Double balanceFormatted = Double.valueOf(s);
		String moneyString = NumberFormat.getCurrencyInstance().format(balanceFormatted);
		MainActivity.activeCards.get(params[0]).setBal(moneyString);
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void aVoid) {
		if(!MainActivity.isConnected()){
			Toast.makeText(MainActivity.context, "No Internet Connection", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		MainActivity.adapter.notifyDataSetChanged();
	}
	

}
