package com.wearbucks.app;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        // 1. Create inflater 
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.single_card, parent, false);

        // 3. Get the layout elements to set
        TextView labelView = (TextView) rowView.findViewById(R.id.card_short_number);
        ImageView cardColor = (ImageView) rowView.findViewById(R.id.credit_card_color_single);

        // 4. Set the text for textView 
        labelView.setText(itemsArrayList.get(position).getShortNumber());
        
        // 5. Set the color drawable
        TypedArray colors = context.getResources().obtainTypedArray(R.array.card_colors_single);
        cardColor.setImageResource(colors.getResourceId(itemsArrayList.get(position).getColorPreference(), 0));

        // 6. Return rowView
        return rowView;
    }
}
