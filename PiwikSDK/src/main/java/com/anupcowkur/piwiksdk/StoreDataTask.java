package com.anupcowkur.piwiksdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Map;

public class StoreDataTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String info, userId;
    private Map<String, String> extraInfo;

    public StoreDataTask(Context context, String info, Map<String, String> extraInfo) {
        this.context = context;
        this.info = info;
        this.extraInfo = extraInfo;
        this.userId = getUserIdFromPreferences();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PiwikDataManager piwikDataManager = PiwikDataManager.getInstance(context);
        SQLiteDatabase sqLiteDatabase = piwikDataManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PiwikDataManager.EVT_TABLE_COL_USER_ID, userId);
        contentValues.put(PiwikDataManager.EVT_TABLE_COL_INFO, info);
        contentValues.put(PiwikDataManager.EVT_TABLE_COL_EXTRA_INFO, prepareExtraInfo());
        contentValues.put(PiwikDataManager.EVT_TABLE_COL_TIMESTAMP, getCurrentTimestamp());
        sqLiteDatabase.insert(PiwikDataManager.EVT_TABLE, null, contentValues);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        PiwikDataManager piwikDataManager = PiwikDataManager.getInstance(context);
        SQLiteDatabase sqLiteDatabase = piwikDataManager.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PiwikDataManager.EVT_TABLE, null);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                Log.d(StoreDataTask.class.getName(), " rec: " + cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_INFO)));
                cursor.moveToNext();
            }
        }
        super.onPostExecute(aVoid);
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

    private String prepareExtraInfo() {
        int index = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if (extraInfo != null) {
            for (Map.Entry<String, String> cursor : extraInfo.entrySet()) {
                stringBuilder.append("\"");
                stringBuilder.append(++index);
                stringBuilder.append("\"");
                stringBuilder.append(":");
                stringBuilder.append("[");
                stringBuilder.append("\"");
                stringBuilder.append(cursor.getKey());
                stringBuilder.append("\"");
                stringBuilder.append(",");
                stringBuilder.append("\"");
                stringBuilder.append(cursor.getValue());
                stringBuilder.append("\"");
                stringBuilder.append("]");
                if (extraInfo.size() > 1) {
                    stringBuilder.append(",");
                }
            }
        }
        return stringBuilder.toString();
    }

    private String getUserIdFromPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PiwikDataManager.PREF_USER_ID, null);
    }
}
