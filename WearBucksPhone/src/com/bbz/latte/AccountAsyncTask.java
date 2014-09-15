package com.bbz.latte;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.os.AsyncTask;

public class AccountAsyncTask extends AsyncTask<Void, Void, Void> {

	private RequestEventListener callback;
	private String response, request;
	private PullToRefreshLayout pullToRefreshLayout;

	public AccountAsyncTask(RequestEventListener callback, String request, PullToRefreshLayout pullToRefresh) {
		this.callback = callback;
		this.request = request;
		this.pullToRefreshLayout = pullToRefresh;
	}

	@Override
	protected void onPostExecute(Void aVoid) {

		JSONObject js = null;
		String s = null;
		try {
			js = new JSONObject(response);
			s = js.getString("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (callback != null) {
			if (s.equals("false")) {
				callback.onEventCompleted(js);
			} else {
				callback.onEventFailed();
			}
		}
		
		if (pullToRefreshLayout != null) {
			pullToRefreshLayout.setRefreshComplete();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost("http://aqueous-reaches-7492.herokuapp.com/account");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		StringEntity se = null;
		String hr = null;
		
		try {
			se = new StringEntity(request);
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

		response = hr;

		return null;
	}
}