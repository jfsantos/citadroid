/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seaandsailor.citadroid;

import com.seaandsailor.citadroid.Quotes.Quote;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class QuoteProvider extends ContentProvider {

	private static final String TAG = "QuoteProvider";

	// The Android's default system path of your application database.
	private static String DATABASE_PATH = "/data/data/com.seaandsailor.citadroid/databases/";

	private static final String DATABASE_NAME = "quotes.db";
	private static final int DATABASE_VERSION = 2;
	private static final String QUOTES_TABLE_NAME = "quotes";

	private static HashMap<String, String> sQuotesProjectionMap;
	// private static HashMap<String, String> sLiveFolderProjectionMap;

	private static final int QUOTES = 1;
	private static final int QUOTE_ID = 2;
	private static final int LIVE_FOLDER_QUOTES = 3;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private SQLiteDatabase myDataBase;

		private final Context myContext;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			myContext = context;
		}

		/**
		 * Creates a empty database on the system and rewrites it with your own
		 * database.
		 * */
		public void createDataBase() throws IOException {

			boolean dbExist = checkDataBase();

			if (dbExist) {
				// do nothing - database already exist
			} else {

				// By calling this method an empty database will be created
				// into the default system path
				// of your application so we are gonna be able to overwrite that
				// database with our database.
				this.getReadableDatabase();

				try {

					copyDataBase();

				} catch (IOException e) {

					throw new Error("Error copying database");

				}
			}

		}

		/**
		 * Check if the database already exist to avoid re-copying the file each
		 * time you open the application.
		 * 
		 * @return true if it exists, false if it doesn't
		 */
		private boolean checkDataBase() {

			SQLiteDatabase checkDB = null;

			try {
				String myPath = DATABASE_PATH + DATABASE_NAME;
				checkDB = SQLiteDatabase.openDatabase(myPath, null,
						SQLiteDatabase.OPEN_READONLY);

			} catch (SQLiteException e) {

				// database does't exist yet.

			}

			if (checkDB != null) {

				checkDB.close();

			}

			return checkDB != null ? true : false;
		}

		/**
		 * Copies your database from your local assets-folder to the just
		 * created empty database in the system folder, from where it can be
		 * accessed and handled. This is done by transfering bytestream.
		 * */
		private void copyDataBase() throws IOException {

			// Open your local db as the input stream
			InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

			// Path to the just created empty db
			String outFileName = DATABASE_PATH + DATABASE_NAME;

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

		}

		public void openDataBase() throws SQLException {

			// Open the database
			String myPath = DATABASE_PATH + DATABASE_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		}

		@Override
		public synchronized void close() {

			if (myDataBase != null)
				myDataBase.close();

			super.close();

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		try {

			mOpenHelper.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}

		try {

			mOpenHelper.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;

		}

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(QUOTES_TABLE_NAME);

		switch (sUriMatcher.match(uri)) {
		case QUOTES:
			qb.setProjectionMap(sQuotesProjectionMap);
			break;

		case QUOTE_ID:
			qb.setProjectionMap(sQuotesProjectionMap);
			qb.appendWhere(Quote._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Quotes.Quote.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case QUOTES:
		case LIVE_FOLDER_QUOTES:
			return Quote.CONTENT_TYPE;

		case QUOTE_ID:
			return Quote.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != QUOTES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		// Make sure that the fields are all set

		if (values.containsKey(Quotes.Quote.AUTHOR) == false) {
			Resources r = Resources.getSystem();
			values.put(Quotes.Quote.AUTHOR,
					r.getString(android.R.string.unknownName));
		}

		if (values.containsKey(Quotes.Quote.QUOTE) == false) {
			values.put(Quotes.Quote.QUOTE, "");
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(QUOTES_TABLE_NAME, Quote.QUOTE, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(Quotes.Quote.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case QUOTES:
			count = db.delete(QUOTES_TABLE_NAME, where, whereArgs);
			break;

		case QUOTE_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(QUOTES_TABLE_NAME,
					Quote._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case QUOTES:
			count = db.update(QUOTES_TABLE_NAME, values, where, whereArgs);
			break;

		case QUOTE_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(QUOTES_TABLE_NAME, values,
					Quote._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/**
	 * Returns a random Quote
	 * @return
	 */
	public String[] getQuote() {
		return new String[2];
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Quotes.AUTHORITY, "quotes", QUOTES);
		sUriMatcher.addURI(Quotes.AUTHORITY, "quotes/#", QUOTE_ID);
		// sUriMatcher.addURI(Quote.AUTHORITY, "live_folders/quotes",
		// LIVE_FOLDER_QUOTES);

		sQuotesProjectionMap = new HashMap<String, String>();
		sQuotesProjectionMap.put(Quote._ID, Quote._ID);
		sQuotesProjectionMap.put(Quote.AUTHOR, Quote.AUTHOR);
		sQuotesProjectionMap.put(Quote.QUOTE, Quote.QUOTE);

		// Support for Live Folders.
		// sLiveFolderProjectionMap = new HashMap<String, String>();
		// sLiveFolderProjectionMap.put(LiveFolders._ID, Quotes._ID + " AS " +
		// LiveFolders._ID);
		// sLiveFolderProjectionMap.put(LiveFolders.NAME, Quotes.AUTHOR + " AS "
		// +
		// LiveFolders.NAME);
		// Add more columns here for more robust Live Folders.
	}
}
