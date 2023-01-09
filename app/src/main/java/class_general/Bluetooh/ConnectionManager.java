package class_general.Bluetooh;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectionManager {
    BluetoothSocket msocket;
    Connection connectedThread;
    Handler mHandler;
    public ConnectionManager(BluetoothSocket socket, Handler handler)
    {
        msocket=socket;
        mHandler= handler;

        connectedThread = new Connection(socket, handler);
       //
        connectedThread.start();

    }



    public void write(String stringa)
    {
        if(connectedThread!= null)
        {
            connectedThread.write(stringa.getBytes());
        }
    }
}
