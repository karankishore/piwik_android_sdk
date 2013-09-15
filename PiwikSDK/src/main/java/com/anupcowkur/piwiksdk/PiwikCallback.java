package com.anupcowkur.piwiksdk;

import com.koushikdutta.async.future.FutureCallback;

/**
 * Custom callback for network calls in sync adapter.
 */
public class PiwikCallback implements FutureCallback<String> {

    String columnName;

    public PiwikCallback(String columnName) {

        this.columnName = columnName;
    }

    public String getId() {
        return columnName;
    }

    @Override
    public void onCompleted(Exception e, String s) {
    }
}
