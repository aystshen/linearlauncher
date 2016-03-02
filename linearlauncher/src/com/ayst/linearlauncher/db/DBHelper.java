package com.ayst.linearlauncher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/3/2.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "linearlauncher.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "package";
    public static final String COLUMN_PKG_NAME = "pkg_name";
    public static final String COLUMN_LAUNCH_CNT = "launch_cnt";

    private SQLiteDatabase db;

    private static DBHelper mdbHelper;

    public static DBHelper getInstance(Context context) {
        if (mdbHelper == null) {
            mdbHelper = new DBHelper(context);
        }
        return mdbHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + "(id integer primary key autoincrement," +
                COLUMN_PKG_NAME + " varchar(256)," + COLUMN_LAUNCH_CNT + " interger)";
        db.execSQL(sql);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion == newVersion) {
            return;
        }
        onCreate(db);
    }

    public long insert(ContentValues values) {
        if (db == null)
            db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, values);
    }

    public int delete(String name) {
        if (db == null)
            db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_PKG_NAME + "=?",
                new String[]{String.valueOf(name)});
    }

    public int update(ContentValues values, String WhereClause, String[] whereArgs) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.update(TABLE_NAME, values, WhereClause, whereArgs);
    }

    public Cursor query(String[] columns, String whereStr, String[] whereArgs) {
        if (db == null) {
            db = getReadableDatabase();
        }
        return db.query(TABLE_NAME, columns, whereStr, whereArgs, null, null,
                null);
    }

    public Cursor rawQuery(String sql, String[] args) {
        if (db == null) {
            db = getReadableDatabase();
        }
        return db.rawQuery(sql, args);
    }

    public void ExecSQL(String sql) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.execSQL(sql);
    }

    public void closeDb() {
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
