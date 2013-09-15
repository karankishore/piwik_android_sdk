package com.anupcowkur.piwiksdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PiwikDatabaseHelper extends SQLiteOpenHelper {

    public static final String EVT_TABLE = "event";
    public static final String EV_TABLE_COL_ID = "_id";
    public static final String EVT_TABLE_COL_USER_ID = "user_id";
    public static final String EVT_TABLE_COL_INFO = "info";
    public static final String EVT_TABLE_COL_TIMESTAMP = "timestamp";
    private static final String DATABASE_NAME = "piwik.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + EVT_TABLE + "(" + EV_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + EVT_TABLE_COL_USER_ID + " TEXT, " + EVT_TABLE_COL_INFO + " TEXT, " + EVT_TABLE_COL_TIMESTAMP + " TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + EVT_TABLE;
    private static PiwikDatabaseHelper piwikDatabaseHelper = null;

    public PiwikDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static PiwikDatabaseHelper getInstance(Context context) {
        if (piwikDatabaseHelper == null) {
            piwikDatabaseHelper = new PiwikDatabaseHelper(context);
        }
        return piwikDatabaseHelper;
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
