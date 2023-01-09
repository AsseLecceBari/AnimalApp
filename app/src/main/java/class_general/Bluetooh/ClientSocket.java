package class_general.Bluetooh;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import model.Animale;

public class ClientSocket extends Thread {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private final ArrayList<Animale> mlistAnimali;
    private BluetoothAdapter mBtAdapter;
    private Handler mhandler;


    @SuppressLint("MissingPermission")
    public ClientSocket(BluetoothDevice device, BluetoothAdapter adapter, Handler handler, ArrayList<Animale> listAnimali) {

        mBtAdapter= adapter;
        BluetoothSocket tmp = null;
        this.mDevice = device;
        mhandler= handler;
        mlistAnimali = listAnimali;




        try {
            UUID u= new UUID(76476475,543245312);

            tmp = device.createRfcommSocketToServiceRecord(u);

        }catch (IOException ignored)
        {

        }

        mSocket=tmp;
    }



    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        mBtAdapter.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {

            try {
                mSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
Log.d("ciao33","sono connesso c");

        ConnectionManager gestioneConnessione = new ConnectionManager(mSocket, mhandler);
        ArrayList<JSONObject> arrayList = new ArrayList<>();

for(int i =0; i<mlistAnimali.size(); i++){
    Animale animale = mlistAnimali.get(i);
        String jsonInString = new Gson().toJson(animale);
        JSONObject mJSONObject = null;
        try {
            mJSONObject = new JSONObject(jsonInString);
            arrayList.add(mJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


            gestioneConnessione.write(arrayList.toString());








    }

    public void cancel()
    {
        try{
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
