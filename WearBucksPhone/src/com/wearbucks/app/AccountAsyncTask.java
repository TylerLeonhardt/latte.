package com.wearbucks.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AccountAsyncTask extends AsyncTask<Void, Void, Void>{

	
	private RequestEventListener callback;
	private String response, request;
	
	
    public AccountAsyncTask(RequestEventListener cb, String r) {
        callback = cb;
        request = r;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    	
    	JSONObject js = null;
    	String s = null;
		try {
			js = new JSONObject(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			s = js.getString("error");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        if(callback != null) {
        	if(s == "false"){
        		callback.onEventCompleted(response);
        	}else{
        		callback.onEventFailed();
        	}
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		DefaultHttpClient client = new DefaultHttpClient();
    	
    	HttpPost httpost = new HttpPost("http://aqueous-reaches-7492.herokuapp.com/account");
    	
    	System.out.println(request);
    	
    	StringEntity se = null;
		try {
			se = new StringEntity(request);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	httpost.setEntity(se);
    	
    	httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");
        
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        
        String hr = null;
		try {
			hr = client.execute(httpost, responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println(hr);
        response = hr;
        
		return null;
	}
}