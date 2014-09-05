package com.wearbucks.app;

public class Card {

	private String cardNumber;
	private int colorPreference;
	private boolean isDefault;
	private String shortNumber;
	
	public Card(String cardNumber, int colorPreference) {
		this.cardNumber = cardNumber;
		this.colorPreference = colorPreference;
		
		shortNumber = this.cardNumber.substring(this.cardNumber.length()-4);
		
		isDefault = false;
	}
	
	public Card(String cardNumber, int colorPreference, boolean isDefault) {
		this.cardNumber = cardNumber;
		this.colorPreference = colorPreference;
		
		shortNumber = this.cardNumber.substring(this.cardNumber.length()-4);
		
		this.isDefault = isDefault;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	
	public int getColorPreference() {
		return colorPreference;
	}
	
	public String getShortNumber() {
		return shortNumber;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	@Override
	public String toString() {
		return cardNumber + ";" + colorPreference + ";" + (isDefault ? "1" : "0") + "*";
	}

}
