package com.anupcowkur.piwiksdk;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class PiwikContentProvider extends ContentProvider {

    // public constants for client development
    public static final String AUTHORITY = "com.anupcowkur.piwiksdk.provider.piwikdbitems";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PiwikDbItems.CONTENT_PATH);
    // helper constants for use with the UriMatcher
    private static final int PIWIK_ITEM_LIST = 1;
    private static final int PIWIK_ITEM_ID = 2;
    private static final UriMatcher URI_MATCHER;
    private SQLiteDatabase db = null;

    @Override
    public boolean onCreate() {

        this.db = PiwikDataManager.getInstance(this.getContext()).getWritableDatabase();

        if (this.db == null) {
            return false;
        }

        if (this.db.isReadOnly()) {
            this.db.close();
            this.db = null;
            return false;
        }
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PiwikDataManager.EVT_TABLE);
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                // all nice and well
                break;
            case PIWIK_ITEM_ID:
                // limit query to one row at most:
                builder.appendWhere(PiwikDbItems._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, null);
        // if we want to be notified of any changes:
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                return PiwikDbItems.CONTENT_TYPE;
            case PIWIK_ITEM_ID:
                return PiwikDbItems.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != PIWIK_ITEM_LIST) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        long id = db.insert(PiwikDataManager.EVT_TABLE, null, values);
        if (id > 0) {
            // notify all listeners of changes and return itemUri:
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into " + PiwikDataManager.EVT_TABLE + ", uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                delCount = db.delete(PiwikDataManager.EVT_TABLE, selection, selectionArgs);
                break;
            case PIWIK_ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = PiwikDbItems._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(PiwikDataManager.EVT_TABLE, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case PIWIK_ITEM_LIST:
                updateCount = db.update(PiwikDataManager.EVT_TABLE, values, selection, selectionArgs);
                break;
            case PIWIK_ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = PiwikDbItems._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(PiwikDataManager.EVT_TABLE, values, where, selectionArgs);
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

    /**
     * Column and content type definitions for the PiwikContentProvider.
     */
    public static interface PiwikDbItems extends BaseColumns {
        public static final Uri CONTENT_URI = PiwikContentProvider.CONTENT_URI;
        public static final String CONTENT_PATH = "items";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.anupcowkur.piwikdbitems";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.anupcowkur.piwikdbitems";
        public static final String[] PROJECTION_ALL = {_ID, PiwikDataManager.EVT_TABLE_COL_USER_ID, PiwikDataManager.EVT_TABLE_COL_INFO, PiwikDataManager.EVT_TABLE_COL_EXTRA_INFO, PiwikDataManager.EVT_TABLE_COL_TIMESTAMP};
    }

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, PiwikDbItems.CONTENT_PATH, PIWIK_ITEM_LIST);
        URI_MATCHER.addURI(AUTHORITY, PiwikDbItems.CONTENT_PATH + "/#", PIWIK_ITEM_ID);
    }
}
