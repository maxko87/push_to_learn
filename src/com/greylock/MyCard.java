package com.greylock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

public class MyCard extends Card {
	private String content, misc_info;
	
	public MyCard(String title, String content, String misc_info){
		super(title);
		this.content=content;
		this.misc_info=misc_info;
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_ex, null);

		((TextView) view.findViewById(R.id.card_title)).setText(title);
		((TextView) view.findViewById(R.id.description)).setText(content);
		((TextView) view.findViewById(R.id.misc_info)).setText(misc_info);
		
		return view;
	}

	
	
	
}