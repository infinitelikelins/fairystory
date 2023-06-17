package com.bearya.robot.fairystory.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.adapter.DevicesAdapter;
import com.bearya.sdk.bluetooth.v2.BluetoothMonitorService;
import com.bearya.sdk.bluetooth.v2.SocketConnectStatus;

public class DiscoveryActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView bluetoothDevicesRecyclerView;
    private AppCompatTextView castingSettings;
    private AppCompatTextView toCasting;

    private ProgressBar pbScanning;

    private DevicesAdapter mAdapter;

    private BluetoothAdapter defaultAdapter;

    private BluetoothStateChangeReceiver bluetoothStateChangeReceiver;

    private BluetoothDeviceFoundReceiver bluetoothDeviceFoundReceiver;
    private boolean isBluetoothDeviceFoundRegister = false;
    private BroadcastReceiver broadcastReceiver;

    public static void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, DiscoveryActivity.class), 200);
    }

    private BluetoothDevice mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        bluetoothDevicesRecyclerView = findViewById(R.id.rv_bluetooth);
        castingSettings = findViewById(R.id.casting_setting);
        toCasting = findViewById(R.id.to_casting);
        pbScanning = findViewById(R.id.pb_bluetooth_scanning);

        withClick(R.id.btnBack,this);
        toCasting.setOnClickListener(this);
        castingSettings.setOnClickListener(this);

        mAdapter = new DevicesAdapter();

        bluetoothDevicesRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, BluetoothDevice device) {
            }

            @Override
            public void onItemCheckChanged(BluetoothDevice device) {
                toCasting.setEnabled(true);
                mDevice = device;
            }
        });

        defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        final boolean enabled = defaultAdapter != null && defaultAdapter.isEnabled();
        castingSettings.setSelected(enabled);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        registerReceiver(bluetoothStateChangeReceiver, intentFilter);

        MusicUtil.stopMusic();
        MusicUtil.stopBgMusic();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action != null) {
                    boolean isCasting = false;
                    if (SocketConnectStatus.CONNECT_SUCCESS.getValue().equals(action)
                            || SocketConnectStatus.CONNECT_WAITING.getValue().equals(action)) {
                        isCasting = true;
                    }

                    if (enabled) {
                        if (isCasting) {
                            castingSettings.setText(getString(R.string.bluetooth_casting));
                            toCasting.setVisibility(View.GONE);
                            bluetoothDevicesRecyclerView.setVisibility(View.GONE);
                        } else {
                            castingSettings.setText(getString(R.string.bluetooth));
                            if (defaultAdapter.isDiscovering()) {
                                defaultAdapter.cancelDiscovery();
                            }
                            registerDeviceFoundEvent();
                            defaultAdapter.startDiscovery();
                        }
                    } else {
                        toCasting.setVisibility(View.GONE);
                        bluetoothDevicesRecyclerView.setVisibility(View.GONE);
                        castingSettings.setText(getString(R.string.bluetooth));
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();

        for (SocketConnectStatus value : SocketConnectStatus.values()) {
            filter.addAction(value.getValue());
        }

        registerReceiver(broadcastReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast(new Intent(BluetoothMonitorService.ACTION_FETCH_STATUS));
    }

    /**
     * 这个是蓝牙设备发现的过程事件
     */
    private void registerDeviceFoundEvent() {
        if (!isBluetoothDeviceFoundRegister) {
            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            bluetoothDeviceFoundReceiver = new BluetoothDeviceFoundReceiver();

            registerReceiver(bluetoothDeviceFoundReceiver, filter);

            isBluetoothDeviceFoundRegister = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isBluetoothDeviceFoundRegister) {
            unregisterReceiver(bluetoothDeviceFoundReceiver);
        }

        unregisterReceiver(bluetoothStateChangeReceiver);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.casting_setting) {
            castingSwitch();
        } else if (id == R.id.to_casting) {
            toReturnCasting();
        } else if (id == R.id.btnBack) {
            finish();
        }

    }

    private void castingSwitch() {

        if (defaultAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.closing_bluetooth), Toast.LENGTH_SHORT).show();
            castingSettings.setText(getString(R.string.bluetooth));
            defaultAdapter.disable();
            toCasting.setEnabled(false);
        } else {
            Toast.makeText(this, getString(R.string.opening_bluetooth), Toast.LENGTH_SHORT).show();
            defaultAdapter.enable();
        }

    }

    private void toReturnCasting() {

        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }

        if (mDevice == null) {
            for (BluetoothDevice device : mAdapter.getDevices()) {
                if (TextUtils.equals(device.getAddress(), BaseApplication.getInstance().getMAC())) {
                    mDevice = device;
                    break;
                }
            }
        }
        Intent intent = getIntent();
        if (mDevice != null) {
            intent.putExtra("device", mDevice);
        }
        setResult(200, intent);
        finish();
    }

    private class BluetoothStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && TextUtils.equals(intent.getAction(), BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (defaultAdapter != null) {
                    if (defaultAdapter.isEnabled()) {
                        castingSettings.setSelected(true);
                        toCasting.setVisibility(View.VISIBLE);
                        bluetoothDevicesRecyclerView.setVisibility(View.VISIBLE);

                        if (defaultAdapter.isDiscovering()) {
                            defaultAdapter.cancelDiscovery();
                        }
                        registerDeviceFoundEvent();
                        defaultAdapter.startDiscovery();
                    } else {
                        castingSettings.setSelected(false);
                        toCasting.setVisibility(View.GONE);
                        bluetoothDevicesRecyclerView.setVisibility(View.GONE);
                        mAdapter.clear();
                        if (defaultAdapter.isDiscovering()) {
                            defaultAdapter.cancelDiscovery();
                        }
                    }
                }
            }
        }
    }

    private class BluetoothDeviceFoundReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice != null && scanDevice.getName() != null && scanDevice.getAddress() != null) {
                    mAdapter.addItemData(scanDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                pbScanning.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                pbScanning.setVisibility(View.GONE);
                isBluetoothDeviceFoundRegister = false;
                unregisterReceiver(this);
            }
        }
    }
}