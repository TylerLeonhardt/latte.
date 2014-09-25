package com.bbz.latte;

import org.json.JSONObject;

public interface RequestEventListener {
	public void onEventCompleted(JSONObject js);
    public void onEventFailed();
    public void onValidateCard(boolean isValid);
}