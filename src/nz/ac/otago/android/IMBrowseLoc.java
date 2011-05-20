package nz.ac.otago.android;

import java.util.ArrayList;

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

public class IMBrowseLoc extends ListActivity {
	LocationAdapter myLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imbrowselist);
		
		Intent me = this.getIntent();
		if (me.hasExtra("categoryName")) {
			myLocation = new LocationAdapter(this, me.getStringExtra("categoryName"));
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
			mapview.putExtra("singleView", 1);
			mapview.putExtra("lat", place.getLatitude());
			mapview.putExtra("lon", place.getLongitude());
			startActivity(mapview);
		} else {
			Intent webview = new Intent(this, IMWebView.class);
			webview.putExtra("offline", 1);
			webview.putExtra("locationName", place.getName());
			startActivity(webview);
		}
	}
	
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
	
	private class LocationAdapter extends BaseAdapter { 
		private LayoutInflater mInflater;
		private ArrayList<IMLocation> locations;
		
		public LocationAdapter(Context context, String categoryName) {
			mInflater = LayoutInflater.from(context); 
			locations = new ArrayList<IMLocation>(); 
			
			IMDatabase db = new IMDatabase(context);
			db.open();
			locations = db.getLocations(categoryName);
			db.close();
		}
		
		@Override
		public int getCount() {
			return locations.size();
		}
		
		public IMLocation getItem(int i) {
			return locations.get(i);
		}
		
		public long getItemId(int i) {
			return i;
		}
		
		public View getView(int index, View convertView, ViewGroup parent) {
			final ViewHolder holder; 
			View view = convertView;
			
			if ((view == null) || (view.getTag() == null)) {
				view = mInflater.inflate(R.layout.imbrowselocrow, null); 
				holder = new ViewHolder(); 
				holder.mName = (TextView)view.findViewById(R.id.name); 
				holder.mCategory = (TextView)view.findViewById(R.id.category); 
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.mLocation = getItem(index); 
			holder.mName.setText(holder.mLocation.getName()); 
			holder.mCategory.setText(holder.mLocation.getCategory());
			view.setTag(holder); return view;
		}
		
		public class ViewHolder {
			IMLocation mLocation;
			TextView mName;
			TextView mCategory;
		}
	}
}
