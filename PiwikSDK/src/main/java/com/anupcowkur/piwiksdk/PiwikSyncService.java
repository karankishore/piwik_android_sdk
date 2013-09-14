package com.anupcowkur.piwiksdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PiwikSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PiwikSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new PiwikSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
