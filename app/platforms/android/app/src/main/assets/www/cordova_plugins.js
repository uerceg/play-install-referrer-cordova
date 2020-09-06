cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-play-install-referrer.PlayInstallReferrer",
      "file": "plugins/cordova-play-install-referrer/www/play_install_referrer.js",
      "pluginId": "cordova-play-install-referrer",
      "clobbers": [
        "PlayInstallReferrer"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-whitelist": "1.3.4",
    "cordova-play-install-referrer": "1.0.0"
  };
});