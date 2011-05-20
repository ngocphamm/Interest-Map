package nz.ac.otago.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import nz.ac.otago.util.IMLocation;

public class IMUtilities {
	/**
	 * Retrieved code from
	 * http://www.helloandroid.com/tutorials/how-download-fileimage-url-your-device
	 * @param inUrl url string to be passed to the function - implemented later
	 * @param fileName - filename to be saved to device
	 */
	public void DownloadFromUrl(String inUrl, FileOutputStream fos, 
															String fileName) {
		try {
			URL url = new URL(inUrl);

			long startTime = System.currentTimeMillis();
			Log.d("InterestMap", "Download begining");
			Log.d("InterestMap", "Download URL: " + url);
			
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/* Define InputStreams to read from the URLConnection. */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			fos.write(baf.toByteArray());
			fos.close();
			Log.d("InterestMap", "Download ready in "
							+ (System.currentTimeMillis() - startTime)
							+ " msec");
			Log.d("InterestMap", "Saved file name: " + fileName);
		} catch (IOException e) {
			Log.d("InterestMap", "ERROR: " + e);
		}
	}
	
	/**
	 * http://www.roseindia.net/java/beginners/java-read-file-line-by-line.shtml
	 * http://www.javapractices.com/topic/TopicAction.do?Id=87
	 * @author Andy Pham
	 *
	 */	
	public ArrayList<IMLocation> parseFile(FileInputStream fis) {
		ArrayList<IMLocation> locationList = new ArrayList<IMLocation>();
		
		try {
			DataInputStream in 	= new DataInputStream(fis);
	        BufferedReader br 	= new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    
		    /* Read File Line By Line */
		    int lat = 0;
		    int lon = 0;
		    String name 	= "";
		    String category = "";
		    String url 		= "";
		    while ((strLine = br.readLine()) != null)   {
		    	try {
		    		Scanner scanner = new Scanner(strLine);
			        scanner.useDelimiter(",");
			        if (scanner.hasNext()) {
			        	lat = scanner.nextInt();
			        	lon = scanner.nextInt();
						name 		= scanner.next();
						category	= scanner.next();
						url 		= scanner.next();
			        }
			        
			        locationList.add(new IMLocation(lat, lon, name, category, url));
		    	} catch (InputMismatchException e) {
		    		Log.d("InterestMap", "Error: " + e);
		    	}
		    }
		    
		    /* Close the input stream */
		    in.close();
		} catch (IOException e) {
			Log.d("InterestMap", "ERROR: " + e);
		}
		
		return locationList;
	}
	
	/**
	 * http://androidcommunity.com/forums/archive/index.php/t-8177.html
	 * @param url
	 * @return
	 */
	public String getOfflineHTML(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		String response = "";
		try {
			response = httpClient.execute(httpGet, responseHandler);
		} catch (ClientProtocolException e) {
			Log.d("InterestMap", "ERROR: " + e);
		} catch (IOException e) {
			Log.d("InterestMap", "ERROR: " + e);
		}
		
		return response;
	}
}
