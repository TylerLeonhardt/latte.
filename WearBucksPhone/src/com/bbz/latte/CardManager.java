package com.bbz.latte;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class CardManager {

	/*
	 * CARD MANAGER
	 * 
	 * List of methods to interact with the cards and adapter
	 * 
	 * List of methods:
	 * 
	 * initializeCards() saveCards() saveNewCard(String cardNumber, int
	 * colorIndex) addNewCard(String cardNumber, int idx) deleteCard(final
	 * String cardNumber) setDefault(Card card)
	 */

	// Creates the arraylist of cards from the string stored in System Prefs
	public static void initializeCards() {
		String current = MainActivity.pref.getString(MainActivity.LISTOFCARDS, "*");
		MainActivity.activeCards = new ArrayList<Card>();

		// Empty; no cards saved yet (usually not possible)
		if (current.length() == 1) {
		} else {
			String[] cards = current.split("\\*");

			for (int i = 1; i < cards.length; i++) {

				// Get data from it
				String[] cardData = cards[i].split(";");

				for (String s : cardData)
					System.out.print(" --- " + s);

				boolean def = cardData[2].equals("1") ? true : false;

				MainActivity.activeCards.add(new Card(cardData[0], Integer.parseInt(cardData[1]),
						def, cardData[3]));
			}
		}
	}

	// Loops through the array list of cards and saves the data to a string to
	// be stored in System Prefs
	public static void saveCards() {
		String allCards = "*";
		for (Card c : MainActivity.activeCards) {
			allCards = allCards + c.toString();
		}

		MainActivity.editor.putString(MainActivity.LISTOFCARDS, allCards);
		MainActivity.editor.commit();
	}

	// saves the new card to the system prefs (aka concats the string and saves
	// it to system prefs)
	public static void saveNewCard(String cardNumber, int colorIndex, String pin) {
		String currentCards = MainActivity.pref.getString(MainActivity.LISTOFCARDS, "*");
		
		currentCards = currentCards + cardNumber + ";" + colorIndex + ";0;" + (pin.equals("") ? "nopin" : pin) + "*";

		MainActivity.editor.putString(MainActivity.LISTOFCARDS, currentCards);
		MainActivity.editor.commit();

		System.err.println(MainActivity.pref.getString(MainActivity.LISTOFCARDS, null));
	}

	// Creates a new card and adds it to the arraylist of cards and updates the
	// list view
	public static void addNewCard(String cardNumber, int idx, String pin) {
		if (MainActivity.activeCards.size() == 0) {
			MainActivity.activeCards.add(new Card(cardNumber, idx, true, pin));
		} else {
			MainActivity.activeCards.add(new Card(cardNumber, idx, pin));
		}
		MainActivity.adapter.notifyDataSetChanged();
	}

	// removes a card from the cards array list
	public static void deleteCard(final String cardNumber) {

		if (MainActivity.activeCards.size() == 1) {
			MainActivity.showDeleteError("Hang on!", "You need at least one card");
		} else {
			new AlertDialog.Builder(MainActivity.context).setTitle("Removing card")
					.setMessage("Are you sure you want to remove this card from latte.?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							for (Card c : MainActivity.activeCards) {
								if (c.getShortNumber().equals(cardNumber)) {
									MainActivity.activeCards.remove(c);

									// make the next card a default
									// automatically
									if (c.isDefault() && !MainActivity.activeCards.isEmpty()) {
										setDefault(MainActivity.activeCards.get(0));
									}

									break;

								}
							}

							CardManager.saveCards();
							MainActivity.adapter.notifyDataSetChanged();
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					}).setIcon(android.R.drawable.ic_dialog_alert).show();
		}
	}

	// sets the card passed in as the default card
	public static void setDefault(Card card) {
		// set all to false (there is some optimization here)
		for (Card d : MainActivity.activeCards)
			d.setDefault(false);

		// set the new default
		card.setDefault(true);

		// save the data
		MainActivity.editor.putString(MainActivity.DEFAULTCARD, card.getCardNumber());
		CardManager.saveCards();

		MainActivity.adapter.notifyDataSetChanged();

		Toast.makeText(MainActivity.context, "Default card set!", Toast.LENGTH_SHORT).show();

		// create the new notification
		new BarcodeAsyncTask(MainActivity.pref.getString(MainActivity.DEFAULTCARD, null),
				MainActivity.context, MainActivity.systemService).execute();
	}
}
