package com.greylock;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fima.cardsui.views.CardUI;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

public class MainActivity extends Activity 
{
    private CardUI mCardView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        Parse.initialize(this, getString(R.string.secret_parse_app_id), getString(R.string.secret_parse_client_key));
        
        // check if user already authenticated with foursquare
        SharedPreferences prefs = this.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
        boolean foursquare_authenticated = prefs.getBoolean(getString(R.string.foursquare_authenticated), false);
        
        final Button foursquare_reg_button = (Button)findViewById(R.id.foursquare_reg_button);
        foursquare_reg_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
        
        ParseQuery<ParseObject> query = ParseQuery.getQuery("foursquareUser");
        query.whereEqualTo("deviceId", deviceId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) { //user exists
                	foursquare_reg_button.setVisibility(View.INVISIBLE);
                } 
                else { //user doesn't exist
                	foursquare_reg_button.setVisibility(View.VISIBLE);
                }
            }
        });
        
        System.out.println(" IS AUTHENTICATED? "  + foursquare_authenticated);
        if (foursquare_authenticated){
        	foursquare_reg_button.setVisibility(View.INVISIBLE);
        }
        else {
            PushService.setDefaultPushCallback(this, MainActivity.class);
            ParseInstallation.getCurrentInstallation().saveInBackground();
            PushService.subscribe(this.getApplicationContext(), "user_"+deviceId, MainActivity.class);
        }
        
	    // init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		mCardView.addCard(new MyCard("Get ready to learn some French!"));
		mCardView.refresh();
	
    }
}