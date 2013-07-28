package com.greylock;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
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
                System.out.println("url: " + url);
                if (start > -1) {
                    final String accessToken = url.substring(start + fragment.length(), url.length());
                    Toast.makeText(WebViewActivity.this, "Token: " + accessToken, Toast.LENGTH_SHORT).show();
                    Thread trd = new Thread(new Runnable(){
                    	  @Override
                    	  public void run(){
                    		  getUserJson(accessToken, deviceId);
                    	  }
                    });
                    trd.start();
                    // save that the user has authenticated successfully
//                    SharedPreferences prefs = webViewContext.getSharedPreferences("com.greylock", Context.MODE_PRIVATE);
//                    Editor editor = prefs.edit();
//                    editor.putBoolean(webViewContext.getString(R.string.foursquare_authenticated), true);
//                    editor.commit();
//                    System.out.println(" IS AUTHENTICATED!!");
                }
            }
        });
        webview.loadUrl(url);
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);

    }
    
    // another api call to get the userId of the foursquare user
    public void getUserJson(String accessToken, String deviceId){
    	String URL = "https://api.foursquare.com/v2/users/self?oauth_token=" + accessToken;
        HttpClient httpclient = new DefaultHttpClient();  
        HttpGet request = new HttpGet(URL);  
        request.addHeader("deviceId", deviceId);  
        ResponseHandler<String> handler = new BasicResponseHandler();  
        String result = "";
		try {  
            result = httpclient.execute(request, handler);  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		System.out.println("JSON RESULT: " + result);
		// parse out the userId
		Pattern p = Pattern.compile("[0-9]+\"");
		Matcher m = p.matcher(result);
		int i=0;
		String userId = "fuck it";
		while (m.find() && i<1){			
			userId = m.group();
			userId = userId.substring(0, userId.length()-1);
			i++;
		}
		System.out.println("FUCK YOU: " + userId);
		
        // store token and device ID
        ParseObject foursquareUser = new ParseObject("foursquareUser");
        foursquareUser.put("foursquareUserId", userId);
        foursquareUser.put("deviceId", deviceId);
        foursquareUser.put("foursquareAccessToken", accessToken);
        foursquareUser.saveInBackground();
        httpclient.getConnectionManager().shutdown();  
    } 
    
}