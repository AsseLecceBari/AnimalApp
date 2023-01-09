package class_general.Bluetooh;

import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.DispositiviDisponibiliBt;
import model.Animale;


public  class SingBroadcastReceiver extends BroadcastReceiver {

    BluetoothAdapter mBtAdapter;
    Handler mhandler;
    ArrayList<Animale > mlistAnimali;

    DispositiviDisponibiliBt  dispositiviDisponibiliBt= new DispositiviDisponibiliBt();
    RecyclerView mrecycleView;




    public  SingBroadcastReceiver(BluetoothAdapter adapter, RecyclerView mRecyclerView, ArrayList<Animale> listAnimali, Handler Handler) {
        mBtAdapter= adapter;
        mlistAnimali= listAnimali;
        mhandler=Handler;




        mrecycleView= mRecyclerView;



    }


    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction(); //may need to chain this to a recognizing function
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

      dispositiviDisponibiliBt.aggiornalista(device);
            mrecycleView.setAdapter(dispositiviDisponibiliBt);

            mrecycleView.addOnItemTouchListener(new class_general.RecyclerItemClickListener(context, mrecycleView , new class_general.RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    ClientSocket clientSocket= new ClientSocket(device,mBtAdapter, mhandler,mlistAnimali);
                    clientSocket.start();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));


        }
    }

}