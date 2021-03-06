package com.androidbegin.menuviewpagertutorial;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class Fragment3 extends SherlockFragment {
	
	ListView listview;
	List<ParseObject> ob;
	ProgressDialog mProgressDialog;
	SkincareAdapter adapter;
	//EditText editsearch;
	TextView t;
	EditText inputsearch;
	private static List<WorldPopulation> skincarelist = null;
	public View rootView;
	
	 private final int SPLASH_DISPLAY_LENGHT = 500;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.listview_main, container, false);
        setHasOptionsMenu(true);
        new RemoteDataTask().execute();
		return rootView;
	}
	public class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressdialog
			mProgressDialog = ProgressDialog.show(getActivity(), "", "Placing order...", true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setTitle("Heritage Fresh");
			// Set progressdialog message
			mProgressDialog.setMessage("Getting Data...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		
		} 
		
		
		@Override
		protected Void doInBackground(Void... params) {
		// Create the array
			skincarelist = new ArrayList<WorldPopulation>();
			try {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
						"Product");
				//query.orderByAscending("Product");
				ob = query.find();
				for (ParseObject Product : ob) {
					// Locate images in Picture column
					ParseFile image = (ParseFile) Product.get("Picture");
					
					WorldPopulation map = new WorldPopulation();
					if ( Product.get("Category").equals("skin care")) {
					map.setPrice((String) Product.get("Price"));
					map.setProduct((String) Product.get("Product"));
					map.setCategory((String) Product.get("Category"));
						map.setQuantity(Integer.parseInt(Product.get("qty").toString()));
					map.setPicture(image.getUrl());
					skincarelist.add(map);
					}
				}
			} catch (ParseException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			} 
			return null;
		} 

		@Override
		protected void onPostExecute(Void result) {
			// Locate the listview in listview_main.xml
			 
			listview = (ListView) rootView.findViewById(R.id.listview);
			// Pass the results into SkincareAdapter.java

			t = (TextView) rootView.findViewById(R.id.Text);
				adapter = new SkincareAdapter(getActivity(),skincarelist);
				listview.setAdapter(adapter);

			mProgressDialog.dismiss();
		}
	
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.frag1act1, menu);
	    com.actionbarsherlock.view.MenuItem menucart = menu.findItem(R.id.menu_cart);
	    menucart.setVisible(true);
	   /* 
	    menucart.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Intent inten = new Intent (getActivity() , ShoppingCartActivity.class);
				startActivity(inten);
				return false;
			}
		}); */
	    
	    SearchView searchView = new SearchView(getActivity());

	    searchView.setQueryHint("Search Product");
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String searchtext) {
				String text = searchtext.toString().toLowerCase(Locale.getDefault());
				if (text.length() <= 0) {
					t.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
					adapter.filter(text);
				} else {
					adapter.filter(text);
					if (adapter == null || adapter.isEmpty()) {
						t.setVisibility(View.VISIBLE);
						listview.setVisibility(View.INVISIBLE);
					} else {
						t.setVisibility(View.INVISIBLE);
						listview.setVisibility(View.VISIBLE);
					}
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String searchtext) {
				String text = searchtext.toString().toLowerCase(Locale.getDefault());
				if (text.length() <= 0) {
					t.setVisibility(View.INVISIBLE);
					listview.setVisibility(View.VISIBLE);
					adapter.filter(text);
				} else {
					adapter.filter(text);
					if (adapter == null || adapter.isEmpty()) {
						t.setVisibility(View.VISIBLE);
						listview.setVisibility(View.INVISIBLE);
					} else {
						t.setVisibility(View.INVISIBLE);
						listview.setVisibility(View.VISIBLE);
					}
				}
				return false;
			}

	    });

	    menu.add("Search Products")
	        .setIcon(R.drawable.ic_action_search)
	        .setActionView(searchView)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

	}
}
