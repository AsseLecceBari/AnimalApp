package fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private RecyclerView mRecyclerViewNonAssociati;
    private RecyclerView mRecyclerViewAssociati;
    private ConnectionManager mconnectionManager;
    private View restartScanner;


   private  Bluetooth bluetooth;




    public interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }
    private static final String[] PERMISSIONS_STORAGE = {

            BLUETOOTH_CONNECT, BLUETOOTH_SCAN
    };




    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,


    };


    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NewApi")
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("ciao", String.valueOf(result.getResultCode()));

                    if (result.getResultCode() == 0) {
                        Toast.makeText(getContext(),"Abilita BT per ricercare i dispositivi",Toast.LENGTH_SHORT).show();

                    } else if (result.getResultCode() == -1) {

                      bluetooth.BtScanner(mRecyclerViewNonAssociati,mRecyclerViewAssociati,mconnectionManager,getContext());


                    }
                }
            });






    public RicercaDispositiviBluetooth() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RicercaDispositiviBluetooth newInstance(ArrayList<Animale> param1) {
        RicercaDispositiviBluetooth fragment = new RicercaDispositiviBluetooth();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

       mParam1= (ArrayList<Animale>) getArguments().getSerializable(ARG_PARAM1);



        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);



    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onResume() {
        super.onResume();











        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                showDialogPermissionBtPosition();


            } else {

                getActivity().requestPermissions(

                        PERMISSIONS_LOCATION,
                        7
                );
            }

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
                                .setCancelable(false)
                                .setTitle("Invio Incarico")
                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            mconnectionManager.cancel();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Gson gson = new Gson();

                                        for(int a=0; a<mParam1.size();a++) {
                                            String jsonString = gson.toJson(mParam1.get(a));
                                            mconnectionManager.write(jsonString);
                                           try {
                                                mconnectionManager.cancel();
                                                Log.d("ciao35","  eliminato" );
                                            } catch (IOException e) {
                                                Log.d("ciao35"," non eliminato" );
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                });


                        builder.show();


                    }




            };
            if(mconnectionManager== null)
            {

                mconnectionManager= new ConnectionManager(mHandler);
                bluetooth.BtScanner(mRecyclerViewNonAssociati,mRecyclerViewAssociati, mconnectionManager,getContext());

            }
            setRestartScanner();






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

        mRecyclerViewNonAssociati = rootview.findViewById(R.id.item_dispositiviNonAssociati);
        mRecyclerViewNonAssociati.setLayoutManager(new LinearLayoutManager(getContext()));
        restartScanner= rootview.findViewById(R.id.restartScanner);



        mRecyclerViewAssociati= rootview.findViewById(R.id.item_dispositiviAssociati);
        mRecyclerViewAssociati.setLayoutManager(new LinearLayoutManager(getContext()));

        bluetooth= new Bluetooth(getActivity(),activityResultLaunch);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        return rootview;
    }


    public void setRestartScanner()
    {
        restartScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewAssociati.setAdapter(null);
                mRecyclerViewNonAssociati.setAdapter(null);



                Snackbar mySnackbar = Snackbar.make(view,"Ricerca Dispositivi",1000) ;
                mySnackbar.show();

               // bluetooth.AbilitazioneBT(mParam1);

                bluetooth.BtScanner(mRecyclerViewNonAssociati,mRecyclerViewAssociati,mconnectionManager,getContext());
            }
        });
    }


    public void showDialogPermissionBtPosition()
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Attenzione")
                .setMessage("Per utilizzare questa funzionalità è molto importante accettare i permessi, inoltre ricordiamo che questi dati non verranno salvati grazie !!")
                .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().requestPermissions(
                                PERMISSIONS_LOCATION,
                                5
                        );
                    }
                })
                .setCancelable(false)

                .create().show();
    }

}