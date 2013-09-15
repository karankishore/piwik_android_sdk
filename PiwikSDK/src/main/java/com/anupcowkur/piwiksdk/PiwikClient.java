package com.anupcowkur.piwiksdk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.math.BigInteger;
import java.security.SecureRandom;

public class PiwikClient {
    private static AccountManager accountManager = null;
    private static Account account = null;

    public static void initPiwik(Context context, String serverUrl, String userId) {
        if (userId == null) {
            userId = generateUserId();
        }
        storeAuthInfo(context, serverUrl, userId);
        accountManager = AccountManager.get(context);
        account = new Account(Authenticator.DEFAULT_ACCOUNT, Authenticator.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null);
    }

    /**
     * Store the server url and user id in shared preferences.
     */
    private static void storeAuthInfo(Context context, String serverUrl, String userId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SharedPreferenceKeys.PREF_SERVER_URL, serverUrl).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SharedPreferenceKeys.PREF_USER_ID, userId).commit();
    }

    private static String generateUserId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(16).substring(0, 16);
    }

    /**
     * Track user defined event.
     *
     * @param type type of event. ex: "settings/color_change" would track the color_change button click event in settings screen.
     */
    public static void trackEvent(Context context, String type) {
        storeData(context, "/" + type);
    }

    /**
     * Stores the data in the local db in a background thread.
     */
    private static void storeData(Context context, String type) {
        new StoreDataTask(context, type).execute();
    }

    /**
     * Immediately sync data with server without waiting for automatic periodic sync.
     */
    public static void syncImmediately() {

        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(account, PiwikContentProvider.AUTHORITY, extras);
    }
}
