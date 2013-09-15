package com.anupcowkur.piwiksdk;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Column and content type definitions for the content provider.
 */
public interface PiwikDBItems extends BaseColumns {
    public static final Uri CONTENT_URI = PiwikContentProvider.CONTENT_URI;
    public static final String CONTENT_PATH = "items";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.anupcowkur.piwikdbitems";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.anupcowkur.piwikdbitems";
    public static final String[] PROJECTION_ALL = {_ID, PiwikDatabaseHelper.EVT_TABLE_COL_USER_ID, PiwikDatabaseHelper.EVT_TABLE_COL_INFO, PiwikDatabaseHelper.EVT_TABLE_COL_EXTRA_INFO, PiwikDatabaseHelper.EVT_TABLE_COL_TIMESTAMP};

}
