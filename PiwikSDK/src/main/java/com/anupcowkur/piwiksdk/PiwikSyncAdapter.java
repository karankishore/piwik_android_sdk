package com.anupcowkur.piwiksdk;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class PiwikSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String serverUrl;

    public PiwikSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        serverUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(SharedPreferenceKeys.PREF_SERVER_URL, null);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {

        Cursor curDbItems = null;
        try {
            curDbItems = provider.query(PiwikDBItems.CONTENT_URI, PiwikDBItems.PROJECTION_ALL, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (curDbItems != null) {
            ArrayList<String> indices = new ArrayList<String>();
            while (curDbItems.moveToNext()) {
                indices.add(curDbItems.getString(curDbItems.getColumnIndex(PiwikDatabaseHelper.EV_TABLE_COL_ID)));
                String url = getRequestUrl(curDbItems);
                Ion.with(getContext(), url).asString().setCallback(new PiwikCallback(curDbItems.getString(curDbItems.getColumnIndex(PiwikDatabaseHelper.EV_TABLE_COL_ID))) {
                    @Override
                    public void onCompleted(Exception e, String s) {
                        if (e == null) {
                            try {
                                provider.delete(PiwikDBItems.CONTENT_URI, PiwikDBItems._ID + " = ? ", new String[]{columnName});
                            } catch (RemoteException e1) {
                            }
                        }

                    }
                });

            }
            curDbItems.close();
        }

    }

    /**
     * Get formatted url to make a GET request on our server.
     */
    String getRequestUrl(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(PiwikDatabaseHelper.EVT_TABLE_COL_USER_ID));
        String type = cursor.getString(cursor.getColumnIndex(PiwikDatabaseHelper.EVT_TABLE_COL_INFO));
        String timestamp = cursor.getString(cursor.getColumnIndex(PiwikDatabaseHelper.EVT_TABLE_COL_TIMESTAMP));

        String returnUrl = serverUrl + "?" + "idsite=1&rec=1&url=http://example.com" + type + "&" + timestamp + "&_id=" + id + "&rand=" + Math.random() * Math.random();

        return returnUrl;
    }
}
