package com.bbz.latte;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ViewHolder", "Recycle" })
public class CardAdapter extends ArrayAdapter<Card> {

	private final Context context;
	private final ArrayList<Card> itemsArrayList;

	public CardAdapter(Context context, ArrayList<Card> itemsArrayList) {
		super(context, R.layout.single_card, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get layout elements
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.single_card, parent, false);
		TextView labelView = (TextView) rowView.findViewById(R.id.card_short_number);
		ImageView cardColor = (ImageView) rowView.findViewById(R.id.credit_card_color_single);
		Button defaultButton = (Button) rowView.findViewById(R.id.default_button_single_card);
		Button deleteButton = (Button) rowView.findViewById(R.id.delete_button_single_card);
		TextView balView = (TextView) rowView.findViewById(R.id.card_bal);
		TypedArray colors = context.getResources().obtainTypedArray(R.array.card_colors_single);

		// Show last four digits
		labelView.setText(itemsArrayList.get(position).getShortNumber());
		balView.setText(itemsArrayList.get(position).getBal());

		// Show if default
		if (itemsArrayList.get(position).isDefault()) {
			defaultButton.setBackground(context.getResources().getDrawable(R.drawable.star));
		} else {
			defaultButton.setBackground(context.getResources().getDrawable(R.drawable.star_grey));
		}

		// Set delete listener
		deleteButton.setOnClickListener(new DeleteOnClickListener(itemsArrayList.get(position)));

		// Set credit card color
		cardColor.setImageResource(colors.getResourceId(itemsArrayList.get(position)
				.getColorPreference(), 0));

		return rowView;
	}

	/**
	 * Deletes card from the list
	 */
	public class DeleteOnClickListener implements OnClickListener {

		Card card;

		public DeleteOnClickListener(Card card) {
			this.card = card;
		}

		@Override
		public void onClick(View v) {
			// Creating the instance of PopupMenu
			PopupMenu popup = new PopupMenu(getContext(), v);
			// Inflating the Popup using xml file
			popup.getMenuInflater().inflate(R.menu.single_card_options, popup.getMenu());

			// registering popup with OnMenuItemClickListener
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					int id = item.getItemId();
					if (id == R.id.edit_card) {
						CardManager.editCard(card);
						return true;
					} else if (id == R.id.delete_card) {
						CardManager.deleteCard(card.getCardNumber());
						return true;
					} else {
						return true;
					}
				}
			});

			popup.show();// showing popup menu
		}

	};
}
