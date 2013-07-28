package com.greylock;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fima.cardsui.views.CardUI;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.SaveCallback;

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
 
//	      final Context c = this.getApplicationContext();
//	      ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
//	  	    public void done(ParseException e) {
//	  	    		System.out.println("ERROR: " + e);
//	      		     PushService.subscribe(c, "user_"+deviceId, MainActivity.class);     
//	      		     System.out.println("SUBSCRIBED TO PUSH IN CALLBACK");
//	  		     }
//	  		});
//	      System.out.println("SUBSCRIBED TO PUSH");
        
        
//        // check if user already authenticated with foursquare
//        SharedPreferences prefs = this.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
//        boolean foursquare_authenticated = prefs.getBoolean(getString(R.string.foursquare_authenticated), false);
//        
//        final Button foursquare_reg_button = (Button)findViewById(R.id.foursquare_reg_button);
//        foursquare_reg_button.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
//                startActivity(intent);
//            }
//        });
//        final TextView foursquare_authenticated_text = (TextView) this.findViewById(R.id.foursquare_already_authenticated);
//        foursquare_authenticated_text.setVisibility(View.INVISIBLE);
//        
//        // backup query. shouldn't change ANYTHING if local storage works.
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("foursquareUser");
//        query.whereEqualTo("deviceId", deviceId);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> resultsList, ParseException e) {
//                if (resultsList != null && resultsList.size()>0) { //user exists
//                	foursquare_reg_button.setVisibility(View.INVISIBLE);
//                	foursquare_authenticated_text.setVisibility(View.VISIBLE);
//                	System.out.println("USER EXISTS!");
//                } 
//                else {
//                	System.out.println("USER DOESN'T EXIST!");
//                }
//            }
//        });
//        
//        System.out.println(" IS AUTHENTICATED? "  + foursquare_authenticated);
//        if (foursquare_authenticated){
//        	foursquare_reg_button.setVisibility(View.INVISIBLE);
//        	foursquare_authenticated_text.setVisibility(View.VISIBLE);
//        }
//        else {
//        	foursquare_authenticated_text.setVisibility(View.INVISIBLE);
////            PushService.setDefaultPushCallback(this, MainActivity.class);
////            final Context c = this.getApplicationContext();
////            ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
////        	    public void done(ParseException e) {
////	        		     PushService.subscribe(c, "user_"+deviceId, MainActivity.class);     
////	        		     System.out.println("SUBSCRIBED TO PUSH IN CALLBACK");
////        		     }
////        		});
////            System.out.println("SUBSCRIBED TO PUSH");
//        }
//        
//	    // init CardView
//		mCardView = (CardUI) findViewById(R.id.cardsview);
//		mCardView.setSwipeable(true);
//		renderNewCard("Get ready to learn some French!", "We will send you push notifications periodically throughout the day, based on your checkins and GPS coordinates.");
    }
	
	public void renderNewCard (String frenchWord, String englishWord) {
		mCardView.addCard(new MyCard(frenchWord, englishWord));
		mCardView.refresh();
	}
}