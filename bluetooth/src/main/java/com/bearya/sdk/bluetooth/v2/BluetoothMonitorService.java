package com.bearya.sdk.bluetooth.v2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BluetoothMonitorService extends Service {

    private static final UUID BluetoothUUID = java.util.UUID.fromString("14f46c43-afa0-4de3-8654-e4d0bda587f5");
    public static final String ACTION_UPDATE_STATUS = "ACTION_UPDATE_STATUS";
    public static final String ACTION_BLUETOOTH_COMMAND = "ACTION_BLUETOOTH_COMMAND";
    public static final String BLUETOOTH_COMMAND = "BLUETOOTH_COMMAND";
    public static final String UPDATE_STATUS = "UPDATE_STATUS";
    public static final String ACTION_FETCH_STATUS = "ACTION_FETCH_STATUS";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_RECONNECT = "ACTION_RECONNECT";

    public static void start(Context context, BluetoothDevice device) {
        context.startService(new Intent(context, BluetoothMonitorService.class).putExtra("device", device));
    }

    private volatile SocketConnectStatus connectStatus;

    private BluetoothAdapter mBluetoothAdapter;

    private final ConcurrentLinkedQueue<String> actions = new ConcurrentLinkedQueue<>();

    private SocketThread socketThread;

    private BluetoothDevice device;

    private BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_RECONNECT:
                        if (mBluetoothAdapter.isEnabled() && device != null) {
                            updateStatus(SocketConnectStatus.CONNECT_INIT);
                            socketThread = new SocketThread(device);
                            socketThread.start();
                        }
                        break;

                    case ACTION_STOP_SERVICE:
                        stopSelf();
                        break;
                    case ACTION_FETCH_STATUS:
                        if (connectStatus == null) {
                            context.sendBroadcast(new Intent(SocketConnectStatus.CONNECT_INIT.getValue()));
                        } else {
                            context.sendBroadcast(new Intent(connectStatus.getValue()));
                        }
                        break;
                    case ACTION_BLUETOOTH_COMMAND:
                        String bluetoothCommand = intent.getStringExtra(BLUETOOTH_COMMAND);
                        if (!TextUtils.isEmpty(bluetoothCommand) && connectStatus == SocketConnectStatus.CONNECT_SUCCESS) {
                            actions.offer(bluetoothCommand);
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        updateStatus(SocketConnectStatus.CONNECT_SUCCESS);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        updateStatus(SocketConnectStatus.CONNECT_BREAK);
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (mBluetoothAdapter != null) {
                            int state = mBluetoothAdapter.getState();
                            if (state == BluetoothAdapter.STATE_ON) {
                                updateStatus(SocketConnectStatus.CONNECT_OPEN);
                            } else if (state == BluetoothAdapter.STATE_OFF) {
                                updateStatus(SocketConnectStatus.CONNECT_CLOSE);
                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        filter.addAction(ACTION_BLUETOOTH_COMMAND);
        filter.addAction(ACTION_FETCH_STATUS);
        filter.addAction(ACTION_STOP_SERVICE);
        filter.addAction(ACTION_RECONNECT);

        registerReceiver(bluetoothStateBroadcastReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (socketThread != null) {
            socketThread.stopLive();
            socketThread = null;
        }
        if (intent != null) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra("device");
            if (bluetoothDevice != null && mBluetoothAdapter.isEnabled()) {
                device = bluetoothDevice;
                updateStatus(SocketConnectStatus.CONNECT_INIT);
                socketThread = new SocketThread(device);
                socketThread.start();
            }
        }
        return START_STICKY;
    }

    /**
     * 蓝牙连接的状态的维护
     */
    private void updateStatus(SocketConnectStatus newStatus) {
        if (newStatus != null) {
            if (TextUtils.equals(SocketConnectStatus.CONNECT_CLOSE.getValue(), newStatus.getValue())) {
                stopSelf();
            } else if (TextUtils.equals(SocketConnectStatus.CONNECT_FAIL.getValue(), newStatus.getValue())) {
                stopThread();
            } else if (TextUtils.equals(SocketConnectStatus.CONNECT_BREAK.getValue(), newStatus.getValue())) {
                stopThread();
            }
            if (connectStatus == null || !TextUtils.equals(connectStatus.getValue(), newStatus.getValue())) {
                connectStatus = newStatus;
                String statusValue = newStatus.getValue();
                sendBroadcast(new Intent(ACTION_UPDATE_STATUS).putExtra(UPDATE_STATUS, statusValue));
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        device = null;
        unregisterReceiver(bluetoothStateBroadcastReceiver);
        bluetoothStateBroadcastReceiver = null;
        stopThread();
    }

    private synchronized void stopThread() {
        if (socketThread != null) {
            socketThread.stopLive();
            socketThread.stopRunningHandler();
            socketThread.interrupt();
            socketThread = null;
        }
    }

    private class SocketThread extends Thread {

        private BluetoothDevice bluetoothDevice;
        private BluetoothSocket bluetoothSocket = null;
        private OutputStream outputStream = null;
        private boolean isLive = true;

        public SocketThread(BluetoothDevice device) {
            bluetoothDevice = device;
        }

        public void stopLive() {
            isLive = false;
        }

        @Override
        public void run() {

            try {
                if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                updateStatus(SocketConnectStatus.CONNECT_WAITING);
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BluetoothUUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                while (isLive) {
                    if (actions != null && actions.size() > 0) {
                        String first = actions.poll();
                        if (first != null) {
                            outputStream.write(first.getBytes(StandardCharsets.UTF_8));
                            outputStream.flush();
                        }
                    }
                }
            } catch (IOException e) {
                CrashReport.postCatchedException(e);
                updateStatus(SocketConnectStatus.CONNECT_FAIL);
            }
        }

        public void stopRunningHandler() {

            if (actions != null && actions.size() > 0) {
                actions.clear();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    CrashReport.postCatchedException(ex);
                } finally {
                    outputStream = null;
                }
            }

            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException ex) {
                    CrashReport.postCatchedException(ex);
                } finally {
                    bluetoothSocket = null;
                }
            }
        }
    }

}
