package com.sharedsqlite;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class SharedSqlite {

	// Private variables
	private static final String TAG = SharedSqlite.class.getSimpleName();

	// Class Variables
	private AtomicInteger mOpenCounter = new AtomicInteger();
    private static SQLiteOpenHelper mDatabaseHelper;
	private static SharedSqlite mInstance = null;
	private SQLiteDatabase mDatabase;
	
	public static abstract class DatabaseEntry implements BaseColumns {
		public static final String TABLE_COMMON_DATA = "shared_values_table";
		public static final String COLUMN_DATA_KEY = "key";
		public static final String COLUMN_DATA_VALUE = "value";
    }
	
	/**
	 * A static method to initialize a new instance of a SharedSqlite class
	 * @param context A context object to create a new SharedSqlite class
	 */
	public synchronized static void initialize(Context context) {
		if(mInstance == null) {
			mInstance = new SharedSqlite();
			mDatabaseHelper = new SharedSqliteDatabase(context);
		}
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
	 * Open the database for read and increment the counter of readers by one
	 * And return a writable SQLiteDatabase object
	 */
    private synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    /*
     * Close the database in case all the pointers to it are 'gone'
     * that is openCounter value is 0
     */
    private synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
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
			db = getInstance().openDatabase();
			row = new ContentValues();
			row.put(DatabaseEntry.COLUMN_DATA_KEY, dataKey);
			row.put(DatabaseEntry.COLUMN_DATA_VALUE, value);
			rowId = db.insertWithOnConflict(DatabaseEntry.TABLE_COMMON_DATA, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			if(rowId > -1) {
				pass = true;
			}
		} catch (SQLException exception) {
			Log.e(TAG, exception.getMessage());
		} finally {
			try {
				if (db != null) {
					// close database connection
					getInstance().closeDatabase();
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
	public <K, V> boolean addValue(K dataKey, V value) {
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
	public <K> String getStringValue(K dataKey, String defaultValue) {
		// method variables
		int columnIndex;
		String value = defaultValue;
		Cursor cursor = null;
		SQLiteDatabase db = null;

		// attempt to get the active cigarette id from the database
		try {
			db = getInstance().openDatabase();
			cursor = db.query(DatabaseEntry.TABLE_COMMON_DATA, null,
					DatabaseEntry.COLUMN_DATA_KEY + " = ?", new String[] { String.valueOf(dataKey) },
					null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					columnIndex = cursor.getColumnIndex(DatabaseEntry.COLUMN_DATA_VALUE);
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
					getInstance().closeDatabase();
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
	public <K> Integer getIntValue(K dataKey, Integer defaultValue) {
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
	public <K> Long getLongValue(K dataKey, Long defaultValue) {
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
	public <K> Double getDoubleValue(K dataKey, Double defaultValue) {
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
	public <K> Float getFloatValue(K dataKey, Float defaultValue) {
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
	public <K> Boolean getBooleanValue(K dataKey, Boolean defaultValue) {
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
