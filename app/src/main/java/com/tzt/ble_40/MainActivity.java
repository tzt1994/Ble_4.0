package com.tzt.ble_40;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,BondAdapter.OnClickConnectListener,SearchAdapter.OnClickConnectListener{
    private LinearLayout layout_switch;                                                             //开关布局
    private ImageView iv_switch;                                                                    //开关图片
    private TextView my_deveice_name;                                                               //我的设备名称
    private RecyclerView bonded_recyc;                                                              //绑定列表
    private RecyclerView search_recyc;                                                              //搜索列表
    private LinearLayout layout_on_off;                                                             //蓝牙状态改变时的布局

    private ArrayList<BluetoothDevice> bondList = new ArrayList<>();
    private BondAdapter bondAdapter;
    private ArrayList<BluetoothDevice> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt mGatt;
    private String my_deveice_name_text;
    private boolean mBondSearch = false;
    private int mPosition;

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        if (state == BluetoothAdapter.STATE_ON){                                    //蓝牙打开
                            initBleOn(my_deveice_name_text);
                        }else if (state == BluetoothAdapter.STATE_OFF) {                            //蓝牙关闭
                            initBleOff();
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
        my_deveice_name_text =bluetoothAdapter.getName();

        //配对列表初始化
        LinearLayoutManager bondManager = new LinearLayoutManager(this);
        bondManager.setOrientation(LinearLayoutManager.VERTICAL);
        bonded_recyc.setLayoutManager(bondManager);
        if (bondAdapter == null){
            bondAdapter = new BondAdapter(bondList);
        }
        bonded_recyc.setAdapter(bondAdapter);

        //搜索列表初始化
        LinearLayoutManager searchManager = new LinearLayoutManager(this);
        searchManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_recyc.setLayoutManager(searchManager);
        if (searchAdapter == null){
            searchAdapter = new SearchAdapter(searchList);
        }
        search_recyc.setAdapter(searchAdapter);


        if (bluetoothAdapter.isEnabled()){
            initBleOn(my_deveice_name_text);
        }else{
            initBleOff();
        }
    }

    public void bindListener(){
        layout_switch.setOnClickListener(this);
        bondAdapter.setOnclickConnectListener(this);
        searchAdapter.setOnClickConnectListener(this);
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
    public void initBleOn(String my_deveice_name_text){
        iv_switch.setImageResource(R.mipmap.turn_on);
        my_deveice_name.setText(String.format(getResources().getString(R.string.my_deveice_name),my_deveice_name_text));
        layout_on_off.setVisibility(View.VISIBLE);
        bondedDevices();
        startSearchDevices();
    }

    /**
     * 初始蓝牙关闭的页面
     */
    public void initBleOff(){
        stopSearchDevices();
        iv_switch.setImageResource(R.mipmap.turn_off);
        my_deveice_name.setText(getResources().getString(R.string.ble_off));
        layout_on_off.setVisibility(View.INVISIBLE);
        bondList.clear();
        searchList.clear();
    }

    /**
     * 初始化配对列表
     */
    public void bondedDevices(){
        bondList.addAll(bluetoothAdapter.getBondedDevices());
        bondAdapter.notifyDataSetChanged();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            searchList.add(bluetoothDevice);
            searchAdapter.notifyDataSetChanged();
        }
    } ;

    private Handler searchHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopSearchDevices();
        }
    };

    /**
     * 开始搜索设备
     */
    public void startSearchDevices(){
        bluetoothAdapter.startLeScan(mLeScanCallback);
        searchHandler.postDelayed(runnable, 15000);
    }

    /**
     * 停止搜索设备
     */
    public void stopSearchDevices(){
        searchHandler.removeCallbacks(runnable);
        bluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    /**
     * 点击配对列表进行连接
     * @param position  下标
     * @param device    蓝牙设备
     */
    @Override
    public void onBondClickConnect(BluetoothDevice device, int position) {
        ToashShow("activity点击配对设备");
        mPosition = position;
        mBondSearch = true;
        gattConnect(device);
    }

    /**
     * 点击搜索列表进行连接
     * @param position  下标
     * @param device    蓝牙设备
     */
    @Override
    public void onSearchClickConnect(BluetoothDevice device, int position) {
        mPosition = position;
        mBondSearch = false;
        gattConnect(device);
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {         //设备连接状态发生变化
            ToashShow("进来了");
            if (mBondSearch){
                bondAdapter.notifyDataSetChanged();
            }else {
                searchAdapter.notifyDataSetChanged();
            }

            if (status == BluetoothGatt.GATT_SUCCESS){
                ToashShow("gatt success");
                if (newState == BluetoothProfile.STATE_CONNECTED){                                        //已连接

                }else if (newState == BluetoothProfile.STATE_DISCONNECTED){                               //断开连接

                }
            }else {
                ToashShow("133--" + status);
            }
        }
    };

    public void gattConnect(BluetoothDevice device){
        stopSearchDevices();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ToashShow("大于M");
            mGatt = device.connectGatt(this, false, gattCallback,BluetoothDevice.TRANSPORT_LE);
        }else {
            mGatt = device.connectGatt(this,false,gattCallback);
        }
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
