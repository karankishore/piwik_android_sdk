package com.anupcowkur.piwiksdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PiwikDataManager extends SQLiteOpenHelper{

    private static PiwikDataManager piwikDataManager = null;

    private static final String DATABASE_NAME = "piwik.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "event";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, info TEXT";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static PiwikDataManager getInstance(Context context){
        if(piwikDataManager == null){
            piwikDataManager = new PiwikDataManager(context);
        }
        return piwikDataManager;
    }


        public PiwikDataManager(Context context){
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
