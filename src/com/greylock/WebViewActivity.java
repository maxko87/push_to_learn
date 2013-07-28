/**
 * Copyright 2011 Mark Wyszomierski
 */
package com.greylock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.parse.ParseObject;

public class WebViewActivity extends Activity 
{
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        final String deviceId = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        final String CALLBACK_URL = getString(R.string.secret_foursquare_redirect_uri);
        final String CLIENT_ID = getString(R.string.secret_foursquare_client_id);
        
        String url =
            "https://foursquare.com/oauth2/authenticate" + 
                "?client_id=" + CLIENT_ID + 
                "&response_type=token" + 
                "&redirect_uri=" + CALLBACK_URL;
                
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        final WebViewActivity webViewContext = this;
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "#access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    String accessToken = url.substring(start + fragment.length(), url.length());
                    Toast.makeText(WebViewActivity.this, "Token: " + accessToken, Toast.LENGTH_SHORT).show();
                    // store token and device ID
                    ParseObject foursquareUser = new ParseObject("foursquareUser");
                    foursquareUser.put("deviceId", deviceId);
                    foursquareUser.put("foursquareAccessToken", accessToken);
                    foursquareUser.saveInBackground();
                    // save that the user has authenticated successfully
                    SharedPreferences prefs = webViewContext.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
                    Editor editor = prefs.edit();
                    editor.putBoolean(webViewContext.getString(R.string.foursquare_authenticated), true);
                    editor.commit();
                    System.out.println(" IS AUTHENTICATED!!");
                }
            }
        });
        webview.loadUrl(url);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}