package com.snj.dascleaner.lib;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.snj.dascleaner.BLEActivity;
import com.snj.dascleaner.MainActivity;

public class AndroidBridge {

    private String TAG = "AndroidBridge";

    private WebView mAppView;
    private MainActivity mContext;

    public AndroidBridge(WebView _mAppView, MainActivity _mContext)
    {
        mAppView = _mAppView;
        mContext = _mContext;
    }

    @JavascriptInterface
    public void BLESetting(final String msg) {
        mContext.ShowPairedDevices(0);
    }

    @JavascriptInterface
    public void DoorLock() {
        mContext.DoorLock();
    }

    @JavascriptInterface
    public void DoorUnLock() {
        mContext.DoorUnLock();
    }

    @JavascriptInterface
    public void Start() {
        mContext.Start();
    }

    @JavascriptInterface
    public void Stop() {
        mContext.Stop();
    }

    @JavascriptInterface
    public void Driving() {
        mContext.Driving();
    }
}
