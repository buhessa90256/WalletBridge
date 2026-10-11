package com.finshell.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

/** Stores only the last request time and outcome locally, never intent payloads or wallet data. */
final class BridgeLog {
    private BridgeLog() {}
    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("bridge_diagnostics", Context.MODE_PRIVATE);
    }
    static void received(Context context) {
        prefs(context).edit().putLong("time", System.currentTimeMillis())
                .putString("result", context.getString(R.string.received)).apply();
    }
    static void result(Context context, int result) {
        prefs(context).edit().putString("result", context.getString(result)).apply();
    }
    static String summary(Context context) {
        SharedPreferences preferences = prefs(context);
        long time = preferences.getLong("time", 0);
        if (time == 0) return context.getString(R.string.no_invocation);
        return context.getString(R.string.last_invocation,
                DateFormat.format("yyyy-MM-dd HH:mm:ss", time).toString(),
                preferences.getString("result", context.getString(R.string.received)));
    }
}

