package com.finshell.wallet;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Foreground intent bridge. Leaves authentication entirely to Android and Google Wallet. */
public final class BridgeActivity extends Activity {
    private TextView message;
    private boolean busy;
    private boolean completed;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        LinearLayout layout = Screen.create(this);
        Screen.text(this, layout, getString(R.string.title), 28);
        message = Screen.text(this, layout, getString(R.string.opening), 18);
        Screen.button(this, layout, R.string.retry, view -> begin());
        Screen.button(this, layout, R.string.close, view -> finish());
        if (state != null && state.getBoolean("completed")) {
            finish();
            return;
        }
        BridgeLog.received(this);
        begin();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!busy && !completed) {
            BridgeLog.received(this);
            begin();
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("completed", completed);
        super.onSaveInstanceState(state);
    }

    private void begin() {
        if (busy || completed || isFinishing() || isDestroyed()) return;
        busy = true;
        KeyguardManager keyguard = getSystemService(KeyguardManager.class);
        if (keyguard != null && keyguard.isKeyguardLocked()) {
            message.setText(R.string.unlock);
            keyguard.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
                @Override public void onDismissSucceeded() { openWallet(); }
                @Override public void onDismissCancelled() { unlockFailed(); }
                @Override public void onDismissError() { unlockFailed(); }
            });
        } else {
            openWallet();
        }
    }

    private void unlockFailed() {
        if (isFinishing() || isDestroyed()) return;
        busy = false;
        message.setText(R.string.unlock_failed);
        BridgeLog.result(this, R.string.unlock_failed);
    }

    private void openWallet() {
        if (isFinishing() || isDestroyed()) return;
        int error = WalletLauncher.launch(this);
        busy = false;
        if (error == 0) {
            completed = true;
            BridgeLog.result(this, R.string.launched);
            finish();
        } else {
            message.setText(error);
            BridgeLog.result(this, error);
        }
    }
}

