package com.sharedsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sharedsqlite.SharedSqlite.DatabaseEntry;

public class SharedSqliteDatabase extends SQLiteOpenHelper {
	
	// Database name version and table name
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_COMMON_DATA = "shared_values_database";
	

	// Tables and table columns names
	private String CREATE_COMMON_DATA_TABLE;

	/*
	 * A package private class constructor
	 */
	protected SharedSqliteDatabase(Context context) {
		super(context, DATABASE_COMMON_DATA, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		CREATE_COMMON_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ DatabaseEntry.TABLE_COMMON_DATA + " (" 
				+ DatabaseEntry.COLUMN_DATA_KEY + " TEXT PRIMARY KEY NOT NULL, "
				+ DatabaseEntry.COLUMN_DATA_VALUE + " TEXT NOT NULL);";

		// create the tables
		db.execSQL(CREATE_COMMON_DATA_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_COMMON_DATA);
		onCreate(db);
	}
}
