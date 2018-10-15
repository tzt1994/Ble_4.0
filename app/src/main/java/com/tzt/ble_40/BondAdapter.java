package com.tzt.ble_40;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BondAdapter extends RecyclerView.Adapter<BondAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> devicesList;
    private OnClickConnectListener connectListener;

    public BondAdapter(ArrayList<BluetoothDevice> list){
        devicesList = list;
    }

    public void setOnclickConnectListener(OnClickConnectListener listener){
        connectListener = listener;
    }

    public interface OnClickConnectListener{
        void onClickConnect(int position, BluetoothDevice device);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bond_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.bond_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击进行连接
                int position = holder.getAdapterPosition();
                BluetoothDevice device = devicesList.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_NONE)
                connectListener.onClickConnect(position, device);
            }
        });

        holder.bond_unbond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击取消配对
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = devicesList.get(position);
        if (device.getName() != null){
            holder.bond_info.setText(device.getName());
        }else {
            holder.bond_info.setText(device.getAddress());
        }

        if (device.getBondState() == BluetoothDevice.BOND_NONE){
            holder.bond_state.setText("未连接");
        }else if (device.getBondState() == BluetoothDevice.BOND_BONDING){
            holder.bond_state.setText("正在连接");
        }else if (device.getBondState() == BluetoothDevice.BOND_BONDED){
            holder.bond_state.setText("已连接");
        }
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bond_info;
        private TextView bond_state;
        private ImageView bond_unbond;

        public ViewHolder(View itemView) {
            super(itemView);
            bond_info = itemView.findViewById(R.id.bond_info);
            bond_state = itemView.findViewById(R.id.bond_state);
            bond_unbond = itemView.findViewById(R.id.bond_unbond);
        }
    }
}
