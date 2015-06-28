package com.sharedsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SharedSqlite extends SQLiteOpenHelper {

	// Class variables
	private final String TAG = SharedSqlite.class.getSimpleName();
	
	// Database name version and table name
	private static final String DATABASE_COMMON_DATA = "shared_values_database";
	private static final String TABLE_COMMON_DATA = "shared_values_table";
	private static final int DATABASE_VERSION = 1;

	// Tables and table columns names
	private String CREATE_COMMON_DATA_TABLE;
	private static final String COLUMN_DATA_KEY = "key";
	private static final String COLUMN_DATA_VALUE = "value";
	private static SharedSqlite mInstance = null;
	
	/**
	 * A static method to initialize a new instance of a SharedSqlite class
	 * @param context A context object to create a new SharedSqlite class
	 */
	public static void initialize(Context context) {
		mInstance = new SharedSqlite(context);
	}
	
	/**
	 * A method used to get a new instance of the SharedSqlite class
	 * @return A reference for the an instance of this class 
	 * @throws NullPointerException In case the class has never been initialized
	 * Use SharedSqlite.initialize(context) to create a new instance prior to this method
	 */
	public static SharedSqlite getInstance() throws NullPointerException {
		if(mInstance == null) {
			throw new NullPointerException("The class has never been initialized. " +
					"Use initialize(context) first to create a new instance");
		}
		return mInstance;
	}

	/*
	 * A private class constructor
	 */
	private SharedSqlite(Context context) {
		super(context, DATABASE_COMMON_DATA, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		CREATE_COMMON_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_COMMON_DATA + " (" 
				+ COLUMN_DATA_KEY + " TEXT PRIMARY KEY NOT NULL, "
				+ COLUMN_DATA_VALUE + " TEXT NOT NULL);";

		// create the tables
		db.execSQL(CREATE_COMMON_DATA_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMON_DATA);
		onCreate(db);
	}

	/*
	 * A private method used to add a row to the table in case it does not exists
	 * Or update a row value in case it exist in the database
	 */
	private boolean addOrUpdate(String dataKey, String value) {

		// method variables
		long rowId;
		boolean pass = false;
		SQLiteDatabase db = null;
		ContentValues row = null;

		try {
			db = getWritableDatabase();
			row = new ContentValues();
			row.put(COLUMN_DATA_KEY, dataKey);
			row.put(COLUMN_DATA_VALUE, value);
			rowId = db.insertWithOnConflict(TABLE_COMMON_DATA, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			if(rowId > -1) {
				pass = true;
			}
		} catch (SQLException exception) {
			Log.e(TAG, exception.getMessage());
		} finally {
			try {
				if (db != null) {
					// close database connection
					db.close();
				} 
			} catch (SQLException exception) {
				Log.e(TAG, exception.getMessage());
			}
		}
		return pass;
	}
	
	/**
	 * A method to add a new row to the database. 
	 * The data will be added as a key-value and the key will be 
	 * used to fetch the data later on 
	 * @param dataKey The key to be used later to fetch the data
	 * @param value The data to be stored
	 * @return True in case the row added successfully False otherwise
	 */
	public <T, S> boolean addValue(T dataKey, S value) {
		boolean pass = addOrUpdate(String.valueOf(dataKey), String.valueOf(value));
		return pass;
	}
	
	/**
	 * Get a new String value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A String value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> String getStringValue(T dataKey, String defaultValue) {
		// method variables
		int columnIndex;
		String value = defaultValue;
		Cursor cursor = null;
		SQLiteDatabase db = null;

		// attempt to get the active cigarette id from the database
		try {
			db = getWritableDatabase();
			cursor = db.query(TABLE_COMMON_DATA, null,
					COLUMN_DATA_KEY + " = ?", new String[] { String.valueOf(dataKey) },
					null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					columnIndex = cursor.getColumnIndex(COLUMN_DATA_VALUE);
					if (columnIndex > -1) {
						value = cursor.getString(columnIndex);
					}
				}
			}
		} catch (SQLException exception) {
			Log.e(TAG, exception.getMessage());
		} finally {
			try {
				if(cursor != null) {
					if(!cursor.isClosed()) {
						cursor.close();
					}
				}
				if (db != null) {
					db.close();
				}
			} catch (Exception exception) {
				Log.e(TAG, exception.getMessage());
			}
		}
		return value;
	}

	/**
	 * Get a new Integer value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A Integer value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> Integer getIntValue(T dataKey, Integer defaultValue) {
		Integer value = defaultValue;
		String stringValue = getStringValue(dataKey, null);
		if (stringValue != null) {
			try {
				value = Integer.valueOf(stringValue);
			} catch (Exception e) {	}
		}
		return value;
	}
	
	/**
	 * Get a new Long value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A Long value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> Long getLongValue(T dataKey, Long defaultValue) {
		Long value = defaultValue;
		String stringValue = getStringValue(dataKey, null);
		if (stringValue != null) {
			try {
				value = Long.valueOf(stringValue);
			} catch (Exception e) {	}
		}
		return value;
	}
	
	/**
	 * Get a new Double value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A Double value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> Double getDoubleValue(T dataKey, Double defaultValue) {
		Double value = defaultValue;
		String stringValue = getStringValue(dataKey, null);
		if (stringValue != null) {
			try {
				value = Double.valueOf(stringValue);
			} catch (Exception e) {	}
		}
		return value;
	}
	
	/**
	 * Get a new Float value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A Float value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> Float getFloatValue(T dataKey, Float defaultValue) {
		Float value = defaultValue;
		String stringValue = getStringValue(dataKey, null);
		if (stringValue != null) {
			try {
				value = Float.valueOf(stringValue);
			} catch (Exception e) {	}
		}
		return value;
	}
	
	/**
	 * Get a new Boolean value by the given key
	 * @param dataKey The key to look a new value by
	 *
	 * @param defaultValue A default return value, if there is no mapping 
	 * For the given key this value will be returned
	 * 
	 * @return A Boolean value associated with the given key or the defaultValue in 
	 * Case there is no such mapping
	 */
	public <T> Boolean getBooleanValue(T dataKey, Boolean defaultValue) {
		Boolean value = defaultValue;
		String stringValue = getStringValue(dataKey, null);
		if (stringValue != null) {
			try {
				value = Boolean.valueOf(stringValue);
			} catch(Exception e) { }
		}
		return value;
	}
}
