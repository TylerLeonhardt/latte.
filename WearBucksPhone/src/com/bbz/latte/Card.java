package com.bbz.latte;

public class Card {

	private String cardNumber;
	private int colorPreference;
	private boolean isDefault;
	private String shortNumber;
	private String balance;
	private String pin;

	public Card(String cardNumber, int colorPreference, String pin) {
		this.cardNumber = cardNumber;
		this.colorPreference = colorPreference;

		shortNumber = this.cardNumber.substring(this.cardNumber.length() - 4);

		isDefault = false;
		
		balance = "";
		
		setPin(pin);
	}

	public Card(String cardNumber, int colorPreference, boolean isDefault, String pin) {
		this.cardNumber = cardNumber;
		this.colorPreference = colorPreference;

		shortNumber = this.cardNumber.substring(this.cardNumber.length() - 4);

		this.isDefault = isDefault;
		
		balance = "";
		
		setPin(pin);
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
		return cardNumber + ";" + colorPreference + ";" + (isDefault ? "1;" : "0;") + pin + "*";
	}
	
	public void setBal(String bal){
		balance = bal;
	}
	
	public String getBal(){
		return balance;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
