package nz.ac.otago.util;

/**
 * Class for representing a location of interest with some attributes
 * - latitude
 * - longitude
 * - location name
 * - category
 * - url (for detail information)
 * - cache (detail information cached)
 * @author ngocminh
 */
public class IMLocation {
	
	/** Latitude for the location */
	private int latitude;
	
	/** Longitude for the location */
	private int longitude;
	
	/** Name of the location */
	private String name;
	
	/** Category for the location */
	private String category;
	
	/** URL with detail information for the location */
	private String url;
	
	/** Cached contents of the above URL for the location */
	private String cache;
	
	public IMLocation() {
		// Do nothing here???
	}
	
	public IMLocation(int latitude, int longitude, String name,
						String category, String url) {
		this.latitude 	= latitude;
		this.longitude 	= longitude;
		this.name 		= name;
		this.category 	= category;
		this.url 		= url;
		this.cache 		= "";
	}
	
	public void setCache(String cache) {
		this.cache = cache;
	}
	
	public int getLatitude() {
		return this.latitude;
	}
	
	public int getLongitude() {
		return this.longitude;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getCache() {
		return this.cache;
	}
}
