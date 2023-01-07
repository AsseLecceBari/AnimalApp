package fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import adapter.DispositiviDisponibiliBt;
import class_general.Bluetooh.Bluetooth;
import it.uniba.dib.sms2223_2.R;


@RequiresApi(api = Build.VERSION_CODES.S)
public class RicercaDispositiviBluetooth extends DialogFragment {
    private Toolbar main_action_bar;
    ArrayList<BluetoothDevice> mlistDevices= new ArrayList<>();
    DispositiviDisponibiliBt dispositiviDisponibiliBt;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private View bottone;


    public interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }




    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };






    public RicercaDispositiviBluetooth() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RicercaDispositiviBluetooth newInstance(String param1, String param2) {
        RicercaDispositiviBluetooth fragment = new RicercaDispositiviBluetooth();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();




        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(

                    PERMISSIONS_LOCATION,
                    1);

        }
        else {
            Log.d("ciao3", "prova");


            mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);

                    if (msg.what == MessageConstants.MESSAGE_READ) {
                        byte[] mmBuffer = (byte[]) msg.obj;

                        String readMessage = new String(mmBuffer, 0, msg.arg1);

                        Log.d("ciao33", readMessage);
                    }


                }
            };


            Bluetooth bluetooth = new Bluetooth(getActivity());
            bluetooth.BtScanner(mlistDevices, mHandler, dispositiviDisponibiliBt, mRecyclerView);


        }

    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View rootview= inflater.inflate(R.layout.fragment_ricerca_dispositivi_bluetooth, container, false);
        main_action_bar= getActivity().findViewById(R.id.main_action_bar);
        if(main_action_bar!= null)
        {
            if(main_action_bar.getMenu()!=null) {
                main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            }
            main_action_bar.setTitle("Seleziona Dispositivo ");
            main_action_bar.setNavigationIcon(R.drawable.back);
        }

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.item_dispositiviNonAssociati);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        return rootview;
    }
}