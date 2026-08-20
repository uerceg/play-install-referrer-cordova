# Play Install Referrer Library wrapper for Cordova

<table>
    <tr>
        <td align="left">Supported platforms:</td>
        <td align="left"><img src="https://images-fe.ssl-images-amazon.com/images/I/21EctgvtXUL.png" width="16"></td>
    </tr>
    <tr>
        <td align="left">Current version:</td>
        <td align="left"><a href=../../releases/tag/v2.0.0><b>2.0.0</b></a></td>
    </tr>
    <tr>
        <td align="left">Troubles?</td>
        <td align="left"><a href="../../issues/new"><b>Report an issue</b></a></td>
    </tr>
</table>

**cordova-play-install-referrer** is a simple wrapper around Google's [Play Install Referrer Library](https://developer.android.com/google/play/installreferrer/library) which offers basic functionality of obtaining Android referrer information from Cordova app.

More information about Play Install Referrer API can be found in [official Google documentation](https://developer.android.com/google/play/installreferrer/igetinstallreferrerservice).

Version of native Play Install Referrer Library which is being used inside of latest **cordova-play-install-referrer** plugin version is [2.2](https://mvnrepository.com/artifact/com.android.installreferrer/installreferrer/2.2).

## Add plugin to your app

**cordova-play-install-referrer** plugin is hosted on [npm repo](https://www.npmjs.com/package/cordova-play-install-referrer) and can be added from there.

```
cordova plugin add cordova-play-install-referrer
```

**Note**: as of **v2.0.0** the plugin requires **cordova-android 12** or newer. It is tested on cordova-android 15.1. On older cordova-android, use v1.0.1.

**Note**: if you are upgrading from v1.0.1, remove the plugin before adding v2.0.0 - `cordova plugin add` does not upgrade in place, it skips the install and leaves the old native code compiled into your app. See the [migration guide](MIGRATION.md).

## Usage

In order to obtain install referrer details, call **getInstallReferrerInfo** static method of **PlayInstallReferrer** class:

```js
PlayInstallReferrer.getInstallReferrerInfo(function(installReferrerInfo) {
    if (!installReferrerInfo.errorMessage) {
        console.log("install referrer = " + installReferrerInfo.installReferrer);
        console.log("referrer click timestamp seconds = " + installReferrerInfo.referrerClickTimestampSeconds);
        console.log("install begin timestamp seconds = " + installReferrerInfo.installBeginTimestampSeconds);
        console.log("referrer click timestamp server seconds = " + installReferrerInfo.referrerClickTimestampServerSeconds);
        console.log("install begin timestamp seconds = " + installReferrerInfo.installBeginTimestampServerSeconds);
        console.log("install version = " + installReferrerInfo.installVersion);
        console.log("google play instant = " + installReferrerInfo.googlePlayInstant);
        } else {
        console.log("error message: " + installReferrerInfo.errorMessage);
        console.log("error response code: " + installReferrerInfo.errorResponseCode);
    }
});
```

If successfully obtained, map with content of install referrer information will be delivered into callback method. From that map, you can get following install referrer details:

Each field carries the type Google's native `ReferrerDetails` reports it as:

| key | type | description |
| :-- | :--- | :---------- |
| **installReferrer** | `string` or `null` | Install referrer string value. |
| **referrerClickTimestampSeconds** | `number` | Timestamp of when user clicked on URL which redirected him/her to Play Store to download your app. |
| **installBeginTimestampSeconds** | `number` | Timestamp of when app installation on device begun. |
| **referrerClickTimestampServerSeconds** | `number` | Server timestamp of when user clicked on URL which redirected him/her to Play Store to download your app. |
| **installBeginTimestampServerSeconds** | `number` | Server timestamp of when app installation on device begun. |
| **installVersion** | `string` or `null` | Original app version which was installed. |
| **googlePlayInstant** | `boolean` | Information if your app's instant version (if you have one) was launched in past 7 days. |

**Note**: prior to **v2.0.0** every one of these arrived as a string, including `googlePlayInstant` - which meant `"false"`, a truthy value in JavaScript. Error reporting also never worked at all. See the [migration guide](MIGRATION.md) if you are upgrading.

Remaining two fields are indicators of **error** which might have occurred. If error happened, error message is guaranteed that it will be available, so you should first check if error message is **null** or not before trying to read above mentioned fields. Remaining error related fields are:

- Error message (**errorMessage** key).
- Error response code reported by install referrer API (**errorResponseCode** key).

In case error is reported, you can get following information about the error:

- **errorMessage**: Additional string message which describes error more in detail. **Note**: Message field should always be present in error map.
- **errorResponseCode**: Error response code which native Install Referrer Library might return. Full list of potential response codes can be found in [here](https://developer.android.com/reference/com/android/installreferrer/api/InstallReferrerClient.InstallReferrerResponse) (`OK` will never be reported in this property, since it's a success status code). **Note**: Error code field is not always present in error map - only if error created when one of the error codes from native Install Referrer Library is received; otherwise this field will be **undefined**.

## Under the hood

Important thing to notice is that in order to work properly, Play Install Referrer Library requires following permission to be added to your app's `AndroidManifest.xml`:

```xml
<uses-permission android:name="com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE"/>
```

Play Install Referrer Library is added to **cordova-play-install-referrer** plugin as an [Gradle dependency](./plugin.xml#L29) and it will automatically make sure that manifest file ends up with above mentioned permission added to it upon building your app.

## Testing your integration

Most confusion about this plugin turns out to be about how the referrer was tested rather than about the plugin, so it is worth being precise about what is testable.

**What the plugin returns is whatever Google Play hands it.** It does not parse, decode or synthesise the referrer string - if `installReferrer` looks wrong, that is what Play returned.

Values Play returns when it has no custom referrer to give you:

- `utm_source=google-play&utm_medium=organic` - Play considers the install organic
- `utm_source=(not%20set)&utm_medium=(not%20set)` - Play had nothing to attribute

Both are Play's own fallbacks, not failures of the plugin, and neither can be turned into your custom referrer after the fact.

**Sideloading cannot produce a referrer.** `cordova run android`, or installing an APK by hand, means the install never went through Play, so there is nothing to read. Timestamps come back as `0` in that case.

**A referrer only survives a real Play install where the click carried it.** Opening a `details?id=...&referrer=...` link so that the Play Store app resolves it directly will often drop the referrer. Opening the same link in a browser, and letting the browser hand off to Play, tends to preserve it.

**The referrer is read once per install.** Uninstall and reinstall through Play to test again.

**Internal and Closed testing tracks are not a reliable way to verify this.** They can install without carrying a referrer through, which is a frequent source of confusion.

To check the plugin itself rather than the referrer, look at the error path: on a device or emulator without the Play Store you should get `FEATURE_NOT_SUPPORTED` or `SERVICE_UNAVAILABLE` in `errorMessage`. That proves the plugin is wired up correctly.

## Example app

An example app lives in the [**example**](./example) folder of this repository. It is not shipped with the plugin. To run it:

```
cd example
cordova platform add android
cordova plugin add .. --link
cordova run android --device
```

`--link` matters. The plugin lives in the parent directory, which contains the example, so a plain `cordova plugin add ..` copies the plugin - example and all - into `example/plugins/`, and recurses until the path is too long. `--link` symlinks instead, which both avoids that and means edits to the plugin are picked up without re-adding it.

For the same reason the example's **package.json** deliberately does not list the plugin: if it did, `cordova platform add` would try to restore it by copying, and recurse before you got as far as `--link`.

## Migration

Instructions for migrating between plugin versions can be found in [here](./MIGRATION.md).
