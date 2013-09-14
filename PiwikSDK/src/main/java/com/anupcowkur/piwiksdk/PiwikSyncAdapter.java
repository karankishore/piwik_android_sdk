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

import com.koushikdutta.async.future.FutureCallback;
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

        ArrayList<String> localDbItems = new ArrayList();
        Cursor curDbItems = null;
        try {
            curDbItems = provider.query(PiwikContentProvider.PiwikDbItems.CONTENT_URI, PiwikContentProvider.PiwikDbItems.PROJECTION_ALL, null, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (curDbItems != null) {
            while (curDbItems.moveToNext()) {
                final Cursor finalCurDbItems = curDbItems;
                Ion.with(getContext(), getRequestUrl(curDbItems)).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String s) {
                        if (e == null) {
                            try {
                                provider.delete(PiwikContentProvider.PiwikDbItems.CONTENT_URI, PiwikContentProvider.PiwikDbItems._ID + " = ? ", new String[]{finalCurDbItems.getString(finalCurDbItems.getColumnIndex("_id"))});
                            } catch (RemoteException e1) {
                            }
                        }

                    }
                });
            }
            curDbItems.close();
        }

        for (String url : localDbItems) {
            Ion.with(getContext(), url);
        }

    }

    String getRequestUrl(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_USER_ID));
        String type = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_USER_ID));
        String extraInfo = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_USER_ID));
        String timestamp = cursor.getString(cursor.getColumnIndex(PiwikDataManager.EVT_TABLE_COL_TIMESTAMP));

        return serverUrl + type + "?" + timestamp + "&_id=" + id + "&_cvar={" + extraInfo + "}";
    }
}
