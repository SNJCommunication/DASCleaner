package com.snj.dascleaner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.snj.dascleaner.lib.AndroidBridge;
import com.snj.dascleaner.lib.DasWebViewClient;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    WebView wv_content = null;
    AndroidBridge ab = null;

    RelativeLayout rlly_bledevices = null;

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlly_bledevices = (RelativeLayout)findViewById(R.id.rlly_bledevices);
        rlly_bledevices.setVisibility(View.GONE);

        wv_content = (WebView)findViewById(R.id.wv_content);
        wv_content.setWebViewClient(new DasWebViewClient());
        wv_content.getSettings().setJavaScriptEnabled(true);

        wv_content.loadUrl("http://158.247.198.222:8081/");
        ab = new AndroidBridge(wv_content, this);
        wv_content.addJavascriptInterface(ab, "DasApp");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RESUME");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("DAS회전형 살균제어시스템").setMessage("종료하시겠습니까?");
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finishAffinity();

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    public void SetDeviceListVisibility(int visibility)
    {
        rlly_bledevices.setVisibility(visibility);
        wv_content.setVisibility(View.GONE);
        rlly_bledevices.invalidate();
    }
}