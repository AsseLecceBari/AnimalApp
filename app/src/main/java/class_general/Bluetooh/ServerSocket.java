package class_general.Bluetooh;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerSocket implements Runnable {


    private final BluetoothServerSocket mServerSoket;
    private BluetoothAdapter mBtAdapter;
    private Handler mHandler;

    @SuppressLint("MissingPermission")
    public ServerSocket(BluetoothAdapter adapter, Handler handler)  {
        mBtAdapter= adapter;
        mHandler= handler;
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
                ConnectionManager gestioneConnessione= new ConnectionManager(socket,mHandler);
                gestioneConnessione.write("cio");
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
