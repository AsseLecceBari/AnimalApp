package class_general.Bluetooh;

import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import adapter.DispositiviDisponibiliBt;


public  class SingBroadcastReceiver extends BroadcastReceiver {

    BluetoothAdapter mBtAdapter;
    Handler mhandler;
    ArrayList<BluetoothDevice> mlistdevice = new ArrayList<>();
    DispositiviDisponibiliBt  dispositiviDisponibiliBt= new DispositiviDisponibiliBt();
    RecyclerView mrecycleView;


    public SingBroadcastReceiver(BluetoothAdapter adapter, ArrayList<BluetoothDevice> listdevice, Handler handler, DispositiviDisponibiliBt dispositiviDisponibiliBtad, RecyclerView mRecyclerView) {
        mBtAdapter= adapter;

        mhandler= handler;

        mrecycleView= mRecyclerView;


    }


    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction(); //may need to chain this to a recognizing function
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Log.d("ciao3", device.getName());
            mlistdevice.add(device);



      dispositiviDisponibiliBt.aggiornalista(device);
            mrecycleView.setAdapter(dispositiviDisponibiliBt);







        }
    }
}