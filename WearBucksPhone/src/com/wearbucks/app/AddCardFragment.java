package com.wearbucks.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

public class AddCardFragment extends Fragment {

	public View view;
	public final static int LENGTH_SEGMENT = 4;
	public final static int NUMBER_OF_SEGMENTS = 4;

	public String cardNumber;
	public int selectedColor = 0;

	// Layout elements
	public EditText cardNumber1;
	public EditText cardNumber2;
	public EditText cardNumber3;
	public EditText cardNumber4;
	public RadioGroup group;
	public View radioButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setup_addcard, container, false);

		// Get all card elements
		cardNumber1 = ((EditText) view.findViewById(R.id.card_input_1));
		cardNumber2 = ((EditText) view.findViewById(R.id.card_input_2));
		cardNumber3 = ((EditText) view.findViewById(R.id.card_input_3));
		cardNumber4 = ((EditText) view.findViewById(R.id.card_input_4));
		group = (RadioGroup) view.findViewById(R.id.card_color_group);

		EditText[] listOfSegments = { cardNumber1, cardNumber2, cardNumber3, cardNumber4 };

		// Set listeners for each segment of card number
		for (int i = 0; i < listOfSegments.length; i++) {
			setListenerSegment(listOfSegments, i);
		}

		return view;
	}

	/**
	 * Moves focus to next card number segment as user types
	 */
	public static void setListenerSegment(final EditText[] listOfSegments, final int position) {
		listOfSegments[position].addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {

				// This segment is full; move on to next
				if (listOfSegments[position].getText().toString().length() == LENGTH_SEGMENT
						&& position != NUMBER_OF_SEGMENTS - 1) {
					listOfSegments[position + 1].requestFocus();
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	/**
	 * Valids card number
	 */
	public boolean isValid() {
		// Concatenate card number from segments
		String cardSegment1 = cardNumber1.getText().toString();
		String cardSegment2 = cardNumber2.getText().toString();
		String cardSegment3 = cardNumber3.getText().toString();
		String cardSegment4 = cardNumber4.getText().toString();
		String cardNumber = "" + cardSegment1 + cardSegment2 + cardSegment3 + cardSegment4;

		// TODO: make a better validation check
		if (cardNumber.length() < 16) {
			return false;
		}

		this.cardNumber = cardNumber;

		// Set preferred color
		int checkedButton = group.getCheckedRadioButtonId();
		radioButton = group.findViewById(checkedButton);
		int idx = group.indexOfChild(radioButton);
		selectedColor = idx;

		return true;
	}
}
