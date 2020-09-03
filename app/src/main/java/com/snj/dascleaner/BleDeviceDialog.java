package com.snj.dascleaner;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.snj.dascleaner.lib.LeDeviceListAdapter;

public class BleDeviceDialog extends Dialog {

    private Context context;

    ListView lstv_bledevices = null;
    LeDeviceListAdapter mLeDeviceListAdapter = null;
    public  RelativeLayout rlly_connecting = null;

    public BleDeviceDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_bledevice);

        lstv_bledevices = (ListView)findViewById(R.id.lstv_bledevices);
        mLeDeviceListAdapter = new LeDeviceListAdapter(context);
        lstv_bledevices.setAdapter(mLeDeviceListAdapter);

        rlly_connecting = (RelativeLayout)findViewById(R.id.rlly_connecting);
    }

    public void AddDevice(BluetoothDevice device)
    {
        mLeDeviceListAdapter.addDevice(device);
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
