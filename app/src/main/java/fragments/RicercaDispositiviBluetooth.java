package fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import adapter.DispositiviDisponibiliBt;
import class_general.Bluetooh.Bluetooth;
import class_general.Bluetooh.ConnectionManager;
import it.uniba.dib.sms2223_2.R;
import model.Animale;


@RequiresApi(api = Build.VERSION_CODES.S)
public class RicercaDispositiviBluetooth extends DialogFragment {
    private Toolbar main_action_bar;
    ArrayList<BluetoothDevice> mlistDevices= new ArrayList<>();
    DispositiviDisponibiliBt dispositiviDisponibiliBt;
    private RecyclerItemClickListener recyclerItemClickListener;

    // TODO: Rename and change types of parameters
    private ArrayList<Animale> mParam1;
    private static final String ARG_PARAM1 = "listaAnimali";
    private static String Arg_Param2= "manager";
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private ConnectionManager mconnectionManager;
    private View bottone;


   private  Bluetooth bluetooth;




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
    public static RicercaDispositiviBluetooth newInstance(ArrayList<Animale> param1, ConnectionManager connectionManager) {
        RicercaDispositiviBluetooth fragment = new RicercaDispositiviBluetooth();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);

        args.putSerializable(Arg_Param2,  connectionManager);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

       mParam1= (ArrayList<Animale>) getArguments().getSerializable(ARG_PARAM1);
       mconnectionManager= (ConnectionManager) getArguments().getSerializable(Arg_Param2);


        }
    }

    @Override
    public void onResume() {
        super.onResume();




        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(

                    PERMISSIONS_LOCATION,
                    1);
            Log.d("ciao32","ciao9");

        }
        else {




            mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    AlertDialog.Builder builder;

                        byte[] mmBuffer = (byte[]) msg.obj;

                        String readMessage = new String(mmBuffer, 0, msg.arg1);


                        builder = new AlertDialog.Builder(getActivity());


                        builder.setMessage("Vuoi inviare l'incarico a " + readMessage + "?")
                                .setTitle("Invio Incarico")
                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        builder.setCancelable(true);
                                    }
                                }).setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String a = mParam1.toString();
                                        try {
                                            Animale mSampleObject = mParam1.get(0);
                                            String jsonInString = new Gson().toJson(mSampleObject);
                                            JSONObject mJSONObject = new JSONObject(jsonInString);
                                            Log.d("ciao26", String.valueOf(mconnectionManager));
                                            if (mconnectionManager != null) {
                                                Log.d("ciao26", String.valueOf(mconnectionManager));
                                              //  mconnectionManager.write(mJSONObject.toString());
                                            }


                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                    }
                                });


                        builder.show();


                    }




            };


          bluetooth.BtScanner(mRecyclerView, mParam1, mHandler, mconnectionManager);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);

        }
           bluetooth.unregistrerReceiver();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View rootview= inflater.inflate(R.layout.fragment_ricerca_dispositivi_bluetooth, container, false);
        main_action_bar= getActivity().findViewById(R.id.main_action_bar);
        if(main_action_bar!= null)
        {

            main_action_bar.setTitle("Seleziona Dispositivo ");
            if(main_action_bar.getMenu()!=null) {
                main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
                main_action_bar.getMenu().clear();
                main_action_bar.setNavigationIcon(R.drawable.back);
                main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getActivity().onBackPressed();
                    }
                });
            }
        }

        mRecyclerView = rootview.findViewById(R.id.item_dispositiviNonAssociati);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bluetooth= new Bluetooth(getActivity());

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        return rootview;
    }

}