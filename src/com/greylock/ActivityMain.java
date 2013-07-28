package com.greylock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fima.cardsui.views.CardUI;
import com.foursquare.greylock.R;

public class ActivityMain extends Activity 
{
    private CardUI mCardView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
                startActivity(intent);
            }
        });
        
     // init CardView
	mCardView = (CardUI) findViewById(R.id.cardsview);
	mCardView.setSwipeable(true);
	mCardView.addCard(new MyCard("Get the CardsUI view"));
	mCardView.refresh();
	
	
    }
}