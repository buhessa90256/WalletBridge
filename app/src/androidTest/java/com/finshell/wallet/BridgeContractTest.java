package com.finshell.wallet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/** Run on an emulator without FinShell installed: ./gradlew connectedDebugAndroidTest. */
@RunWith(AndroidJUnit4.class)
public final class BridgeContractTest {
    @Test public void testExactColorOsIntentResolvesToExportedBridge() {
        PackageManager manager = getInstrumentation().getTargetContext().getPackageManager();
        ResolveInfo result = manager.resolveActivity(new Intent(WalletLauncher.OPEN_ACTION)
                .setPackage("com.finshell.wallet"), PackageManager.MATCH_DEFAULT_ONLY);
        assertNotNull(result);
        assertEquals("com.finshell.wallet.BridgeActivity", result.activityInfo.name);
        assertTrue(result.activityInfo.exported);
        assertEquals(ActivityInfo.LAUNCH_SINGLE_TOP, result.activityInfo.launchMode);
    }

    @Test public void testNoSensitivePermissionsOrBackgroundComponents() throws Exception {
        PackageInfo info = getInstrumentation().getTargetContext().getPackageManager()
                .getPackageInfo("com.finshell.wallet", PackageManager.GET_PERMISSIONS
                        | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS);
        assertTrue(info.requestedPermissions == null || info.requestedPermissions.length == 0);
        assertTrue(info.services == null || info.services.length == 0);
        assertTrue(info.receivers == null || info.receivers.length == 0);
    }

    @Test public void testUnknownActionDoesNotResolve() {
        ResolveInfo result = getInstrumentation().getTargetContext().getPackageManager()
                .resolveActivity(new Intent("walletbridge.unrelated.action")
                        .setPackage("com.finshell.wallet"), PackageManager.MATCH_DEFAULT_ONLY);
        assertNull(result);
    }

    @Test public void testMissingWalletIsRecoverable() throws Exception {
        final int[] result = {-1};
        if (WalletLauncher.walletIntent(getInstrumentation().getTargetContext()) == null) {
            getInstrumentation().runOnMainSync(() ->
                    result[0] = WalletLauncher.launch(getInstrumentation().getTargetContext()));
            assertEquals(R.string.wallet_missing, result[0]);
        }
    }
}

