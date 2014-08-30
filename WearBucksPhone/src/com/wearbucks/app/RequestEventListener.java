package com.wearbucks.app;

import org.json.JSONObject;

public interface RequestEventListener {
	public void onEventCompleted(JSONObject js);
    public void onEventFailed();
}