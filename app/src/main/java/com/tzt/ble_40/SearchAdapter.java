package com.tzt.ble_40;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> deviceList;
    private OnClickConnectListener listener;

    SearchAdapter(ArrayList<BluetoothDevice> list){
        deviceList = list;
    }

    public void setOnClickConnectListener(OnClickConnectListener onClickConnectListener){
        listener = onClickConnectListener;
    }

    public interface OnClickConnectListener{
        void onSearchClickConnect(BluetoothDevice device, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                BluetoothDevice device = deviceList.get(position);
                holder.search_state.setText("正在连接");
                listener.onSearchClickConnect(device, position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        if (device.getName() != null){
            holder.search_info.setText(device.getName());
        }else {
            holder.search_info.setText(device.getAddress());
        }

        holder.search_state.setText("");
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView search_info;
        private TextView search_state;
        private LinearLayout search_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            search_layout = (LinearLayout) itemView;
            search_info = itemView.findViewById(R.id.search_info);
            search_state = itemView.findViewById(R.id.search_state);
        }
    }
}
