package class_general.Bluetooh;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import model.Animale;

public class ClientSocket extends Thread {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    private BluetoothAdapter mBtAdapter;

    private ConnectionManager mconnectionManager;


    @SuppressLint("MissingPermission")
    public ClientSocket(BluetoothDevice device, BluetoothAdapter adapter,  ConnectionManager connectionManager) {

        mBtAdapter= adapter;
        BluetoothSocket tmp = null;
        this.mDevice = device;


        mconnectionManager=connectionManager;




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
        mconnectionManager.setSocket(mSocket);





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
