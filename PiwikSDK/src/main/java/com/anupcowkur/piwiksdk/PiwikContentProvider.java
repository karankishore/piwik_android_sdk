package com.anupcowkur.piwiksdk;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content provider for sync adapter.
 */
public class PiwikContentProvider extends ContentProvider {

    // public constants for client development
    public static final String AUTHORITY = "com.anupcowkur.piwiksdk.provider.piwikdbitems";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PiwikDBItems.CONTENT_PATH);
    // helper constants for use with the UriMatcher
    private static final int PIWIK_ITEM_LIST = 1;
    private static final int PIWIK_ITEM_ID = 2;
    private static final UriMatcher URI_MATCHER;
    private SQLiteDatabase db = null;

    @Override
    public boolean onCreate() {

        db = PiwikDatabaseHelper.getInstance(getContext()).getWritableDatabase();

        if (db == null) {
            return false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            return false;
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PiwikDatabaseHelper.EVT_TABLE);
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                break;
            case PIWIK_ITEM_ID:
                // limit query to one row at most:
                builder.appendWhere(PiwikDBItems._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                return PiwikDBItems.CONTENT_TYPE;
            case PIWIK_ITEM_ID:
                return PiwikDBItems.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != PIWIK_ITEM_LIST) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        long id = db.insert(PiwikDatabaseHelper.EVT_TABLE, null, values);
        if (id > 0) {
            // notify all listeners of changes and return itemUri:
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Error while inserting into " + PiwikDatabaseHelper.EVT_TABLE + ", uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delCount;
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                delCount = db.delete(PiwikDatabaseHelper.EVT_TABLE, selection, selectionArgs);
                break;
            case PIWIK_ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = PiwikDBItems._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(PiwikDatabaseHelper.EVT_TABLE, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return delCount;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                updateCount = db.update(PiwikDatabaseHelper.EVT_TABLE, values, selection, selectionArgs);
                break;
            case PIWIK_ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = PiwikDBItems._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(PiwikDatabaseHelper.EVT_TABLE, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, PiwikDBItems.CONTENT_PATH, PIWIK_ITEM_LIST);
        URI_MATCHER.addURI(AUTHORITY, PiwikDBItems.CONTENT_PATH + "/#", PIWIK_ITEM_ID);
    }
}
