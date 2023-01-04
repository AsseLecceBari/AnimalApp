package class_general;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import fragments.RicercaDispositiviBluetooth;
import it.uniba.dib.sms2223_2.MainActivity;
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


        Log.d("ciao990","ciao");

    }

    private static final String[] PERMISSIONS_STORAGE = {

            BLUETOOTH_CONNECT, BLUETOOTH_SCAN
    };






    public void AbilitazioneBT() {
     if (!mBtAdapter.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         //   if (android.os.Build.VERSION.SDK_INT >= 30)
            if (ContextCompat.checkSelfPermission(mactivity.getApplicationContext(), BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                mactivity.requestPermissions(

                        PERMISSIONS_STORAGE,
                        1
                );
            } else {

                mactivityResultLaunch.launch(enableBt);
                Log.d("ciao23", String.valueOf(REQUEST_ENABLE_BT));
            }

            //con api minori di 30 non chiede il permesso BLUETOOTH_CONNECT ma richiede BLUETOOTH, che non viene chiesto a runTime
            if (ContextCompat.checkSelfPermission(mactivity.getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {

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





}
