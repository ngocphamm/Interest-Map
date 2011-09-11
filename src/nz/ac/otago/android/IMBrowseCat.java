package nz.ac.otago.android;

import java.util.ArrayList;

import nz.ac.otago.util.IMConstants;
import nz.ac.otago.util.IMDatabase;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Intent for Category browsing - Show all categories from data file
 * @author ngocminh
 */
public class IMBrowseCat extends ListActivity {
	
	/** CategoryAdapter to build category list */
	CategoryAdapter myCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imbrowselist);
		
		myCategory = new CategoryAdapter(this);
		if (myCategory.getCount() != 0) {
			/* If there is more than 1 category, show it/them */
			this.setListAdapter(myCategory);
		} else {	
			/* Show a Toast message that there's no category */
			Toast toast = Toast.makeText(this, IMConstants.TOAST_NO_CAT, 
													Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 40);
			toast.show();
		}
	}
	
	@Override
    protected void onListItemClick(ListView parent, View v, int position, long id) {
		String cat = myCategory.getItem(position);
		
		/* On list item click, change to Location Browsing with category name */ 
		Intent locList = new Intent(this, IMBrowseLoc.class);
		locList.putExtra("categoryName", cat);
		startActivity(locList);
	}
	
	/**
	 * Extend BaseAdapter to create a list of category
	 * @author ngocminh
	 */
	private class CategoryAdapter extends BaseAdapter { 
		private LayoutInflater mInflater;
		private ArrayList<String> categories;
		
		public CategoryAdapter(Context context) {
			mInflater = LayoutInflater.from(context); 
			categories = new ArrayList<String>(); 
			
			/* Get categories from the database */
			IMDatabase db = new IMDatabase(context);
			db.open();
			categories = db.getCategories();
			/*
			 * If there is more than one category, add an "abstract" 
			 * category that shows all items of interest called "All Locations"
			 */
			if (!categories.isEmpty()) {
				/* Add to top of the category list */
				categories.add(0, IMConstants.ALL_LOCATION);
			}
			db.close();
		}
		
		@Override
		public int getCount() {
			/* 
			 * Get the number of categories 
			 * NB: +1 by the added "abstract" category
			 */
			return categories.size();
		} 

		public String getItem(int i) {
			/* return category name at the given index i */
			return categories.get(i);
		} 
		
		public long getItemId(int i) {
			/* return category id the given index i, here is i, actually */
			return i;
		} 
		
		public View getView(int index, View convertView, ViewGroup parent) {
			/* Get item and add to a "holder" */
			
			final ViewHolder holder; 
			View view = convertView;
			
			if ((view == null) || (view.getTag() == null)) {
				/* 
				 * if there's no holder defined, get it from layout 
				 * "imbrowsecatrow", TextView "name"
				 */
				view = mInflater.inflate(R.layout.imbrowsecatrow, null); 
				holder = new ViewHolder(); 
				holder.mName = (TextView)view.findViewById(R.id.name); 
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			/* set text for the holder = Category name */
			holder.mName.setText(getItem(index)); 
			view.setTag(holder); 
			
			return view;
		}
		
		public class ViewHolder {
			TextView mName;
		}
	}
}
