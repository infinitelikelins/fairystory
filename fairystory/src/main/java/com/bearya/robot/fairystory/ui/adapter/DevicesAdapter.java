package com.bearya.robot.fairystory.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.fairystory.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    private List<BluetoothDevice> mDevices;

    private OnItemClickListener onItemClickListener;

    public DevicesAdapter() {
        mDevices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, int position) {

        final int adapterPosition = holder.getAdapterPosition();
        final BluetoothDevice device = mDevices.get(adapterPosition);

        holder.name.setText(device.getName());

        final boolean selected = TextUtils.equals(BaseApplication.getInstance().getMAC(), device.getAddress());
        holder.state.setVisibility(selected ? View.VISIBLE : View.GONE);
        holder.state.setSelected(selected);

        if(selected){
            onItemClickListener.onItemCheckChanged(device);
        }
        holder.itemView.setOnClickListener(view -> {
            BaseApplication.getInstance().updateBTMac(device.getAddress()) ;
            notifyDataSetChanged();
            onItemClickListener.onItemClick(view, adapterPosition, device);
        });
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView state;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_bl_item_name);
            state = itemView.findViewById(R.id.im_bl_item_state);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addItemData(BluetoothDevice device) {

        if (device == null || TextUtils.isEmpty(device.getAddress()) || TextUtils.isEmpty(device.getName())) {
            return;
        }

        for (BluetoothDevice mDevice : mDevices) {
            if (TextUtils.equals(mDevice.getAddress(), device.getAddress())) {
                return;
            }
        }

        mDevices.add(device);
        notifyDataSetChanged();

    }

    public void clear() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getDevices(){
        return mDevices;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, BluetoothDevice device);
        void onItemCheckChanged(BluetoothDevice device);
    }
}
