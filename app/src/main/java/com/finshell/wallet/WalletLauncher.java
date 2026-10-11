package com.finshell.wallet;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

/** Only launches the genuine Google Wallet package; never forwards incoming extras or URIs. */
final class WalletLauncher {
    static final String TARGET_PACKAGE = "com.google.android.apps.walletnfcrel";
    static final String OPEN_ACTION = "finshell.wallet.intent.action.OPEN";

    private WalletLauncher() {}

    static Intent walletIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        return intent;
    }

    static int launch(Context context) {
        Intent intent = walletIntent(context);
        if (intent == null) return R.string.wallet_missing;
        try {
            context.startActivity(intent);
            return 0;
        } catch (ActivityNotFoundException | SecurityException exception) {
            return R.string.wallet_failed;
        }
    }
}

