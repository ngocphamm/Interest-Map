package nz.ac.otago.util;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class IMDatabase extends Activity {
	private SQLiteDatabase db;
	private final Context context;
	private final IMDatabaseHelper dbhelper;

	public IMDatabase(Context c) {
		context = c;
		dbhelper = new IMDatabaseHelper(context, IMConstants.DATABASE_NAME, null,
				IMConstants.DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("InterestMap", "ERROR: " + ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}
	
	public void prepare() {
		Log.v("InterestMap", "IMDatabase prepare: Deleting old records");
		String delete = "delete from " + IMConstants.LOCATION_TABLE
							+ " where 1=1";
		db.execSQL(delete);
	}

	public long insertLocation(IMLocation location) {
		try {
			ContentValues newValue = new ContentValues();
			
			/* If Location has more attributes, add them here */
			newValue.put(IMConstants.LATITUDE, location.getLatitude());
			newValue.put(IMConstants.LONGITUDE, location.getLongitude());
			newValue.put(IMConstants.LOCATION, location.getName());
			newValue.put(IMConstants.CATEGORY, location.getCategory());
			newValue.put(IMConstants.URL, location.getURL());
			newValue.put(IMConstants.CACHE, location.getCache());
			newValue.put(IMConstants.DATE,
					java.lang.System.currentTimeMillis());

			return db.insert(IMConstants.LOCATION_TABLE, null, newValue);
		} catch (SQLiteException ex) {
			Log.v("InterestMap", "ERROR: " + ex.getMessage());
			return -1;
		}
	}
	
	public void storeLocations(ArrayList<IMLocation> locations) {
		Iterator<IMLocation> itr = locations.iterator();

		IMLocation location = new IMLocation();
		this.open();
		this.prepare();
		while (itr.hasNext()) {
			location = itr.next();
			this.insertLocation(location);
		}
	}

	public ArrayList<IMLocation> getLocations(String catName) {
		ArrayList<IMLocation> locationList = new ArrayList<IMLocation>();
		
		String query = "select * from " + IMConstants.LOCATION_TABLE;
		if (catName.compareTo(IMConstants.ALL_LOCATION) != 0) {
			query += " where " + IMConstants.CATEGORY + " = '" + catName + "'"; 
		}
		query += " order by " + IMConstants.LOCATION;
				
		Cursor c = db.rawQuery(query, null);
		startManagingCursor(c); 
		if(c.moveToFirst()){
			do { 
				int lat = c.getInt(c.getColumnIndex(IMConstants.LATITUDE));
				int lon = c.getInt(c.getColumnIndex(IMConstants.LONGITUDE));
				String name = c.getString(c.getColumnIndex(IMConstants.LOCATION));
				String cat = c.getString(c.getColumnIndex(IMConstants.CATEGORY));
				String url = c.getString(c.getColumnIndex(IMConstants.URL));
				
				locationList.add(new IMLocation(lat, lon, name, cat, url));
			} while(c.moveToNext());
		} 
		c.close();
		
		return locationList;
	}
	
	public ArrayList<String> getCategories() {
		ArrayList<String> categoryList = new ArrayList<String>();
//		Cursor c = db.query(Constants.LOCATION_TABLE, null, null, null, null, 
//			null, null);
		
		Cursor c = db.rawQuery("select " + IMConstants.CATEGORY 
				+ " from " + IMConstants.LOCATION_TABLE 
				+ " group by " + IMConstants.CATEGORY
				+ " order by " + IMConstants.CATEGORY,  null);
		startManagingCursor(c);
		if (c.moveToFirst()) {
			do { 
				String cat = c.getString(c.getColumnIndex(IMConstants.CATEGORY));
				categoryList.add(cat);
			} while(c.moveToNext());
		}
		c.close();
		
		return categoryList;
	}
	
	public String getCachedData(String locationName) {
		String data = "";
		
		String query = "select " + IMConstants.CACHE
			+ " from " + IMConstants.LOCATION_TABLE
			+ " where " + IMConstants.LOCATION
				+ " = '" + locationName + "'";		
		
		Cursor c = db.rawQuery(query, null);
				
		startManagingCursor(c);
		if (c.moveToFirst()) {
			do {
				data = c.getString(c.getColumnIndex(IMConstants.CACHE));
			} while (c.moveToNext());
		}
		c.close();
				
		return data;
	}
}