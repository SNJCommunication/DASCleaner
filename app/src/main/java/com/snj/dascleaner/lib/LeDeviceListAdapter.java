package com.snj.dascleaner.lib;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snj.dascleaner.MainActivity;
import com.snj.dascleaner.R;

import java.util.ArrayList;
import java.util.List;

public class LeDeviceListAdapter extends BaseAdapter {

    private Context context;
    List<BluetoothDevice> bleDeviceList = null;

    public LeDeviceListAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        if(bleDeviceList == null) return  null;
        return bleDeviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addDevice(BluetoothDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public void removeDevice(BluetoothDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BluetoothDevice device = bleDeviceList.get(i);
            if ((bleDevice.getName() + bleDevice.getAddress()).equals(device.getName() + device.getAddress())) {
                bleDeviceList.remove(i);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.txt_blename =(TextView)convertView.findViewById(R.id.txt_blename);

        }


        final BluetoothDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            String name = bleDevice.getName();
            holder.txt_blename.setText(name);
        }

        final BluetoothDevice bledevice = getItem(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LeDeviceListAdapter", bledevice.getName());

                ((MainActivity)context).ConnectDevice(bledevice);
            }
        });

        return  convertView;
    }

    class ViewHolder {
        TextView txt_blename;
        BluetoothDevice device;
    }
}
