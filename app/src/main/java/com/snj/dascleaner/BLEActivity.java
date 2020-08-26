package com.snj.dascleaner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BLEActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태

    ListView lstv_bledevices = null;

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        lstv_bledevices = (ListView)findViewById(R.id.lstv_bledevices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 디바이스 블루투스 미지원
        if(bluetoothAdapter == null) {
            Toast.makeText(BLEActivity.this, "이 장치는 BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if(bluetoothAdapter.isEnabled())
            {
                selectBluetoothDevice();
            }
            else
            {
                // 블루투스를 활성화 하기 위한 다이얼로그 출력

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                // 선택한 값이 onActivityResult 함수에서 콜백된다.

                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        switch (requestCode) {

            case REQUEST_ENABLE_BT :

                if(requestCode == RESULT_OK) { // '사용'을 눌렀을 때

                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                }
                else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;
        }
    }

    public void selectBluetoothDevice() {

        int pariedDeviceCount = 0;

        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.

        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장

        pariedDeviceCount = devices.size();

        // 페어링 되어있는 장치가 없는 경우

        if(pariedDeviceCount == 0) {

            // 페어링을 하기위한 함수 호출

        }
        // 페어링 되어있는 장치가 있는 경우
        else {

            // 디바이스를 선택하기 위한 다이얼로그 생성

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장

            List<String> list = new ArrayList<>();

            // 모든 디바이스의 이름을 리스트에 추가

            for(BluetoothDevice bluetoothDevice : devices) {

                list.add(bluetoothDevice.getName());

            }

            list.add("취소");



            // List를 CharSequence 배열로 변경

            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);

            list.toArray(new CharSequence[list.size()]);



            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너

            builder.setItems(charSequences, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {

                    // 해당 디바이스와 연결하는 함수 호출

                    //connectDevice(charSequences[which].toString());

                }

            });



            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정

            builder.setCancelable(false);

            // 다이얼로그 생성

            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }

    }



}