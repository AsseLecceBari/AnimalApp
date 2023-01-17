package class_general.Bluetooh;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import adapter.DispositiviDisponibiliBt;
import fragments.RicercaDispositiviBluetooth;
import it.uniba.dib.sms2223_2.R;
import model.Animale;


public class Bluetooth  {

    private BluetoothAdapter mBtAdapter ;
    private final FragmentActivity mactivity;
    private static int REQUEST_ENABLE_BT=2;
   private ActivityResultLauncher<Intent> mactivityResultLaunch;
    private ArrayList<Animale> listAnimali;
    private  SingBroadcastReceiver mReceiver;


    public Bluetooth(FragmentActivity activity,   ActivityResultLauncher<Intent> activityResultLaunch )
    {
        mactivity=  activity;
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();
        mactivityResultLaunch=activityResultLaunch;




    }
    public Bluetooth(FragmentActivity activity )
    {
        mactivity=  activity;
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();

    }

    private static final String[] PERMISSIONS_STORAGE = {

            BLUETOOTH_CONNECT, BLUETOOTH_SCAN
    };

    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };







    @SuppressLint({"NewApi", "SuspiciousIndentation"})
    public void AbilitazioneBT(ArrayList<Animale> animaliPerCarico) {

     if (!mBtAdapter.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           if (android.os.Build.VERSION.SDK_INT > 30) {
               if (ContextCompat.checkSelfPermission(mactivity.getApplicationContext(), BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                   mactivity.requestPermissions(PERMISSIONS_STORAGE,5);
               } else {
                   listAnimali= animaliPerCarico;

                   mactivityResultLaunch.launch(enableBt);
                   Log.d("ciao23", String.valueOf(REQUEST_ENABLE_BT));
               }
           }else {
               listAnimali= animaliPerCarico;

               //con api minori di 30 non chiede il permesso BLUETOOTH_CONNECT ma richiede BLUETOOTH, che non viene chiesto a runTime
               mactivityResultLaunch.launch(enableBt);
           }
        }
     else
         listAnimali= animaliPerCarico;



        mactivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView, RicercaDispositiviBluetooth.newInstance(listAnimali)).commit();

    }


    public boolean VerificaBtSupportato()
    {
        if (mBtAdapter == null) {

           return false;
        }

        return true;
    }


    public void setRequestEnableBt(int i) {
        REQUEST_ENABLE_BT = i;
    }




    @SuppressLint("MissingPermission")
    public void BtScanner(RecyclerView mRecyclerView, RecyclerView mRecyclerViewAssociati, ConnectionManager mconnectionManager, Context context) {

           ricercaDispositiviAssociati(mRecyclerViewAssociati,mconnectionManager,context);



            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
            }
            mBtAdapter.startDiscovery();


            mReceiver = new SingBroadcastReceiver(mBtAdapter, mRecyclerView, mconnectionManager);
            IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mactivity.registerReceiver(mReceiver, ifilter);





    }

    public void unregistrerReceiver()
    {
mactivity.unregisterReceiver(mReceiver);
    }

    @SuppressLint("MissingPermission")
    public void ricercaDispositiviAssociati(RecyclerView mrecycleview, ConnectionManager mconnectionManager, Context context)
    {
 Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {


            DispositiviDisponibiliBt dispositiviDisponibiliBt= new DispositiviDisponibiliBt();
            // There are paired devices. Get the name and address of each paired device.
            int cont = 0;

            ArrayList<BluetoothDevice> devices = new ArrayList<>();


            for ( BluetoothDevice device : pairedDevices) {
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
                mrecycleview.setAdapter(dispositiviDisponibiliBt);
                mrecycleview.addOnItemTouchListener(new class_general.RecyclerItemClickListener(context, mrecycleview , new class_general.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {



                        ClientSocket clientSocket= new ClientSocket(devices.get(position),mBtAdapter, mconnectionManager);
                        clientSocket.start();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));




            }
        }
    }








}
