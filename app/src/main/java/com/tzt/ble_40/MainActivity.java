package com.tzt.ble_40;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,BondAdapter.OnClickConnectListener{
    private LinearLayout layout_switch;                                                             //开关布局
    private ImageView iv_switch;                                                                    //开关图片
    private TextView my_deveice_name;                                                               //我的设备名称
    private RecyclerView bonded_recyc;                                                              //绑定列表
    private RecyclerView search_recyc;                                                              //搜索列表
    private LinearLayout layout_on_off;                                                             //蓝牙状态改变时的布局

    private ArrayList<BluetoothDevice> bondList = new ArrayList<>();
    private BondAdapter bondAdapter;
    private ArrayList<BluetoothDevice> searchList = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        if (state == BluetoothAdapter.STATE_ON){                                    //蓝牙打开

                        }else if (state == BluetoothAdapter.STATE_OFF) {                            //蓝牙关闭

                        }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        bindListener();
    }

    public void initView(){
        layout_switch = findViewById(R.id.layout_switch);
        iv_switch = findViewById(R.id.iv_switch);
        my_deveice_name = findViewById(R.id.my_deveice_name);
        bonded_recyc = findViewById(R.id.bonded_recyc);
        search_recyc = findViewById(R.id.search_recyc);
        layout_on_off = findViewById(R.id.layout_on_off);
    }

    public void initData(){
        registerReceiver(bleReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            initBleOn();
        }else{
            initBleOff();
        }
    }

    public void bindListener(){
        layout_switch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_switch:                                                                //蓝牙打开关闭
                if (bluetoothAdapter.isEnabled()){
                    if (bluetoothAdapter.disable()){
                        iv_switch.setImageResource(R.mipmap.turn_off);
                    }else {
                        ToashShow("关闭蓝牙失败");
                    }
                }else {
                    if (bluetoothAdapter.enable()){
                        iv_switch.setImageResource(R.mipmap.turn_on);
                    }else {
                        ToashShow("打开蓝牙失败");
                    }
                }
                break;

        }
    }

    /**
     * 初始化蓝牙打开的页面
     */
    public void initBleOn(){
        String my_deveice_name_text =bluetoothAdapter.getName();
        iv_switch.setImageResource(R.mipmap.turn_on);
        my_deveice_name.setText(String.format(getResources().getString(R.string.my_deveice_name),my_deveice_name_text));
        layout_on_off.setVisibility(View.VISIBLE);
        bondedDevices();
        searchDevices();
    }

    /**
     * 初始蓝牙关闭的页面
     */
    public void initBleOff(){
        iv_switch.setImageResource(R.mipmap.turn_off);
        my_deveice_name.setText(getResources().getString(R.string.ble_off));
        layout_on_off.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化配对列表
     */
    public void bondedDevices(){
        bondList.addAll(bluetoothAdapter.getBondedDevices());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        bonded_recyc.setLayoutManager(manager);
        bondAdapter = new BondAdapter(bondList);
        bondAdapter.setOnclickConnectListener(this);
        bonded_recyc.setAdapter(bondAdapter);
    }

    /**
     * 开始搜索设备
     */
    public void searchDevices(){

    }

    /**
     * 点击配对列表进行连接
     * @param position  下标
     * @param device    蓝牙设备
     */
    @Override
    public void onClickConnect(int position, BluetoothDevice device) {

    }

    public void ToashShow(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleReceiver);
    }
}
