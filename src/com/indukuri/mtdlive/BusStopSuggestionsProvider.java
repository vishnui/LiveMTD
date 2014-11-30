package com.indukuri.mtdlive;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.algolia.search.saas.APIClient;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

public class BusStopSuggestionsProvider extends ContentProvider {
	
	private Index index ;
	private String[] columnNames = { BaseColumns._ID, 
			SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } ;
	@Override
	public boolean onCreate() {
		index = new APIClient("RFWSMAK04M", "4f79feb796e14445ef46ad5b56a80604").initIndex("dev_Stops") ;
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = new MatrixCursor(columnNames, 10) ;
		try {
			String query = uri.getLastPathSegment().toLowerCase(Locale.US);
			Query q = new Query() ;
			q.setHitsPerPage(10);
			q.setPage(0);
			q.setQueryString(query);
			
			JSONObject response = index.search(q);
			JSONArray hits = response.getJSONArray("hits") ;
			
			for(int i=0; i < Math.min(10, hits.length()); i++){
				JSONObject hit = hits.getJSONObject(i) ;
				String name = hit.getString("stop_name") ;
				String id = hit.getString("stop_id") ;
				String code = hit.getString("code") ;
				
				String[] data = { i+"", name, code, id} ;
				cursor.addRow(data) ;
			}
		} catch (Exception e) {
			// Anything go wrong, throw error
			throw new IllegalArgumentException(e.getMessage()) ;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return "json";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Do nothing
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Do nothing
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// Do nothing
		return 0;
	}

}
