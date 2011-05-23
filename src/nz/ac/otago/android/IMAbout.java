package nz.ac.otago.android;

import android.app.Activity;
import android.os.Bundle;

/**
 * Intent to show "About" information of the application
 * Use simple TextView with Dialog theme
 * @author ngocminh
 */
public class IMAbout extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imabout);
	}
}