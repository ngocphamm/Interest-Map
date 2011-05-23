package nz.ac.otago.android;

import java.util.ArrayList;

import nz.ac.otago.util.IMConstants;
import nz.ac.otago.util.IMDatabase;
import nz.ac.otago.util.IMLocation;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Intent for Location browsing - Show all Locations from data file within a
 * particular Category or All categories
 * @author ngocminh
 */
public class IMBrowseLoc extends ListActivity {
	
	/** LocationAdapter to build location list */
	LocationAdapter myLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imbrowselist);
		
		Intent me = this.getIntent();
		if (me.hasExtra("categoryName")) {
			String categoryName = me.getStringExtra("categoryName");
			myLocation = new LocationAdapter(this, categoryName);
			
			/* Set title bar for the intent with Category name */
			if (categoryName.compareTo(IMConstants.ALL_LOCATION) == 0) {
				this.setTitle("All items of interest");
			} else {
				this.setTitle("Items in \"" + categoryName + "\" category");
			}
			
		}
		
		this.setListAdapter(myLocation);
	}
	
	@Override
    protected void onListItemClick(ListView parent, View v, int position, long id) {
		IMLocation place = myLocation.getItem(position);
		Log.d("InterestMap" ,"Location Clicked: " + place.getName());
		
		/* 
		 * On list item click - determine Internet access availability
		 * ONLINE - navigate to map view focused on clicked location 
		 * OFFLINE - Show WebView with cached Data
		 */
		if (this.isOnline() == true) {
			Intent mapview = new Intent(this, IMMapView.class);
			/* singleView extra to tell MapView to zoom closer to the place */
			mapview.putExtra("singleView", 1);
			
			/* latitude and longitude extras tell MapView to navigate to the place */
			mapview.putExtra("lat", place.getLatitude());
			mapview.putExtra("lon", place.getLongitude());
			startActivity(mapview);
		} else {
			Intent webview = new Intent(this, IMWebView.class);
			/* Tell WebView to use cached contents */
			webview.putExtra("offline", 1);
			webview.putExtra("locationName", place.getName());
			startActivity(webview);
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
	 * Extend BaseAdapter to create a list of location
	 * @author ngocminh
	 */
	private class LocationAdapter extends BaseAdapter { 
		private LayoutInflater mInflater;
		private ArrayList<IMLocation> locations;
		
		public LocationAdapter(Context context, String categoryName) {
			mInflater = LayoutInflater.from(context); 
			locations = new ArrayList<IMLocation>(); 
			
			/* Get items of interest from the database */
			IMDatabase db = new IMDatabase(context);
			db.open();
			locations = db.getLocations(categoryName);
			db.close();
		}
		
		@Override
		public int getCount() {
			/* get numbers of items */
			return locations.size();
		}
		
		public IMLocation getItem(int i) {
			/* return object of IMLocation type with given index i */
			return locations.get(i);
		}
		
		public long getItemId(int i) {
			return i;
		}
		
		public View getView(int index, View convertView, ViewGroup parent) {
			/* Get item and add to a "holder" */
			
			final ViewHolder holder; 
			View view = convertView;
			
			if ((view == null) || (view.getTag() == null)) {
				/* 
				 * if there's no holder defined, get it from layout 
				 * "imbrowselocrow", TextView "name" and "category"
				 */
				view = mInflater.inflate(R.layout.imbrowselocrow, null); 
				holder = new ViewHolder(); 
				holder.mName = (TextView)view.findViewById(R.id.name); 
				holder.mCategory = (TextView)view.findViewById(R.id.category); 
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			/* Set text for the holder with Location name and Category name */
			IMLocation location = getItem(index); 
			holder.mName.setText(location.getName()); 
			holder.mCategory.setText(location.getCategory());
			view.setTag(holder); 
			
			return view;
		}
		
		public class ViewHolder {
			TextView mName;
			TextView mCategory;
		}
	}
}
