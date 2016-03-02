package com.ayst.linearlauncher.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/3/2.
 */
public class DBManager {
    Context mContext = null;
    DBHelper mDbHelper = null;

    public DBManager(Context context) {
        this.mContext = context;
        mDbHelper = DBHelper.getInstance(context);
    }

    public boolean save(String pkgName, int launchCnt) {
        if (!TextUtils.isEmpty(pkgName)) {
            String sql = "select * from " + DBHelper.TABLE_NAME + " where " + DBHelper.COLUMN_PKG_NAME + "=?";
            Cursor cursor = mDbHelper.rawQuery(sql, new String[]{pkgName});

            ContentValues value = new ContentValues();
            value.put(DBHelper.COLUMN_PKG_NAME, pkgName);
            value.put(DBHelper.COLUMN_LAUNCH_CNT, launchCnt);
            if (cursor.moveToFirst()) {
                mDbHelper.update(value, DBHelper.COLUMN_PKG_NAME + "=?", new String[]{pkgName});
            } else {
                mDbHelper.insert(value);
            }
            mDbHelper.closeDb();
            return true;
        }
        return false;
    }

    public boolean increase(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            int launchCnt = 0;
            ContentValues value = new ContentValues();
            String sql = "select * from " + DBHelper.TABLE_NAME + " where " + DBHelper.COLUMN_PKG_NAME + "=?";
            Cursor cursor = mDbHelper.rawQuery(sql, new String[]{pkgName});

            if (cursor.moveToFirst()) {
                launchCnt = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_LAUNCH_CNT));
                value.put(DBHelper.COLUMN_PKG_NAME, pkgName);
                value.put(DBHelper.COLUMN_LAUNCH_CNT, launchCnt+1);
                mDbHelper.update(value, DBHelper.COLUMN_PKG_NAME + "=?", new String[]{pkgName});
            } else {
                value.put(DBHelper.COLUMN_PKG_NAME, pkgName);
                value.put(DBHelper.COLUMN_LAUNCH_CNT, launchCnt+1);
                mDbHelper.insert(value);
            }
            mDbHelper.closeDb();
            return true;
        }
        return false;
    }

    public boolean delete(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            mDbHelper.delete(pkgName);
            mDbHelper.closeDb();
            return true;
        }
        return false;
    }

    public int get(String pkgName) {
        String sql = "select * from " + DBHelper.TABLE_NAME + " where " + DBHelper.COLUMN_PKG_NAME + "=?";
        Cursor cursor = mDbHelper.rawQuery(sql, new String[]{pkgName.toString()});
        int launchCnt = 0;

        if (cursor.moveToFirst()) {
            launchCnt = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_LAUNCH_CNT));
        }
        mDbHelper.closeDb();
        return launchCnt;
    }
}
