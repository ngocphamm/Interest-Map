package nz.ac.otago.util;

/**
 * Class for CONSTANTS such as database-related name and messages
 * @author ngocminh
 */
public class IMConstants { 
	/** data file name to be saved on device */
	public static final String FILE_NAME 		= "locationlist.txt";
	public static final String ALL_LOCATION		= "All Locations";
	
	/* Using the SQLite Database */
	public static final String DATABASE_NAME	= "datastorage";
	public static final int DATABASE_VERSION 	= 1; 
	public static final String LOCATION_TABLE 	= "imlocations";
	public static final String ID 				= "_id";
	public static final String LATITUDE			= "latitude";
	public static final String LONGITUDE		= "longtitude";
	public static final String LOCATION 		= "name"; 
	public static final String CATEGORY 		= "category";
	public static final String URL 				= "url";
	public static final String CACHE 			= "cache";
	public static final String DATE 			= "recorddate";
	
	/* Texts for Toast messages */
	public static final String TOAST_NO_CAT			= "There is no category to show";
	public static final String TOAST_NO_INTERNET	= "No Internet Connectivity! Data will be loaded from cache!";
	public static final String TOAST_DATA_STORED	= "Data stored sucessfully";
	public static final String TOAST_DATA_CACHE 	= "Data will be loaded from cache";
	public static final String TOAST_ERROR_LOAD		= "Cannot download data file!";
	public static final String TOAST_ERROR_URL		= "Cannot get location info from URLs";
	public static final String TOAST_ERROR_STORE	= "Cannot store data!";
	
	/* Texts for Dialogs */
	public static final String MES_LOAD_DATA	= "Do you want to load data file from the Internet?";
	public static final String MES_DOWN_DATA	= "Downloading data file ...";
	public static final String MES_DOWN_INFO	= "Retrieving location info from URLs ...";
	public static final String MES_STORE_INFO	= "Storing information to database ...";
}
