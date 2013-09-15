package com.anupcowkur.piwiksdk;

import android.accounts.Account;
import android.accounts.AccountManager;
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
    private final AccountManager mAccountManager;
    private final String serverUrl;

    public PiwikSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        serverUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("serverUrl", null);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, final ContentProviderClient provider, SyncResult syncResult) {

        Cursor curDbItems = null;
        try {
            curDbItems = provider.query(PiwikContentProvider.PiwikDbItems.CONTENT_URI, PiwikContentProvider.PiwikDbItems.PROJECTION_ALL, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (curDbItems != null) {
            ArrayList<String> indices = new ArrayList<String>();
            while (curDbItems.moveToNext()) {
                indices.add(curDbItems.getString(curDbItems.getColumnIndex("_id")));
                String url = getRequestUrl(curDbItems);
                Ion.with(getContext(), url).asString().setCallback(new PiwikCallback(curDbItems.getString(curDbItems.getColumnIndex("_id"))) {
                    @Override
                    public void onCompleted(Exception e, String s) {
                        if (e == null) {
                            try {
                                provider.delete(PiwikContentProvider.PiwikDbItems.CONTENT_URI, PiwikContentProvider.PiwikDbItems._ID + " = ? ", new String[]{columnName});
                            } catch (RemoteException e1) {
                            }
                        }

                    }
                });

            }
            curDbItems.close();
        }

    }

    String getRequestUrl(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_USER_ID));
        String type = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_INFO));
        String extraInfo = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_EXTRA_INFO));
        String timestamp = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_TIMESTAMP));

        String returnUrl = serverUrl + "?" + "idsite=1&rec=1&url=" + type + "&" + timestamp + "&_id=" + id;
        if (id.equals(""))
            returnUrl += "&_cvar={" + extraInfo + "}";

        return returnUrl;
    }
}
