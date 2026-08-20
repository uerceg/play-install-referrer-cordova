document.addEventListener('deviceready', function () {
    var button = document.getElementById('read');
    var status = document.getElementById('status');
    var details = document.getElementById('details');

    function row(key, value, isError) {
        var dt = document.createElement('dt');
        dt.textContent = key;
        var dd = document.createElement('dd');
        dd.textContent = String(value);
        if (isError) { dd.className = 'error'; }
        details.appendChild(dt);
        details.appendChild(dd);
    }

    button.addEventListener('click', function () {
        status.textContent = 'reading...';
        details.textContent = '';
        button.disabled = true;

        PlayInstallReferrer.getInstallReferrerInfo(function (installReferrerInfo) {
            button.disabled = false;

            if (installReferrerInfo.errorMessage) {
                status.textContent = 'error';
                row('errorMessage', installReferrerInfo.errorMessage, true);
                row('errorResponseCode', installReferrerInfo.errorResponseCode, true);
                return;
            }

            status.textContent = 'ok';
            row('install referrer', installReferrerInfo.installReferrer);
            row('referrer click', installReferrerInfo.referrerClickTimestampSeconds);
            row('install begin', installReferrerInfo.installBeginTimestampSeconds);
            row('referrer click (server)', installReferrerInfo.referrerClickTimestampServerSeconds);
            row('install begin (server)', installReferrerInfo.installBeginTimestampServerSeconds);
            row('install version', installReferrerInfo.installVersion);
            row('google play instant', installReferrerInfo.googlePlayInstant);

            // the plugin delivers each field with its native type
            console.log('typeof googlePlayInstant = ' + typeof installReferrerInfo.googlePlayInstant);
            console.log('typeof referrerClickTimestampSeconds = ' + typeof installReferrerInfo.referrerClickTimestampSeconds);
        });
    });
}, false);
