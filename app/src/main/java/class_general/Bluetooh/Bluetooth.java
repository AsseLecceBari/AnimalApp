package class_general.Bluetooh;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.DispositiviDisponibiliBt;
import fragments.RicercaDispositiviBluetooth;
import it.uniba.dib.sms2223_2.R;


public class Bluetooth  {

    private BluetoothAdapter mBtAdapter ;
    private final FragmentActivity mactivity;
    private static int REQUEST_ENABLE_BT=2;
    ActivityResultLauncher<Intent> mactivityResultLaunch;

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







    @SuppressLint("NewApi")
    public void AbilitazioneBT() {
     if (!mBtAdapter.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           if (android.os.Build.VERSION.SDK_INT > 30) {
               if (ContextCompat.checkSelfPermission(mactivity.getApplicationContext(), BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                   mactivity.requestPermissions(

                           PERMISSIONS_STORAGE,
                           1
                   );
               } else {

                   mactivityResultLaunch.launch(enableBt);
                   Log.d("ciao23", String.valueOf(REQUEST_ENABLE_BT));
               }
           }else {

               //con api minori di 30 non chiede il permesso BLUETOOTH_CONNECT ma richiede BLUETOOTH, che non viene chiesto a runTime
               mactivityResultLaunch.launch(enableBt);
           }


        }
     else  mactivity.getSupportFragmentManager().beginTransaction()
             .replace(R.id.fragmentContainerView,  new RicercaDispositiviBluetooth()).addToBackStack(null).commit();

    }


    public boolean VerificaBtSupportato()
    {
        if (mBtAdapter == null) {
            Log.d("ciao ", "non Supportato");
           return false;
        }

        return true;
    }


    public void setRequestEnableBt(int i) {
        REQUEST_ENABLE_BT = i;
    }




    @SuppressLint("MissingPermission")
    public void BtScanner(ArrayList<BluetoothDevice> listdevice, Handler mhandler, DispositiviDisponibiliBt dispositiviDisponibiliBtad, RecyclerView mRecyclerView) {




            SingBroadcastReceiver mReceiver;

            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
            }
            mBtAdapter.startDiscovery();


            mReceiver = new SingBroadcastReceiver(mBtAdapter, listdevice, mhandler, dispositiviDisponibiliBtad, mRecyclerView);
            IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mactivity.registerReceiver(mReceiver, ifilter);



    }








}
