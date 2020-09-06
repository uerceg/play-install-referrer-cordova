# Play Install Referrer Library wrapper for Cordova

<table>
    <tr>
        <td align="left">Supported platforms:</td>
        <td align="left"><img src="https://images-fe.ssl-images-amazon.com/images/I/21EctgvtXUL.png" width="16"></td>
    </tr>
    <tr>
        <td align="left">Current version:</td>
        <td align="left"><a href=../../../releases/tag/cordova-v1.0.0><b>1.0.0</b></a></td>
    </tr>
    <tr>
        <td align="left">Troubles?</td>
        <td align="left"><a href="../../../issues/new"><b>Report an issue</b></a></td>
    </tr>
</table>

**cordova-play-install-referrer** is a simple wrapper around Google's [Play Install Referrer Library](https://developer.android.com/google/play/installreferrer/library) which offers basic functionality of obtaining Android referrer information from Cordova app.

More information about Play Install Referrer API can be found in [official Google documentation](https://developer.android.com/google/play/installreferrer/igetinstallreferrerservice).

Version of native Play Install Referrer Library which is being used inside of latest **cordova-play-install-referrer** plugin version is [2.1](https://mvnrepository.com/artifact/com.android.installreferrer/installreferrer/2.1).

## Add plugin to your app

**cordova-play-install-referrer** plugin is hosted on [npm repo](https://www.npmjs.com/package/cordova-play-install-referrer) and can be added from there.

```
cordova plugin add cordova-play-install-referrer
```

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

- Install referrer string value (**installReferrer** key).
- Timestamp of when user clicked on URL which redirected him/her to Play Store to download your app (**referrerClickTimestampSeconds** key).
- Timestamp of when app installation on device begun (**installBeginTimestampSeconds** key).
- Server timestamp of when user clicked on URL which redirected him/her to Play Store to download your app (**referrerClickTimestampServerSeconds** key).
- Server timestamp of when app installation on device begun (**installBeginTimestampServerSeconds** key).
- Original app version which was installed (**installVersion** key).
- Information if your app's instant version (if you have one) was launched in past 7 days (**googlePlayInstant** key).

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

Play Install Referrer Library is added to **cordova-play-install-referrer** plugin as an [Gradle dependency](./plugin/plugin.xml#L30) and it will automatically make sure that manifest file ends up with above mentioned permission added to it upon building your app.
