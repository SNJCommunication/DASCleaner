package com.snj.dascleaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.snj.dascleaner.lib.GLOBAL;

public class EntryActivity extends Activity {

    final int LOCATION_PERMISSION_REQUEST_CODE = 0x1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        if( !getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH_LE ) ) {
            Toast.makeText(EntryActivity.this, "이 장치는 BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            // OS 버전이 마시멜로 이상이라면 위치 권한이 있는지 점검 후 없다면 사용자에게 위치 권한을 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // 위치 권한이 있는지 점검
                // 없다면 -1 반환
                int permission = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);

                // 권한이 없을 때
                if (permission == PackageManager.PERMISSION_DENIED)
                {
                    String[] permissions = new String[1];
                    permissions[0] = android.Manifest.permission.ACCESS_COARSE_LOCATION; // 사용자에게 요청할 권한
                    requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE); // 사용자에게 권한 요청
                }
                // 권한이 있을 때
                else
                {
                    // 변수 값을 true 로 변경
                    GLOBAL.isPermissionAllowed = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(EntryActivity.this, MainActivity.class);
                            startActivity(i);
                            finishAffinity();
                        }
                    }, 1000);
                }
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 위치 권한 요청의 응답값인지 체크
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // 권한을 획득
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(EntryActivity.this, MainActivity.class);
                        startActivity(i);
                        finishAffinity();
                    }
                }, 1000);

            }
            // 권한 획득 실패
            else
            {
                Toast.makeText(this, "블루투스 사용권한이 없습니다. 앱을 종료합니다.", Toast.LENGTH_SHORT);
                finish();
            }
        }
    }
}