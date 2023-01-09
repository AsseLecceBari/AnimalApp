package class_general.Bluetooh;

import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import adapter.DispositiviDisponibiliBt;
import model.Animale;


public  class SingBroadcastReceiver extends BroadcastReceiver {

    BluetoothAdapter mBtAdapter;
    Handler mhandler;
    ArrayList<Animale > mlistAnimali;
   ArrayList< BluetoothDevice> devices = new ArrayList<>();

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
            int cont =0;
            for(int i=0; i<devices.size(); i++) {
                if(Objects.equals(devices.get(i).getName(), device.getName()))
                {
                    cont++;
                }
            }
            if(cont==0)
            {
                devices.add(device);
            }


      dispositiviDisponibiliBt.aggiornalista(devices);
            mrecycleView.setAdapter(dispositiviDisponibiliBt);
            if(Objects.equals(device.getName(), "Galaxy A7 (2018)"))
            {
                ClientSocket clientSocket= new ClientSocket(device,mBtAdapter, mhandler,mlistAnimali);
                clientSocket.run();
            }



            mrecycleView.addOnItemTouchListener(new class_general.RecyclerItemClickListener(context, mrecycleView , new class_general.RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d("ciao32",devices.get(position).getName());

                    ClientSocket clientSocket= new ClientSocket(devices.get(position),mBtAdapter, mhandler,mlistAnimali);
                    clientSocket.run();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));


        }
    }

}