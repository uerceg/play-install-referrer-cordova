### Version 2.0.0 [20th August 2026]
#### Added
- Added **Testing your integration** chapter to README, explaining what Play actually returns and why sideloaded installs never carry a referrer.
- Added handling of `PERMISSION_ERROR` response code, introduced in Install Referrer Library v2.2, along with a fallback for any code the library adds in the future. Previously an unrecognised code produced no callback at all.
- Added [migration guide](MIGRATION.md).
- Added example app under **example**, rebuilt on Cordova 13 and cordova-android 15.1. It is added with `cordova plugin add .. --link` - a plain copy would recurse, since the plugin directory contains the example.

#### Changed
- Install referrer fields now carry the type Google's native `ReferrerDetails` reports, instead of everything being flattened to a string: `googlePlayInstant` is a `boolean`, the four timestamps are `number`, and the two string fields are a string or `null`. **`"false"` is truthy in JavaScript**, so a plain `if (info.googlePlayInstant)` check has been wrong until now. `errorResponseCode` is likewise the native `int` rather than a string name. See the [migration guide](MIGRATION.md).
- Updated native Play Install Referrer Library to **v2.2**.
- Raised the declared engine floor from `cordova-android >=4.0.0` to `>=12.0.0`, which is a version range that can actually build this plugin.
- Renamed the native class package from **com.ugi.play_install_referrer** to **com.uerceg.play_install_referrer**.
- Declared an explicit `files` allowlist in **package.json** and dropped **.npmignore**, so the published package cannot leak the example app or local build state.
- Reporting a vulnerability now points at GitHub's private vulnerability reporting instead of a public issue.

#### Fixed
- Failures are now delivered to the callback. The native side sends errors as `PluginResult.Status.ERROR`, which Cordova routes to the error callback - and the plugin passed an empty function as that callback, so **every failure was silently swallowed** and the `else` branch documented in the README could never run.
- Connection to the install referrer service is now ended once the details are read.
- A lost connection to the install referrer service is now reported to the callback. `onInstallReferrerServiceDisconnected` used to be ignored, so a disconnect arriving before a value had been delivered left the caller waiting forever.
- `getInstallReferrer()` is now read off the main thread. It is a blocking IPC call and the listener is invoked on the main thread, which risked ANRs.
- Stopped keeping the callback registered after a one-shot result, and removed unused imports.
- Removed the example app's generated **node_modules**, **platforms** and **plugins** directories from version control - 832 files which should never have been committed.

**Note**: For migration to v2.0.0, please check [migration guide](MIGRATION.md). Upgrading needs `cordova plugin rm cordova-play-install-referrer` first - Cordova does not upgrade a plugin in place, and adding on top silently leaves v1.0.1's native code in your app.

---

### Version 1.0.1 [7th September 2020]
#### Changed
- Changed repository structure to have README published with plugin package to **npm** repository.

---

### Version 1.0.0 [24th August 2020]
#### Added
- Initial release of **cordova-play-install-referrer** plugin.
