package nz.ac.otago.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DatabaseHelper class to extend Android SQLiteOpenHelper class
 * @author ngocminh
 */
public class IMDatabaseHelper extends SQLiteOpenHelper {
	private static final String CREATE_LOCATION_TABLE = "create table "
		+ IMConstants.LOCATION_TABLE + " (" 
			+ IMConstants.ID + " integer primary key autoincrement, "
			+ IMConstants.LATITUDE + " integer, "
			+ IMConstants.LONGITUDE + " integer, "
			+ IMConstants.LOCATION + " text not null, " 
			+ IMConstants.CATEGORY + " text not null, "
			+ IMConstants.URL + " text not null, "
			+ IMConstants.CACHE + " text, "
			+ IMConstants.DATE + " long);";
	
	private static final String DROP_LOCATION_TABLE = "drop table if exists "
		+ IMConstants.LOCATION_TABLE + ";";
	
	/**
	 * Init a new instance of DatabaseHelper
	 * @param context	Context
	 * @param name		Database name
	 * @param factory	Usually "null"
	 * @param version	Database version
	 */
	public IMDatabaseHelper(Context context, String name,
								CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	/**
	 * Run on first creation of database file to create all tables needed
	 */
	public void onCreate(SQLiteDatabase db) {
		Log.d("InterestMap", "Database: Creating all the tables");
		try {			
			db.execSQL(CREATE_LOCATION_TABLE);
		} catch (SQLiteException ex) {
			Log.e("OnCreatingTable", "ERROR: " + ex.getMessage());
		}
	}

	@Override
	/**
	 * Run on upgrading the application, currently not used!
	 * Just drop the table(s) then call onCreate() again
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("InterestMap", "Upgrading from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL(DROP_LOCATION_TABLE);
		onCreate(db);
	}
}
