package com.anupcowkur.piwiksdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PiwikDataManager extends SQLiteOpenHelper {

    private static PiwikDataManager piwikDataManager = null;

    private static final String DATABASE_NAME = "piwik.db";
    private static final int DATABASE_VERSION = 1;
    public static final String EVT_TABLE = "event";
    public static final String EVT_TABLE_COL_USER_ID = "user_id";
    public static final String EVT_TABLE_COL_INFO = "info";
    public static final String EVT_TABLE_COL_EXTRA_INFO = "extra_info";
    public static final String EVT_TABLE_COL_TIMESTAMP = "timestamp";
    private static final String CREATE_TABLE = "CREATE TABLE " + EVT_TABLE + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + EVT_TABLE_COL_INFO + " TEXT, " + EVT_TABLE_COL_EXTRA_INFO + " TEXT, " + EVT_TABLE_COL_TIMESTAMP + " TEXT";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + EVT_TABLE;

    public static PiwikDataManager getInstance(Context context) {
        if (piwikDataManager == null) {
            piwikDataManager = new PiwikDataManager(context);
        }
        return piwikDataManager;
    }


    public PiwikDataManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
