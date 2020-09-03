package com.snj.dascleaner.lib;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.snj.dascleaner.BLEActivity;
import com.snj.dascleaner.MainActivity;

public class AndroidBridge {

    private String TAG = "AndroidBridge";
    final public Handler handler = new Handler();

    private WebView mAppView;
    private MainActivity mContext;

    String hh = "0";

    public AndroidBridge(WebView _mAppView, MainActivity _mContext)
    {
        mAppView = _mAppView;
        mContext = _mContext;
    }

    @JavascriptInterface
    public void BLESetting(final String msg) {
        //mContext.ShowPairedDevices(0);
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

    @JavascriptInterface
    public void setTemperature(String t) {
        mContext.setTemperature(t);
    }

    @JavascriptInterface
    public void setHumidity(String t) {
        mContext.setHumidity(t);
    }

    @JavascriptInterface
    public void setUltraTime(String t) {
        mContext.setUltraTime(t);
    }

    @JavascriptInterface
    public void setLightTime(String t) {
        mContext.setLightTime(t);
    }

    @JavascriptInterface
    public void setUltra(String t) {
        mContext.setUltra(t);
    }

    @JavascriptInterface
    public void setLight(String t) {
        mContext.setLight(t);
    }

    @JavascriptInterface
//    public void GetTemperature(String t) { mAppView.loadUrl("javascript:getTemperature('" + t + "')"); }
    public void GetTemperature(String t) {
//        mAppView.loadUrl("javascript:function testHello() { var ct = document.getElementById('currentTemperature'); ct.innerHTML = 'aaa'; } testHello();");
        final String tt = t;

        handler.post(new Runnable() {
            @Override
            public void run() {
                mAppView.loadUrl("javascript:getTemperature('" + tt + "')");
            }
        });
    }

    @JavascriptInterface
    public void GetHumidity(String h) {

        try {
            int temp = Integer.parseInt(h);
            hh = String.format("%d", temp);
        }
        catch (Exception ex)
        {

        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                mAppView.loadUrl("javascript:getHumidity('" + hh + "')");
            }
        });
    }
}
