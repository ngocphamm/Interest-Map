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

/**
 * Class for SQLite database actions
 * @author ngocminh
 */
public class IMDatabase extends Activity {
	
	/** SQLite database object */
	private SQLiteDatabase db;
	private final Context context;
	
	/** IMDatabaseHelper object, to create database and table structure */
	private final IMDatabaseHelper dbhelper;

	public IMDatabase(Context c) {
		context = c;
		dbhelper = new IMDatabaseHelper(context, IMConstants.DATABASE_NAME, null,
				IMConstants.DATABASE_VERSION);
	}
	
	public void close() {
		/*
		 * Close database connection
		 */
		db.close();
	}
	
	/**
	 * Open new database connection, trying to get "write" permission
	 * @throws SQLiteException
	 */
	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("InterestMap", "ERROR: " + ex.getMessage());
			db = dbhelper.getReadableDatabase();
		}
	}
	
	/**
	 * Each time reloading data file, also delete all existing records from db
	 */
	public void prepare() {
		Log.v("InterestMap", "IMDatabase prepare: Deleting old records");
		String delete = "delete from " + IMConstants.LOCATION_TABLE
							+ " where 1=1";
		db.execSQL(delete);
	}

	/**
	 * Insert new location to database
	 * @param location	Location object to be inserted
	 * @return
	 */
	public long insertLocation(IMLocation location) {
		try {
			ContentValues newValue = new ContentValues();
			
			/* If Location object has more attributes, add them here */
			newValue.put(IMConstants.LATITUDE, location.getLatitude());
			newValue.put(IMConstants.LONGITUDE, location.getLongitude());
			newValue.put(IMConstants.LOCATION, location.getName());
			newValue.put(IMConstants.CATEGORY, location.getCategory());
			newValue.put(IMConstants.URL, location.getUrl());
			newValue.put(IMConstants.CACHE, location.getCache());
			newValue.put(IMConstants.DATE,
					java.lang.System.currentTimeMillis());

			return db.insert(IMConstants.LOCATION_TABLE, null, newValue);
		} catch (SQLiteException ex) {
			Log.v("InterestMap", "ERROR: " + ex.getMessage());
			return -1;
		}
	}
	
	/**
	 * Iterate through all locations parsed from data file then save to database
	 * @param locations
	 */
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
	
	/**
	 * Get locations from database, could be within a particular category
	 * @param catName 	Category name
	 * @return			An ArrayList of all resulted locations
	 */
	public ArrayList<IMLocation> getLocations(String catName) {
		ArrayList<IMLocation> locationList = new ArrayList<IMLocation>();
		
		/* build the raw query */
		String query = "select * from " + IMConstants.LOCATION_TABLE;
		if (catName.compareTo(IMConstants.ALL_LOCATION) != 0) {
			query += " where " + IMConstants.CATEGORY + " = '" + catName + "'"; 
		}
		
		/* Order by location name */
		query += " order by " + IMConstants.LOCATION;
		
		/* Create a cursor to run through the result set of SQLite query */
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
	
	/**
	 * Get all categories on database
	 * @return	An ArrayList of all categories
	 */
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
	
	/**
	 * Get cached data of location's description from database
	 * @param locationName	Location Name
	 * @return				String of description, web contents actually (HTML)
	 */
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