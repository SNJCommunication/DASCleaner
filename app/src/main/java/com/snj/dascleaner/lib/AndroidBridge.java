package com.snj.dascleaner.lib;

import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

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
        //Log.d(TAG, "MSG : " + msg);
        mContext.SetDeviceListVisibility(View.VISIBLE);
    }
}
