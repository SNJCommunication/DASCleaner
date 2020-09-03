package com.snj.dascleaner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snj.dascleaner.lib.AndroidBridge;
import com.snj.dascleaner.lib.DasWebViewClient;
import com.snj.dascleaner.lib.LeDeviceListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

public class MainActivity extends AppCompatActivity {


    final String TAG = "MainActivity";
    final int SCAN_PERIOD = 10000;

    WebView wv_content = null;
    AndroidBridge ab = null;




    TextView txt_debug = null;



    BluetoothAdapter mBluetoothAdapter = null;
    Handler mHandler = null;
    boolean mScanning = false;
    Set<BluetoothDevice> mBondedDevices = null;

    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mReadCharacteristic = null;
    private BluetoothGattCharacteristic mWriteCharateristic = null;

    private final String SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    private final String WRITE_UUID = "0000fff2-0000-1000-8000-00805f9b34fb";
    private final String READ_UUID = "0000fff1-0000-1000-8000-00805f9b34fb";

    byte[] sendData = null;

    BleDeviceDialog dlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        wv_content = (WebView)findViewById(R.id.wv_content);
        wv_content.setWebViewClient(new DasWebViewClient());
        wv_content.getSettings().setJavaScriptEnabled(true);

        wv_content.loadUrl("http://158.247.198.222/das");
        ab = new AndroidBridge(wv_content, this);
        wv_content.addJavascriptInterface(ab, "DasApp");

        txt_debug = (TextView)findViewById(R.id.txt_debug);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBondedDevices = mBluetoothAdapter.getBondedDevices();
        mHandler = new Handler();



        dlg = new BleDeviceDialog(MainActivity.this);
        dlg.show();


        for(BluetoothDevice device : mBondedDevices)
        {
            dlg.AddDevice(device);
        }


//        lstv_bledevices.setVisibility(View.VISIBLE);

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // SCAN_PERIOD 값만큼 시간이 지나면 스캐닝 중지
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    stopScanBLE();
//                    invalidateOptionsMenu();


                }
            }, SCAN_PERIOD);

            mScanning = true;
            startScanBLE();


        } else {
            mScanning = false;
            stopScanBLE();
        }
        invalidateOptionsMenu();
    }

    // BLE 스캔시작
    private void startScanBLE(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
        }
        else
        {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    // BLE 스캔중지
    private void stopScanBLE(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }
        else
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // BLE 기기가 스캔되면 호출. 롤리팝 이하 버전
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dlg.AddDevice(device);
                }
            });
        }
    };

    // BLE 기기가 스캔되면 호출. 롤리팝 이상 버전
    private ScanCallback mScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            dlg.AddDevice(device);
        }
    };

    public void ConnectDevice(BluetoothDevice device) {

        if(mScanning)
        {
            stopScanBLE();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mScanning = false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mCallback );
    }

    BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);

            if(newState == 2)   // Connected
            {
                mBluetoothGatt.discoverServices();
                try {
                    dlg.dismiss();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                //Log.d(TAG, "discovered : " + gatt.ge)
                findGattServices(gatt);


            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            try
            {
                byte[] readByte = characteristic.getValue();

                Log.d(TAG, "RECV : " + new String(readByte));
            }
            catch(Exception e)
            {
                Log.d(TAG, e.toString());
            }
        }
    };

    private boolean findGattServices(BluetoothGatt gattServices) {

        mReadCharacteristic = null;
        mWriteCharateristic = null;

        for (BluetoothGattService gattService : gattServices.getServices()){
            HashMap<String, String> currentServiceData = new HashMap<String, String>();

            if(gattService.getUuid().toString().equals(SERVICE)){
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                    if(gattCharacteristic.getUuid().toString().equals(READ_UUID)){
                        final int charaProp = gattCharacteristic.getProperties();

                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
                            try{
                                mReadCharacteristic = gattCharacteristic;
                                List<BluetoothGattDescriptor> list = mReadCharacteristic.getDescriptors();
                                Log.d(TAG, "read characteristic found : " + charaProp);

                                mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                                //리시버 설정
//
//                                BluetoothGattDescriptor descriptor = mReadCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
//                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                                mBluetoothGatt.writeDescriptor(descriptor);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                return false;
                            }
                        }
                        else{
                            Log.d(TAG, "read characteristic prop is invalid : " + charaProp);
                        }
                    }
                    else if(gattCharacteristic.getUuid().toString().equalsIgnoreCase(WRITE_UUID)){
                        final int charaProp = gattCharacteristic.getProperties();

                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
                            Log.d(TAG, "write characteristic found : " + charaProp);
                            mWriteCharateristic = gattCharacteristic;
                        }
                        else{
                            Log.d(TAG, "write characteristic prop is invalid : " + charaProp);
                        }
                    }
                }
            }
        }


        return true;
    }

    public void writeData(byte[] data){
        try{

            boolean result = false;
            if(mBluetoothGatt.connect())
            {
                if(mWriteCharateristic == null){
                    Log.i(TAG, "Write gatt characteristic is null");
                } else if(mReadCharacteristic == null){
                    Log.i(TAG, "Read gatt characteristic is null");
                } else {
                    int dataLen = data.length;

                    String sendDataG = "";
                    for(int i=0; i<data.length; i++){
                        sendDataG += String.format("%02x", data[i]);
                    }
                    System.out.println("BLE Command 데이터 : " + sendDataG);
                    sendData = data;

                    Thread.sleep(5);

                    mWriteCharateristic.setValue(sendData);

                    if(mWriteCharateristic == null){
                        Log.d("Ble UUID가 없습니다.", "");
                        mBluetoothGatt.disconnect();
                    }else {
                        mBluetoothGatt.writeCharacteristic(mWriteCharateristic);
                    }
//                    Handler hd = new Handler();
//                    hd.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    },5);
                }
            }
            else
            {
                Log.i(TAG, "Bluetooth gatt is not connected");
            }
        }
        catch (Exception e)
        {
            Log.e("", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scanLeDevice(true);
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("DAS회전형 살균제어시스템").setMessage("종료하시겠습니까?");
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mBluetoothGatt.disconnect();
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

    public void DoorLock()
    {
        byte[] cmd = new byte[]{(byte)'$', (byte)'D', (byte)'L', (byte)'U', (byte)'U', (byte)'U', 0x00, (byte)'@'};
        cmd[6] = (byte)(cmd[1] + cmd[2] + cmd[3] + cmd[4] + cmd[5]);

        try {
            writeData(cmd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void DoorUnLock()
    {
        byte[] cmd = new byte[]{(byte)'$', (byte)'D', (byte)'O', (byte)'U', (byte)'U', (byte)'U', 0x00, (byte)'@'};
        cmd[6] = (byte)(cmd[1] + cmd[2] + cmd[3] + cmd[4] + cmd[5]);

        try {
            writeData(cmd);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Start()
    {
        byte[] cmd = new byte[]{(byte)'$', (byte)'E', (byte)'S', (byte)'U', (byte)'U', (byte)'U', 0x00, (byte)'@'};
        cmd[6] = (byte)(cmd[1] + cmd[2] + cmd[3] + cmd[4] + cmd[5]);

        try {
            writeData(cmd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Stop()
    {
        byte[] cmd = new byte[]{(byte)'$', (byte)'E', (byte)'T', (byte)'U', (byte)'U', (byte)'U', 0x00, (byte)'@'};
        cmd[6] = (byte)(cmd[1] + cmd[2] + cmd[3] + cmd[4] + cmd[5]);

        try {
            writeData(cmd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Driving()
    {
        byte[] cmd = new byte[]{(byte)'$', (byte)'G', (byte)'O', (byte)'U', (byte)'U', (byte)'U', 0x00, (byte)'@'};
        cmd[6] = (byte)(cmd[1] + cmd[2] + cmd[3] + cmd[4] + cmd[5]);

        try {
            writeData(cmd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}