package com.bearya.robot.fairystory.ui.observer;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;

import androidx.activity.ComponentActivity;
import androidx.annotation.DrawableRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.CardChildAction;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class HandyBlockObserver implements LifecycleObserver {

    private final ComponentActivity mContext;

    public HandyBlockObserver(ComponentActivity activity) {
        mContext = activity;
        activity.getLifecycle().addObserver(this);
    }

    private static final UUID UUID_SERVICE = UUID.fromString("7e87d2af-4781-4a48-a827-abfa6b322e47");
    private static final UUID UUID_CHAR_WRITE_NOTIFY = UUID.fromString("a0de9f4b-4f42-4dd9-a655-7140b15159fe");

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private BluetoothGattServer mBluetoothGattServer = null; // BLE服务端

    private BlockCallback mCallback = null;

    private BroadcastReceiver registerReceiver = null;

    // BLE广播Callback
    private final AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            updateBlockMessage(mContext.getString(R.string.blocks_wait_command), R.drawable.handy_block_wait);
        }

        @Override
        public void onStartFailure(int errorCode) {
            updateBlockMessage(mContext.getString(R.string.blocks_start_fail), R.drawable.handy_block_error);
        }
    };

    private final BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {

        private final StringBuffer responseString = new StringBuffer();

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value); // 响应客户端

            if (value != null && value.length > 0) {
                if ((value[0] & 0xff) == 0xf8 && (value[value.length - 1] & 0xff) == 0xf9) {
                    characteristic.setValue("SUCCESS");
                    updateBlockMessage(mContext.getString(R.string.blocks_command_received), R.drawable.handy_block_success);
                    blockInflate(new String(Arrays.copyOfRange(value, 3, value.length - 2)));
                    mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, true);
                } else if ((value[0] & 0xff) == 0xf8 && (value[value.length - 1] & 0xff) != 0xf9) {
                    responseString.append(new String(Arrays.copyOfRange(value, 3, value.length)));
                } else if ((value[0] & 0xff) != 0xf8 && (value[value.length - 1] & 0xff) == 0xf9) {
                    if (value.length > 2) {
                        responseString.append(new String(Arrays.copyOfRange(value, 0, value.length - 2)));
                    } else if (value.length == 1 && responseString.length() > 0) {
                        responseString.deleteCharAt(responseString.length() - 1);
                    }
                    updateBlockMessage(mContext.getString(R.string.blocks_command_received), R.drawable.handy_block_success);
                    String response = responseString.toString();
                    blockInflate(response);
                    responseString.delete(0, responseString.length());
                    mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, true);
                } else {
                    responseString.append(new String(value, StandardCharsets.UTF_8));
                }
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (status == 0) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    updateBlockMessage(mContext.getString(R.string.blocks_link_connected), R.drawable.handy_block_wait);
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    updateBlockMessage(mContext.getString(R.string.blocks_link_disconnected), R.drawable.handy_block_error);
                } else {
                    updateBlockMessage(mContext.getString(R.string.blocks_link_status_change), R.drawable.handy_block_wait);
                }
            } else {
                updateBlockMessage(mContext.getString(R.string.blocks_link_fail), R.drawable.handy_block_error);
            }
        }
    };

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        openBluetooth();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (mBluetoothLeAdvertiser != null)
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        mBluetoothGattServer.clearServices();
        mBluetoothGattServer.close();
        mBluetoothGattServer = null;
        mContext.getLifecycle().removeObserver(this);
    }

    private void openGattService() {
        if (registerReceiver != null) {
            mContext.unregisterReceiver(registerReceiver);
            registerReceiver = null;
        }

        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        }

        // 广播设置
        AdvertiseSettings advertiseSettingsBuilder = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY) // 广播模式: 低功耗,平衡,低延迟
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH) // 发射功率级别: 极低,低,中,高
                .setConnectable(true)  // 能否连接,广播分为可连接广播和不可连接广播,必须要开启可连接的BLE广播,其它设备才能发现并连接BLE服务端
                .build();
        // 广播数据
        AdvertiseData advertiseDataBuild = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .build();
        // 扫描响应数据
        AdvertiseData advertiseResponseDataBuild = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(UUID_SERVICE))
                .build();

        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.startAdvertising(advertiseSettingsBuilder, advertiseDataBuild, advertiseResponseDataBuild, mAdvertiseCallback);
        }

        BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // 添加指定UUID的可写characteristic
        bluetoothGattService.addCharacteristic(new BluetoothGattCharacteristic(UUID_CHAR_WRITE_NOTIFY, BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_WRITE));

        // 添加 可读+通知 characteristic
        BluetoothManager systemService = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothGattServer = systemService.openGattServer(mContext, mBluetoothGattServerCallback);
        mBluetoothGattServer.addService(bluetoothGattService);

    }

    @SuppressLint("HardwareIds")
    private void openBluetooth() {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (!BluetoothAdapter.getDefaultAdapter().getName().startsWith("BeiYa-")) {
                BluetoothAdapter.getDefaultAdapter().setName("BeiYa-" + Build.SERIAL);
            }
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                updateBlockMessage(mContext.getString(R.string.blocks_starting), R.drawable.handy_block_wait);
                BluetoothAdapter.getDefaultAdapter().enable();
                registerReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent != null && intent.getAction() != null) {
                            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                                int state = BluetoothAdapter.getDefaultAdapter().getState();
                                if (state == BluetoothAdapter.STATE_ON) {
                                    openGattService();
                                } else if (state == BluetoothAdapter.STATE_OFF) {
                                    updateBlockMessage(mContext.getString(R.string.blocks_closed), R.drawable.handy_block_error);
                                }
                            }
                        }
                    }
                };
                mContext.registerReceiver(registerReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            } else {
                openGattService();
            }
        } else {
            updateBlockMessage(mContext.getString(R.string.blocks_unsupported), R.drawable.handy_block_error);
        }
    }

    private void updateBlockMessage(String message, @DrawableRes int icon) {
        if (mCallback!=null) {
            mCallback.onBlockMessage(message, icon);
        }
    }

    public interface BlockCallback {
        void onBlockDef(List<CardParentAction> cards);
        void onBlockMessage(String message, @DrawableRes int icon);
    }

    public void setBlockCallback(BlockCallback callback) {
        mCallback = callback;
    }

    private void blockInflate(final String blocks) {
        if (blocks.isEmpty()) {
            updateBlockMessage(mContext.getString(R.string.blocks_command_empty), R.drawable.handy_block_error);
            return;
        }
        String message = blocks;
        String prefix = "START:beiya;";

        if (message.startsWith(prefix)) {
            message = blocks.substring(prefix.length());
        }

        if (message.endsWith("END;")) {
            message = message.substring(0, message.length() - "END;".length());
        }

        if (message.length() == 0) {
            updateBlockMessage(mContext.getString(R.string.blocks_command_empty), R.drawable.handy_block_error);
            return;
        }
        final List<CardParentAction> cardParentActions = new LinkedList<>();
        String[] actions = message.split(";");
        if (actions.length > 0) {
            for (String action : actions) {
                String[] split = action.split(":");
                CardParentAction parentAction = translate(split);
                if (parentAction != null) cardParentActions.add(parentAction);
            }
            mContext.runOnUiThread(() -> {
                if (mCallback != null) {
                    mCallback.onBlockDef(cardParentActions);
                }
            });
        } else {
            updateBlockMessage(mContext.getString(R.string.blocks_command_empty), R.drawable.handy_block_error);
        }

    }

    private CardParentAction translate(String[] actions) {
        CardParentAction cardParentAction = null;
        if (actions.length > 0) {
            for (String action : actions) {
                switch (action) {
                    case "F": // 前进
                        cardParentAction = new CardParentAction(CardType.ACTION_FORWARD);
                        break;
                    case "B":
                    case "T": // 向后
                        cardParentAction = new CardParentAction(CardType.ACTION_BACKWARD);
                        break;
                    case "L": // 向左
                        cardParentAction = new CardParentAction(CardType.ACTION_LEFT);
                        break;
                    case "R": // 向右
                        cardParentAction = new CardParentAction(CardType.ACTION_RIGHT);
                        break;
                    case "LOOP": // 循环
                        cardParentAction = new CardParentAction(CardType.ACTION_LOOP);
                        break;
                    case "POOL": // 结束循环
                        cardParentAction = new CardParentAction(CardType.ACTION_CLOSURE);
                        break;
                    case "DJ0": // 变身水
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_WATER);
                        }
                        break;
                    case "DJ1": // 跳舞笛
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_FLUTE);
                        }
                        break;
                    case "DJ2": // 铁皮船
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_BOAT);
                        }
                        break;
                    case "DJ3": // 毛衣针
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_NEEDLES);
                        }
                        break;
                    case "DJ4": // 魔法棒
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_MAGIC);
                        }
                        break;
                    case "DJ5": // 粘粘弹
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_BULLET);
                        }
                        break;
                    case "DJ6": // 逗猫棒
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_STICK);
                        }
                        break;
                    case "DJ7": // 葵花战士
                        if (cardParentAction != null && cardParentAction.parentActionId == CardType.ACTION_FORWARD) {
                            cardParentAction.childAction = new CardChildAction(CardType.ACTION_SOLDIER);
                        }
                        break;
                    default:
                        if (cardParentAction != null && TextUtils.isDigitsOnly(action)
                                && (cardParentAction.parentActionId == CardType.ACTION_FORWARD || cardParentAction.parentActionId == CardType.ACTION_LOOP)) {
                            try {
                                cardParentAction.stepCount = Integer.parseInt(action);
                            } catch (Exception exception) {
                                cardParentAction.stepCount = 1;
                            }
                        }
                        break;
                }
            }
        }
        return cardParentAction;
    }

}
