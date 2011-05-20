package nz.ac.otago.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import nz.ac.otago.util.IMConstants;
import nz.ac.otago.util.IMUtilities;
import nz.ac.otago.util.IMDatabase;
import nz.ac.otago.util.IMLocation;

/**
 * Main Intent at application startup, show some buttons for further actions
 * @author ngocminh
 */
public class InterestMap extends Activity implements OnClickListener {
	private static final String FIXED_URL = "http://www.andypham.info/interestlist.txt";
	private ArrayList<IMLocation> locations;
	private ProgressDialog pDialog;
	private IMDatabase db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*
		 * Check if there is Internet connection or not
		 * If YES: Ask user to Download data from Internet or Load from cache
		 * Data will be downloaded in background with a progress dialog
		 * If NO: Show user that there is no Internet connection
		 */
		if (this.isOnline() == true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(IMConstants.MES_LOAD_DATA)
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						
						db = new IMDatabase(InterestMap.this);
						
						new DownloadDataFile().execute(FIXED_URL);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						
						Toast toast = Toast.makeText(InterestMap.this, 
								IMConstants.TOAST_DATA_CACHE, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
						toast.show();
					}
				});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			Toast toast = Toast.makeText(InterestMap.this, 
					IMConstants.TOAST_NO_INTERNET, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
			toast.show();
		}

		// Set up click listeners for all the buttons
		View browseButton = findViewById(R.id.browse_button);
		browseButton.setOnClickListener(this);

		View demoButton = findViewById(R.id.demo_button);
		demoButton.setOnClickListener(this);

		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);

		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

	/**
	 * Start new Intent according to which button clicked
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_button:
			Intent about = new Intent(this, IMAbout.class);
			startActivity(about);
			break;
		case R.id.browse_button:
			Intent browse = new Intent(this, IMBrowseCat.class);
			startActivity(browse);
			break;
		case R.id.demo_button:
			Intent demo = new Intent(this, IMMapView.class);
			startActivity(demo);
			break;
		case R.id.exit_button:
			finish();
			break;
		}
	}
	
	/**
	 * Check that Internet connection is available or not
	 * @return True if there is, False otherwise
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(
												Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
	            && cm.getActiveNetworkInfo().isAvailable()
	            && cm.getActiveNetworkInfo().isConnected()) {
	        return true;
	    } else {
	    	Log.d("InterestMap", "WARNING: Internet connection not available!");
	    	return false;
	    }
	}
	
	/**
	 * Backgrounding Task
	 * To download data file and update the progress dialog
	 * @author ngocminh
	 */
	protected class DownloadDataFile extends AsyncTask<String, Void, Integer> {
		protected void onPreExecute() {
			pDialog = ProgressDialog.show(
					InterestMap.this, "", 
					"Loading data file ...", true);
		}
		
		protected Integer doInBackground(String... urls) {
			IMUtilities fp = new IMUtilities();
			/* Try to get data file then parse into an ArrayList of Location */						
			try {
				FileOutputStream fos = openFileOutput(
						IMConstants.FILE_NAME,
						Context.MODE_WORLD_WRITEABLE);

				fp.DownloadFromUrl(urls[0], fos, IMConstants.FILE_NAME);
	
				FileInputStream fis = openFileInput(IMConstants.FILE_NAME);
				locations = fp.parseFile(fis);
			} catch (FileNotFoundException e) {
				Log.d("InterestMap", "ERROR: " + e);
				return 0;
			}
			
			return 1;
		}
		
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				new LoadInfoCache().execute();
			} else {
				pDialog.dismiss();
				
				Toast toast = Toast.makeText(InterestMap.this, 
						IMConstants.TOAST_ERROR_LOAD, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
				toast.show();
			}
		}
	}
	
	/**
	 * Backgrounding Task
	 * To download all places' descriptions from associated URLs for caching
	 * @author ngocminh
	 */
	protected class LoadInfoCache extends AsyncTask<Void, Void, Integer> {
		protected void onPreExecute() {
			pDialog.setMessage("Loading location info from URLs");
		}
		
		protected Integer doInBackground(Void... params) {
			IMUtilities fp = new IMUtilities();
			
			/* Store locations to database */
			Iterator<IMLocation> itr = locations.iterator();
			IMLocation location = new IMLocation();
			while (itr.hasNext()) {
				location = itr.next();
				location.setCache(fp.getOfflineHTML(location.getURL()));
			}
			
			return 1;
		}

		protected void onPostExecute(Integer result) {			
			if (result == 1) {
				new StoreLocations().execute();
			} else {
				pDialog.dismiss();
				
				Toast toast = Toast.makeText(InterestMap.this, 
						IMConstants.TOAST_ERROR_URL, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
				toast.show();
			}
		}
	}
	
	/**
	 * Backgrounding Task
	 * To store all locations with cached descriptions to SQLite database
	 * @author ngocminh
	 */
	protected class StoreLocations extends AsyncTask<Void, Void, Integer> {
		protected Integer doInBackground(Void... params) {
			db.storeLocations(locations);
			db.close();
			
			return 1;
		}
		
		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			
			if (result == 1) {	
				Toast toast = Toast.makeText(InterestMap.this, 
						IMConstants.TOAST_DATA_STORED, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
				toast.show();
			} else {
				Toast toast = Toast.makeText(InterestMap.this, 
						IMConstants.TOAST_ERROR_STORE, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
				toast.show();
			}
		}
	}
}