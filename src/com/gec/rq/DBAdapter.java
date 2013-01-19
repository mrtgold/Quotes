package com.gec.rq;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.*;

public class DBAdapter
{

	int id=0;
	public static final String KEY_ROWID="_id";
	public static final String KEY_QUOTE="Quote";
	private static final String TAG="DBAdaptor";

	private static final String DATABASE_NAME="Random";
	private static final String DATABASE_TABLE="tblRandomQuotes";
	private static final int DATABASE_VERSION=2;

	private static final String DATABASE_CREATE = 
	"create table " + DATABASE_TABLE + 
	" (" + 
	KEY_ROWID + " integer primary key autoincrement, " +
	KEY_QUOTE + " text not null " +
	" );";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Log.e(TAG,DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion +
				  " to " + newVersion + "; All data will be destroyed");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}

	}

	public DBAdapter open() throws SQLiteException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		DBHelper.close();
	}

	public long insertQuote(String quote)
	{
		ContentValues initialValues=new ContentValues();
		initialValues.put(KEY_QUOTE, quote);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	//TODO: rename this -> getQuoteCount
	public int getAllEntries()
	{
		Cursor cursor = db.rawQuery(
			"SELECT count(" + KEY_QUOTE + ") from " + DATABASE_TABLE, null);
		//TODO: this could be cleaned up	
		
		int numEntries  =0;
		if (cursor.moveToFirst())
		{
			numEntries  = cursor.getInt(0);
			
		}
		numEntries = cursor.getInt(0);
		Log.e(TAG,"numEntries = " + numEntries);
		return numEntries;
	}


	//TODO: refactor this per comments
	public String getRandomEntry()
	{
		//id = getAllEntries();

		Random random = new Random();
		int rand=0;
		try
		{
			rand  = random.nextInt(getAllEntries());
		}
		catch (Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		if (rand == 0)
			++rand;

		Cursor cursor = db.rawQuery(
			"SELECT " + KEY_QUOTE + " from " + DATABASE_TABLE + 
			" WHERE " + KEY_ROWID + " = " + rand, null);
		//TODO: this could be cleaned up	
		if (cursor.moveToFirst())
		{
			return cursor.getString(0);
		}
		return cursor.getString(0);
	}
}
				
