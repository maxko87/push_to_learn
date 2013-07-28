/**
 * Copyright 2011 Mark Wyszomierski
 */
package com.greylock;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.foursquare.greylock.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class ActivityWebView extends Activity 
{
    private static final String TAG = "ActivityWebView";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Parse.initialize(this, getString(R.string.secret_parse_app_id), getString(R.string.secret_parse_client_key));
        
        final String CALLBACK_URL = getString(R.string.secret_foursquare_redirect_uri);
        final String CLIENT_ID = getString(R.string.secret_foursquare_client_id);
        
        String url =
            "https://foursquare.com/oauth2/authenticate" + 
                "?client_id=" + CLIENT_ID + 
                "&response_type=token" + 
                "&redirect_uri=" + CALLBACK_URL;
        
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "#access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    String accessToken = url.substring(start + fragment.length(), url.length());
                    Log.v(TAG, "OAuth complete, token: [" + accessToken + "].");
                    Toast.makeText(ActivityWebView.this, "Token: " + accessToken, Toast.LENGTH_SHORT).show();
                    
                    ParseObject foursquareUser = new ParseObject("foursquareUser");
                    foursquareUser.put("foursquareAccessToken", accessToken);
                    foursquareUser.saveInBackground();
                }
            }
        });
        webview.loadUrl(url);

    }
}