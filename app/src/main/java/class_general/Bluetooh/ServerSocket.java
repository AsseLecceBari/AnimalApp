package class_general.Bluetooh;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerSocket extends Thread {


    private final BluetoothServerSocket mServerSoket;
    private BluetoothAdapter mBtAdapter;
    private Handler mHandler;
    private String emailVeterinario;

    @SuppressLint("MissingPermission")
    public ServerSocket(BluetoothAdapter adapter, Handler handler, String email)  {
        mBtAdapter= adapter;
        mHandler= handler;
        emailVeterinario= email;
        BluetoothServerSocket tmp = null;

        try {
            UUID u= new UUID(76476475,543245312);

            tmp = mBtAdapter.listenUsingRfcommWithServiceRecord("domenico", u);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mServerSoket= tmp;
    }


    @Override
    public void run() {
        BluetoothSocket socket = null;
        Log.d("ciao1","sono in ascolto");
        while (true){
            try{

                socket=mServerSoket.accept();

            } catch (IOException e) {

                e.printStackTrace();
                break;
            }

            if(socket!= null)
            {
                Log.d("ciao33","sono connesso s");
               ConnectionManager connectionManager= new ConnectionManager(socket,mHandler);
              connectionManager.write(emailVeterinario);


                try {
                    mServerSoket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void cancel()
    {
        try {
            mServerSoket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
