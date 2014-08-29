package com.wearbucks.app;

public interface RequestEventListener {
	public void onEventCompleted(String val);
    public void onEventFailed();
}