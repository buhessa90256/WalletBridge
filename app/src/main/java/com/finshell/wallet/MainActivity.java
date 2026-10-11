package com.finshell.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private TextView status;
    private TextView lastRequest;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout layout = Screen.create(this);
        Screen.text(this, layout, getString(R.string.title), 30);
        Screen.text(this, layout, getString(R.string.intro), 17);
        status = Screen.text(this, layout, "", 16);
        Screen.button(this, layout, R.string.open_wallet, view ->
                startActivity(new Intent(WalletLauncher.OPEN_ACTION).setPackage(getPackageName())));
        Screen.text(this, layout, getString(R.string.setup), 22);
        Screen.text(this, layout, getString(R.string.setup_text), 16);
        Screen.text(this, layout, getString(R.string.diagnostics), 22);
        Screen.text(this, layout, getString(R.string.diagnostic_text), 14).setTextIsSelectable(true);
        lastRequest = Screen.text(this, layout, "", 14);
    }

    @Override public void onResume() {
        super.onResume();
        status.setText(WalletLauncher.walletIntent(this) == null
                ? R.string.wallet_missing : R.string.wallet_ready);
        lastRequest.setText(BridgeLog.summary(this));
    }
}

