package nz.ac.otago.util;

public class IMLocation {
	private int latitude;
	private int longitude;
	private String name;
	private String category;
	private String url;
	private String cache;
	
	public IMLocation() {
		// Do nothing here???
	}
	
	public IMLocation(int latitude, int longitude, String name,
						String category, String url) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.category = category;
		this.url = url;
		this.cache = "";
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
	
	public String getURL() {
		return this.url;
	}
	
	public String getCache() {
		return this.cache;
	}
}
