package nz.ac.otago.android;

import nz.ac.otago.util.IMDatabase;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class IMWebView extends Activity {
	private WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.imwebview);

	    mWebView = (WebView) findViewById(R.id.imwebview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    
	    // If has Internet connection, go ONLINE; else use OFFLINE data
	    Intent me = this.getIntent();
	    if (me.hasExtra("offline")) {
	    	int offline = me.getIntExtra("offline", 0);
	    	if (offline == 0) { // Online browsing
	    		String url = me.getStringExtra("customURL");
	    		mWebView.loadUrl(url);
	    	} else { // Load offline data from cache
	    		String locationName = me.getStringExtra("locationName");
	    		IMDatabase db = new IMDatabase(IMWebView.this);
	    		db.open();
	    		String offlineData = db.getCachedData(locationName);
	    		db.close();
	    		mWebView.loadData(offlineData, "text/html", null);
	    	}
		}
	    
	    mWebView.setWebViewClient(new IMWebViewClient());
	}
	
	/*
	 * Override method
	 * Go Back within the WebView whenever possible then back to last Intent
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
	        mWebView.goBack();
	        return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * Ensure that clicking links on WebView will not open the 'actual' browser
	 * Links are loaded using the current WebView
	 */
	private class IMWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}
}
