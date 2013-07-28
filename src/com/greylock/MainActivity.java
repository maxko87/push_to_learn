package com.greylock;

import java.util.Date;
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
import android.widget.TextView;

import com.cardsui.example.MyCard;
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
        
//        SharedPreferences prefs = this.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
//        Editor editor = prefs.edit();
//        editor.putBoolean(this.getString(R.string.foursquare_authenticated), false);
//        editor.commit();
        
        Parse.initialize(this, getString(R.string.secret_parse_app_id), getString(R.string.secret_parse_client_key));
        
        PushService.setDefaultPushCallback(this, WebViewActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        
        final String deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        PushService.subscribe(this, "user_"+deviceId, WebViewActivity.class);
        
        
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
        final TextView foursquare_authenticated_text = (TextView) this.findViewById(R.id.foursquare_already_authenticated);
        foursquare_authenticated_text.setVisibility(View.INVISIBLE);
        
        // backup query. shouldn't change ANYTHING if local storage works.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("foursquareUser");
        query.whereEqualTo("deviceId", deviceId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultsList, ParseException e) {
                if (resultsList != null && resultsList.size()>0) { //user exists
                	foursquare_reg_button.setVisibility(View.INVISIBLE);
                	foursquare_authenticated_text.setVisibility(View.VISIBLE);
                	System.out.println("USER EXISTS!");
                } 
                else {
                	System.out.println("USER DOESN'T EXIST!");
                }
            }
        });
        
        System.out.println(" IS AUTHENTICATED? "  + foursquare_authenticated);
        if (foursquare_authenticated){
        	foursquare_reg_button.setVisibility(View.INVISIBLE);
        	foursquare_authenticated_text.setVisibility(View.VISIBLE);
        }
        else {
        	foursquare_authenticated_text.setVisibility(View.INVISIBLE);
        }
        
        
        
        
	    // init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		renderNewCard("Get ready to learn some French!", "We will send you push notifications periodically throughout the day, based on your checkins and GPS coordinates.");
    
		
		
		PushService.subscribe(this, "user_"+deviceId, RenderActivity.class);
	}
	
	class RenderActivity extends Activity {
		@Override
		protected
		void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			final String deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("cardInfo");
			query.whereEqualTo("device_id", deviceId);
			Date now = new Date();
			Date before = new Date(now.getTime()-(1000*60));
			query.whereGreaterThan("createdAt", before);
			
			query.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> results, ParseException e) {
			        if (e == null && results.size()>0) {
			        	for (int i=0; i<results.size(); i++){
			        		ParseObject res = results.get(i);
			        		String cardTitle = res.getString("english");
			        		String cardContent = res.getString("translation");
			        		String cardPlace = res.getString("place");
			        		String cardService = res.getString("service");
			        		
			        		if (i==0){
			        			mCardView.addCard(new MyCard("Get the CardsUI view",""));
			        		}
			        		else{
			        			mCardView.addCardToLastStack(new MyCard(cardTitle, cardContent));
			        		}
			        		
			        	}
			        } else {
			        	System.out.println("no data");
			        }
			    }
			});
			
			//mCardView.addCard(new MyCard(A, B));
			mCardView.refresh();
		}
	}
	
	
	
	
	
	
	
	public void renderNewCard (String frenchWord, String englishWord) {
		mCardView.addCard(new MyCard(frenchWord, englishWord));
		mCardView.refresh();
	}
}