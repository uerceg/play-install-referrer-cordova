//
//  PlayInstallReferrer.java
//  cordova-play-install-referrer
//
//  Created by Uglješa Erceg (@ugi) on 31st July 2020.
//  Copyright (c) 2020 Uglješa Erceg. All rights reserved.
//

package com.ugi.play_install_referrer;

import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class PlayInstallReferrerCordova extends CordovaPlugin {
    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstallReferrerInfo")) {
            // callback is getting pinged always with JSON object
            // error is indicated if JSON contains 'errorMessage' key
            try {
                final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this.cordova.getActivity().getApplicationContext()).build();
                referrerClient.startConnection(new InstallReferrerStateListener() {
                    @Override
                    public void onInstallReferrerSetupFinished(int responseCode) {
                        switch (responseCode) {
                            case InstallReferrerClient.InstallReferrerResponse.OK: {
                                try {
                                    ReferrerDetails response = referrerClient.getInstallReferrer();
                                    String installReferrer = null;
                                    long referrerClickTimestampSeconds = 0L;
                                    long installBeginTimestampSeconds = 0L;
                                    long referrerClickTimestampServerSeconds = 0L;
                                    long installBeginTimestampServerSeconds = 0L;
                                    String installVersion = null;
                                    boolean googlePlayInstant = false;
                                    if (response != null) {
                                        installReferrer = response.getInstallReferrer();
                                        referrerClickTimestampSeconds = response.getReferrerClickTimestampSeconds();
                                        installBeginTimestampSeconds = response.getInstallBeginTimestampSeconds();
                                        referrerClickTimestampServerSeconds = response.getReferrerClickTimestampServerSeconds();
                                        installBeginTimestampServerSeconds = response.getInstallBeginTimestampServerSeconds();
                                        installVersion = response.getInstallVersion();
                                        googlePlayInstant = response.getGooglePlayInstantParam();

                                        // create the map with install referrer details and ping callback
                                        Map<String, String> installReferrerInfo = new HashMap<>();
                                        installReferrerInfo.put("installReferrer", installReferrer);
                                        installReferrerInfo.put("referrerClickTimestampSeconds", Long.toString(referrerClickTimestampSeconds));
                                        installReferrerInfo.put("installBeginTimestampSeconds", Long.toString(installBeginTimestampSeconds));
                                        installReferrerInfo.put("referrerClickTimestampServerSeconds", Long.toString(referrerClickTimestampServerSeconds));
                                        installReferrerInfo.put("installBeginTimestampServerSeconds", Long.toString(installBeginTimestampServerSeconds));
                                        installReferrerInfo.put("installVersion", installVersion);
                                        installReferrerInfo.put("googlePlayInstant", Boolean.toString(googlePlayInstant));
                                        JSONObject jsonInstallReferrerInfo = new JSONObject(installReferrerInfo);
                                        PluginResult pluginResult = new PluginResult(Status.OK, jsonInstallReferrerInfo);
                                        pluginResult.setKeepCallback(true);
                                        callbackContext.sendPluginResult(pluginResult);
                                    } else {
                                        Map<String, String> error = new HashMap<>();
                                        error.put("errorMessage", "Response from install referrer library was null");
                                        JSONObject jsonError = new JSONObject(error);
                                        PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                        pluginResult.setKeepCallback(true);
                                        callbackContext.sendPluginResult(pluginResult);
                                    }
                                } catch (RemoteException ex) {
                                    Map<String, String> error = new HashMap<>();
                                    error.put("errorMessage", "Exception while reading install referrer info: " + ex.getMessage());
                                    JSONObject jsonError = new JSONObject(error);
                                    PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                    pluginResult.setKeepCallback(true);
                                    callbackContext.sendPluginResult(pluginResult);
                                }
                                break;
                            }
                            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED: {
                                Map<String, String> error = new HashMap<>();
                                error.put("errorMessage", "FEATURE_NOT_SUPPORTED");
                                error.put("errorResponseCode", "FEATURE_NOT_SUPPORTED");
                                JSONObject jsonError = new JSONObject(error);
                                PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                pluginResult.setKeepCallback(true);
                                callbackContext.sendPluginResult(pluginResult);
                                break;
                            }
                            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE: {
                                Map<String, String> error = new HashMap<>();
                                error.put("errorMessage", "SERVICE_UNAVAILABLE");
                                error.put("errorResponseCode", "SERVICE_UNAVAILABLE");
                                JSONObject jsonError = new JSONObject(error);
                                PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                pluginResult.setKeepCallback(true);
                                callbackContext.sendPluginResult(pluginResult);
                                break;
                            }
                            case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR: {
                                Map<String, String> error = new HashMap<>();
                                error.put("errorMessage", "DEVELOPER_ERROR");
                                error.put("errorResponseCode", "DEVELOPER_ERROR");
                                JSONObject jsonError = new JSONObject(error);
                                PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                pluginResult.setKeepCallback(true);
                                callbackContext.sendPluginResult(pluginResult);
                                break;
                            }
                            case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED: {
                                Map<String, String> error = new HashMap<>();
                                error.put("errorMessage", "SERVICE_DISCONNECTED");
                                error.put("errorResponseCode", "SERVICE_DISCONNECTED");
                                JSONObject jsonError = new JSONObject(error);
                                PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                                pluginResult.setKeepCallback(true);
                                callbackContext.sendPluginResult(pluginResult);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onInstallReferrerServiceDisconnected() {
                        // no need to handle this
                    }
                });
            } catch (Throwable ex) {
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", "Exception while starting connection with referrer client: " + ex.getMessage());
                JSONObject jsonError = new JSONObject(error);
                PluginResult pluginResult = new PluginResult(Status.ERROR, jsonError);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        } else {
            // TODO: log error
            return false;    
        }
        return true;
    }
}
