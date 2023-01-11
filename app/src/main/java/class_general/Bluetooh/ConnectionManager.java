package class_general.Bluetooh;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;

public class ConnectionManager implements Serializable {
    BluetoothSocket msocket;
    Connection connectedThread;
    Handler mHandler;
    public ConnectionManager( Handler handler)
    {
        mHandler= handler;

    }

    public void setSocket(BluetoothSocket socket)

    {
        connectedThread = new Connection(socket, mHandler);
        connectedThread.start();
    }


    public void write(String stringa)
    {
        if(connectedThread!= null)
        {
            connectedThread.write(stringa.getBytes());
        }
    }

    public void cancel() throws IOException {
        connectedThread.cancel();
    }
}
