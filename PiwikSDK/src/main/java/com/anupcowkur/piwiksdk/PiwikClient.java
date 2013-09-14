package com.anupcowkur.piwiksdk;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

public class PiwikClient {

    public static void initPiwik(Context context, String serverUrl, String userId) {
        if (userId == null) {
            userId = generateUserId();
            Log.d(PiwikClient.class.getName(), " userId: " + userId);
        }
        storeServerUrlToPreferences(context, serverUrl, userId);
    }

    public static void trackEvent(Context context, String eventInfo, Map<String, String> extraInfo) {
        storeData(context, eventInfo, extraInfo);
    }

    public static void trackView(Context context, String viewInfo, Map<String, String> extraInfo) {

    }

    public static void trackCrash(Context context, String crashInfo, Map<String, String> extraInfo) {

    }

    private static void storeData(Context context, String eventInfo, Map<String, String> extraInfo) {
        new StoreDataTask(context, eventInfo, extraInfo).execute();
    }

    private static String generateUserId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(16);
    }

    private static void storeServerUrlToPreferences(Context context, String serverUrl, String userId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PiwikDataManager.PREF_SERVER_URL, serverUrl).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PiwikDataManager.PREF_USER_ID, userId).commit();
    }
}
