package nz.ac.otago.android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.ac.otago.util.IMConstants;
import nz.ac.otago.util.IMDatabase;
import nz.ac.otago.util.IMLocation;

public class IMMapView extends MapActivity {
	private Drawable drawable;
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private MapView mapView;
	private IMItemizedOverlay itemizedOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.immapview);
		
		// Get MapView and turn some configurations on
		mapView = (MapView) findViewById(R.id.immapview);
		mapView.setBuiltInZoomControls(true);
		
		// Get the Map Controller and define some settings
		mapController = mapView.getController();
		
		// Get the marker on an overlay
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.mapmarkersmall);
		itemizedOverlay = new IMItemizedOverlay(drawable);
		
		ArrayList<IMLocation> locations = new ArrayList<IMLocation>();
		
		/* Get from database */
		IMDatabase db = new IMDatabase(this);
		db.open();
		locations = db.getLocations(IMConstants.ALL_LOCATION);
		db.close();
		
		Iterator<IMLocation> itr = locations.iterator();
		
		IMLocation location = new IMLocation();
		while(itr.hasNext()) {
			location = itr.next();
			GeoPoint point = new GeoPoint(location.getLatitude(), 
											location.getLongitude());
			OverlayItem overlayitem = new OverlayItem(point, 
														location.getName(), 
														location.getURL());
			
			itemizedOverlay.addOverlay(overlayitem);
		}
		
		/* Single point view or whole view */
		Intent me = this.getIntent();
		if (me.hasExtra("singleView")) {
			GeoPoint point = new GeoPoint(me.getIntExtra("lat", 0),
											me.getIntExtra("lon", 0));
			
			mapController.animateTo(point);
			mapController.setZoom(10);
		} else {
			mapController.setZoom(2); // Zoom 1 is world view
		}
		
		mapOverlays.add(itemizedOverlay);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	/*
	 * Class to draw the marker on an Overlay
	 */
	public class IMItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		public IMItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}
		
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
		  return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}
		
		/*
		 * Override Method
		 * Start new WebView intent to show the web defined on the file for 
		 * each location
		 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
		 */
		@Override
		protected boolean onTap(int index) {
			Intent i = new Intent(IMMapView.this, IMWebView.class);
			i.putExtra("offline", 0);
			i.putExtra("customURL", mOverlays.get(index).getSnippet());
			startActivity(i);
			
			return super.onTap(index);
		}
	}
}