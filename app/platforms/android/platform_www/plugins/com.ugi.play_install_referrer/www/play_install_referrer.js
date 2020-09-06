cordova.define("com.ugi.play_install_referrer.PlayInstallReferrer", function(require, exports, module) {
function callCordovaCallback(action, callback) {
    var args = Array.prototype.slice.call(arguments, 2);

    cordova.exec(callback,
        function errorHandler(err) { },
        'PlayInstallReferrer',
        action,
        args
    );
}

var PlayInstallReferrer = {
    getInstallReferrerInfo: function(callback) {
        callCordovaCallback('getInstallReferrerInfo', callback);
    },
};

module.exports = PlayInstallReferrer;

});
