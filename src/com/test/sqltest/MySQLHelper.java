package com.test.sqltest;

import android.content.Context;
import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

public class MySQLHelper extends SQLiteOpenHelper {

	
	
	private MySQLHelper(Context context, String name, CursorFactory factory, int version, SQLiteDatabaseHook hook, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, hook, errorHandler);
	}

	private MySQLHelper(Context context, String name, CursorFactory factory, int version, SQLiteDatabaseHook hook) {
		super(context, name, factory, version, hook);
	}

	public MySQLHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}


}
