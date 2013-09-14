package com.anupcowkur.piwiksdk;

import android.content.Context;
import java.util.Map;

public class PiwikClient {

    public static void initPiwik(Context context, String serverUrl, String userId) {
        if(userId == null){
            userId = generateUserId();
        }
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

    private static String generateUserId(){
        return " ";
    }
}
