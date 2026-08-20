## Migrate cordova-play-install-referrer plugin to v2.0.0

The API shape is unchanged - `getInstallReferrerInfo(callback)` still takes a single callback and still flags failures with an `errorMessage` key. Four things can break, which is why this is a major.

### Errors actually reach your callback now

Prior to v2.0.0 the error path was dead code. The native side sent failures as `PluginResult.Status.ERROR`, which Cordova routes to the *error* callback - and the plugin passed an empty function as that callback, so every failure was silently swallowed. The `else` branch documented in the README could never run.

Both paths now feed the same callback, so the documented contract is real:

```js
PlayInstallReferrer.getInstallReferrerInfo(function (installReferrerInfo) {
    if (installReferrerInfo.errorMessage) {
        // this used to be unreachable
    }
});
```

If you relied on the callback only ever firing on success, add the `errorMessage` check.

### Fields now carry their native types

Every field used to arrive as a string, whatever the native library reported it as. They now carry the type Google's `ReferrerDetails` actually returns:

| field | native type | before | now |
| :---- | :---------- | :----- | :-- |
| `installReferrer` | `String` | `string` | `string` or `null` |
| `installVersion` | `String` | `string` | `string` or `null` |
| `googlePlayInstant` | `boolean` | `"true"` / `"false"` | `true` / `false` |
| `referrerClickTimestampSeconds` | `long` | `"1755640000"` | `1755640000` |
| `installBeginTimestampSeconds` | `long` | `"1755640000"` | `1755640000` |
| `referrerClickTimestampServerSeconds` | `long` | `"1755640000"` | `1755640000` |
| `installBeginTimestampServerSeconds` | `long` | `"1755640000"` | `1755640000` |

The one to watch is **`googlePlayInstant`**, because `"false"` is truthy in JavaScript:

```js
if (installReferrerInfo.googlePlayInstant) {
    // this used to run even when the value was "false"
}
```

Timestamps no longer need parsing:

```js
// before
new Date(parseInt(installReferrerInfo.referrerClickTimestampSeconds, 10) * 1000)
// now
new Date(installReferrerInfo.referrerClickTimestampSeconds * 1000)
```

`errorResponseCode` is likewise the native `int` rather than a string name. `errorMessage` still carries the readable name, so you rarely need the number:

```js
// before
if (installReferrerInfo.errorResponseCode === 'FEATURE_NOT_SUPPORTED') {
// now
if (installReferrerInfo.errorResponseCode === 2) {
```

The values are `-1` `SERVICE_DISCONNECTED`, `1` `SERVICE_UNAVAILABLE`, `2` `FEATURE_NOT_SUPPORTED`, `3` `DEVELOPER_ERROR`, `4` `PERMISSION_ERROR`.

### The plugin now requires cordova-android 12 or newer

The declared engine floor used to be `>=4.0.0`, which was never realistic. v2.0.0 declares `>=12.0.0` and is tested on cordova-android 15.1.

### The native class moved package

`com.ugi.play_install_referrer.PlayInstallReferrerCordova` is now `com.uerceg.play_install_referrer.PlayInstallReferrerCordova`. Nothing to do in your own code unless you referenced the class directly from native code - Cordova wires it up from `plugin.xml`.

It does change how you upgrade, though.

### Remove the plugin before adding v2.0.0

**Do not add v2.0.0 on top of an existing install.** Cordova does not upgrade a plugin in place - it skips the install and only rewrites your **package.json**:

```
$ cordova plugin add cordova-play-install-referrer
Plugin "cordova-play-install-referrer" already installed on android.
Adding cordova-play-install-referrer to package.json
```

You end up with v2.0.0 in your manifest and v1.0.1's native code still compiled into the app - old string fields, swallowed errors and all - with nothing to tell you. Remove first:

```
cordova plugin rm cordova-play-install-referrer
cordova plugin add cordova-play-install-referrer
```

Done in that order the old class is deleted and only `com/uerceg/...` remains.

If you already tried adding on top, the removal afterwards can leave the old class orphaned in the platform. Delete it by hand:

```
rm -rf platforms/android/app/src/main/java/com/ugi
```

or remove and re-add the platform, which regenerates everything:

```
cordova platform rm android && cordova platform add android
```

Leaving it there is not fatal - the two classes have different fully qualified names and `config.xml` points at the new one - but it is dead code in your build.
