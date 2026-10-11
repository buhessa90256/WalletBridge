# WalletBridge

A minimal, independent Android bridge for the ColorOS power-button wallet route reported in the prior PMA110 / ColorOS 17 diagnosis:

- Package: `com.finshell.wallet`
- Incoming activity action: `finshell.wallet.intent.action.OPEN`
- Destination: Google Wallet, `com.google.android.apps.walletnfcrel`

The prior `com.heytap.wallet` accessibility shim did not receive the expected route. This project implements the FinShell package/action instead. It is an experimental compatibility build, not an OPPO or Google product. A successful build does not establish that a particular firmware permits installation or dispatches this intent to a third-party replacement.

## Installation prerequisite — check this first

Android requires matching signing certificates when replacing an existing package. This APK is not signed by OPPO/FinShell. **Disabling the stock app, or running `pm uninstall --user 0` against a factory system app, does not remove its system-wide package/signing identity.** If factory FinShell remains registered, installing this same-package bridge can fail with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`.

Read-only checks (ADB; omit `adb shell` inside aShell):

```sh
adb shell pm list packages -u com.finshell.wallet
adb shell pm path com.finshell.wallet
adb shell dumpsys package com.finshell.wallet
```

If this is a factory/system package, stop and preserve the stock wallet. Do not delete system APKs or disable signature verification to install this bridge. A separate-package redirect strategy would need further on-device evidence. This build does not claim to solve that constraint.

If the package is absent system-wide, install the APK normally:

```sh
adb install WalletBridge-0.2.0-debug.apk
```

Uninstalling an ordinary user-installed package can remove its local data; do not do that blindly for a wallet. An older WalletBridge signed with another build key can also cause a signing conflict.

## Setup and verification

1. Install and set up genuine Google Wallet. This app does not change the default NFC/payment service.
2. Open WalletBridge and tap **Test bridge**. This exercises the same package-scoped action, including normal device authentication.
3. In ColorOS settings select Wallet for the double-press power shortcut. Preserve the existing diagnosed configuration; the app does not modify secure settings.
4. Test while unlocked, then while locked. Normal PIN/password/biometric authentication is required when locked.
5. Reopen WalletBridge to see the last request timestamp and outcome.

To test dispatch without the physical shortcut:

```sh
adb shell am start -W -a finshell.wallet.intent.action.OPEN -p com.finshell.wallet
```

If the command works but double-power does not, collect the actual firmware route:

```sh
adb logcat -c
# Double-press power, then unlock.
adb logcat -d -s ActivityTaskManager ActivityManager
```

Review logs before sharing; they may contain other apps' information. An explicit firmware component name, signature allowlist, or privileged permission check may require a different design. This manifest claims only the observed action, not guessed OEM activities.

## Behavior and privacy

The launcher shows setup and diagnostics. The exported bridge wakes its own screen, requests Android keyguard dismissal, and only then starts the installed Google Wallet launcher activity. Cancelled authentication and a missing/disabled wallet show a recoverable message. Incoming extras and URLs are never forwarded. Only the last request time/outcome is kept in private local preferences.

No Internet permission, accessibility service, foreground service, key interception, payment emulation, analytics, or secure-settings writes. Google Wallet handles all payment and account data.

## Build

JDK 17, Android SDK platform 35, Gradle 8.9, Android Gradle Plugin 8.7.3:

```sh
./gradlew assembleDebug assembleDebugAndroidTest lintDebug
# Windows: gradlew.bat assembleDebug assembleDebugAndroidTest lintDebug
```

Set `ANDROID_HOME` or an untracked `local.properties` with `sdk.dir`.
Installable output: `app/build/outputs/apk/debug/app-debug.apk`.
`assembleRelease` produces an unsigned release APK unless you configure private signing locally.

GitHub Actions builds the debug APK and publishes **WalletBridge-0.2.0-debug** with its SHA-256 checksum. A separate emulator job verifies intent resolution, component/permission restrictions and missing-wallet handling. Debug builds use the build machine's debug key; independent machines/runs may sign differently. These are test APKs, not a stable production update channel. Do not publish private signing keys.

## Validation boundaries

Build/lint and emulator results are recorded by CI. Hardware double-power dispatch, factory package replaceability, ColorOS keyguard behavior, and actual Google Wallet/payment behavior require testing on the target device. Emulator contract tests cannot verify those OEM-specific conditions.

References: [Android app signing](https://developer.android.com/studio/publish/app-signing), [keyguard API](https://developer.android.com/reference/android/app/KeyguardManager), [AGP 8.7 compatibility](https://developer.android.com/build/releases/agp-8-7-0-release-notes).

