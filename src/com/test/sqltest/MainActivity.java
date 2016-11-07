package com.test.sqltest;

import java.io.DataOutputStream;
import java.io.File;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

	Button rootBtn, sqlBtn, testBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SQLiteDatabase.loadLibs(this);

		rootBtn = (Button) findViewById(R.id.root_btn);
		sqlBtn = (Button) findViewById(R.id.sql_btn);
		testBtn = (Button) findViewById(R.id.test_btn);

		rootBtn.setOnClickListener(this);
		sqlBtn.setOnClickListener(this);
		testBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.root_btn) {
			Log.e("test", "pkg code path:" + getPackageCodePath());
			boolean result = upgradeRootPermission(getPackageCodePath());
			Toast.makeText(this, "请求root权限：" + result, Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.sql_btn) {
			testSql(this);
		} else if (v.getId() == R.id.test_btn) {
			boolean result = rootFile("/data/data/com.tencent.mm/MicroMsg/ee8cf35d4971eb78c3c94b36647f886c/EnMicroMsg.db");
			Log.e("test", "db root result:" + result);
		}

	}

	private static void testSql(Activity act) {
		Context wechatContext = getWechatContext(act);
		if (wechatContext == null) {
			Log.e("test", "wechat context is null !");
			return;
		}
		String password = "4a34bb5"; //
		File databaseFile = wechatContext.getDatabasePath("/data/data/com.tencent.mm/MicroMsg/ee8cf35d4971eb78c3c94b36647f886c/EnMicroMsg.db");
		if (!databaseFile.exists()) {
			Log.e("test", "db is not exist");
			return;
		}

		SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
			@Override
			public void preKey(SQLiteDatabase database) {

			}

			@Override
			public void postKey(SQLiteDatabase database) {
				// 兼容2.X版本
				database.rawExecSQL("PRAGMA cipher_migrate;"); // 最关键的一句！！！
			}
		};

		// SQLiteDatabase db = SQLiteDatabase.openDatabase(databaseFile,
		// password, null, SQLiteDatabase.OPEN_READWRITE);

		// test
		try {
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null, hook);
			// Cursor c = db.query(table, columns, selection, selectionArgs,
			// groupBy, having, orderBy);
			Cursor c = db.query("message", null, null, null, null, null, null);
			while (c.moveToNext()) {
				int _id = c.getInt(c.getColumnIndex("msgId"));
				String name = c.getString(c.getColumnIndex("content"));
				Log.i("db", "_id=>" + _id + ", content=>" + name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限) e
	 * 
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
		int exitVal = 1;
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				exitVal = process.exitValue(); // 1: 拒绝 0:同意
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		if (exitVal == 0) {
			return true;
		}
		return false;
	}

	// 修改文件读写权限
	public static boolean rootFile(String path) {
		try {
			Runtime.getRuntime().exec("chmod 777 " + path);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Context getWechatContext(Activity act) {
		try {
			return act.createPackageContext("com.tencent.mm", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
