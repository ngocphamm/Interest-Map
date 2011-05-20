package nz.ac.otago.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

	public IMDatabaseHelper(Context context, String name,
								CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("InterestMap", "Database: Creating all the tables");
		try {			
			db.execSQL(CREATE_LOCATION_TABLE);
		} catch (SQLiteException ex) {
			Log.v("InterestMap", "ERROR: " + ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("InterestMap", "Upgrading from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL(DROP_LOCATION_TABLE);
		onCreate(db);
	}
}
