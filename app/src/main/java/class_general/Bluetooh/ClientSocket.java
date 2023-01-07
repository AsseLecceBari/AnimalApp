package com.example.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import class_general.Bluetooh.ConnectionManager;

public class ClientSocket implements Runnable {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBtAdapter;
    private Handler mhandler;


    @SuppressLint("MissingPermission")
    public ClientSocket(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {

        mBtAdapter= adapter;
        BluetoothSocket tmp = null;
        this.mDevice = device;
        mhandler= handler;


        try {
            UUID u= new UUID(76476475,543245312);

            tmp = device.createRfcommSocketToServiceRecord(u);

        }catch (IOException ignored)
        {

        }

        mSocket=tmp;
    }



    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        mBtAdapter.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }


        ConnectionManager gestioneConnessione = new ConnectionManager(mSocket, mhandler);
        gestioneConnessione.write("ciccio");

    }

    public void cancel()
    {
        try{
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
