package com.anupcowkur.piwiksdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Calendar;

public class StoreDataTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String type, userId;

    public StoreDataTask(Context context, String type) {
        this.context = context;
        this.type = type;
        this.userId = getUserIdFromPreferences();
    }

    private String getUserIdFromPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SharedPreferenceKeys.PREF_USER_ID, null);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PiwikDatabaseHelper piwikDatabaseHelper = PiwikDatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = piwikDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PiwikDatabaseHelper.EVT_TABLE_COL_USER_ID, userId);
        contentValues.put(PiwikDatabaseHelper.EVT_TABLE_COL_INFO, type);
        contentValues.put(PiwikDatabaseHelper.EVT_TABLE_COL_TIMESTAMP, getCurrentTimestamp());
        sqLiteDatabase.insert(PiwikDatabaseHelper.EVT_TABLE, null, contentValues);
        return null;
    }

    private String getCurrentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("h=");
        stringBuilder.append(calendar.get(Calendar.HOUR_OF_DAY));
        stringBuilder.append("&m=");
        stringBuilder.append(calendar.get(Calendar.MINUTE));
        stringBuilder.append("&s=");
        stringBuilder.append(calendar.get(Calendar.SECOND));
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        PiwikDatabaseHelper piwikDatabaseHelper = PiwikDatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = piwikDatabaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PiwikDatabaseHelper.EVT_TABLE, null);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Log.d(StoreDataTask.class.getName(), " rec: " + cursor.getString(cursor.getColumnIndex(PiwikDatabaseHelper.EVT_TABLE_COL_INFO)));
                cursor.moveToNext();
            }
        }
        super.onPostExecute(aVoid);
    }
}
