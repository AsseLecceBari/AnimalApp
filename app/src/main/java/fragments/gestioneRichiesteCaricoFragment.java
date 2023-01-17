package fragments;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.AnimaleDB;
import DB.CaricoDB;
import adapter.GestioneRichiesteCaricoAdapter;
import class_general.Bluetooh.ServerSocket;
import fragments_mieiAnimali.RichiediCaricoFragment;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.RichiestaCarico;


@RequiresApi(api = Build.VERSION_CODES.S)
public class gestioneRichiesteCaricoFragment extends Fragment {

    private final int Request_code_bt=5;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private AnimaleDB animaleDB;
    private CaricoDB caricoDB;
    private RecyclerView mRecyclerView;
   private ArrayList< Animale> richiesteIncaricoBt = new ArrayList<>();
    private ArrayList<Animale> mDataset = new ArrayList<>();
    private ArrayList<RichiestaCarico> richiesteDataset = new ArrayList<>();
    private ArrayList<Animale> filteredlist = new ArrayList<>();
    private View buttonBluetooh;
    private GestioneRichiesteCaricoAdapter mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);
    FirebaseAuth firebaseAuth;

    private Toolbar main_action_bar;

    private AlertDialog.Builder builder;
    private ServerSocket serverSoket;
   private View view;



    private static final String[] PERMISSIONS_STORAGE = {

            BLUETOOTH_CONNECT
    };

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint({"NewApi", "SuspiciousIndentation"})
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("ciao33", String.valueOf(result.getResultCode()));
                 if (result.getResultCode() == 200) {
                     firebaseAuth= FirebaseAuth.getInstance();

                     Snackbar mySnackbar = Snackbar.make(view,"In attesa di ricevere un Incarico",15000) ;
                     mySnackbar.show();


                     BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                     serverSoket= new ServerSocket(mBluetoothAdapter,mHandler,firebaseAuth.getCurrentUser().getEmail());
                     serverSoket.start();


                    }
                }
            });
    private Handler mHandler;

    public gestioneRichiesteCaricoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_gestione_richieste_carico, container, false);
        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleGestioneRichiesteCarico);
        buttonBluetooh= view.findViewById(R.id.buttonBluetooth);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        animaleDB = new AnimaleDB();
        caricoDB= new CaricoDB();
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(R.string.gestisci_richieste_di_carico);
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataset.clear();
        filteredlist.clear();

        buttonBluetooh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (android.os.Build.VERSION.SDK_INT > 30) {


                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(BLUETOOTH_CONNECT)) {
                            showDialogPermission();


                        } else {

                            getActivity().requestPermissions(

                                    PERMISSIONS_STORAGE,
                                    8
                            );
                        }
                    }else {
                        Intent abilitavisibilità = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        abilitavisibilità.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
                        activityResultLaunch.launch(abilitavisibilità);
                    }
                }
                else {


                    Intent abilitavisibilità = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    abilitavisibilità.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
                    activityResultLaunch.launch(abilitavisibilità);
                }

            }
        });




        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == RicercaDispositiviBluetooth.MessageConstants.MESSAGE_READ) {


                    byte[] mmBuffer = (byte[]) msg.obj;

                    String readMessage = new String(mmBuffer, 0, msg.arg1);
                    //traformo il messaggio ricevuto in json e poi in arraylist e lo inserisco nel db
                    Gson gson = new Gson();
                    richiesteIncaricoBt.add(gson.fromJson(readMessage , Animale.class));
                    RichiestaCarico richiestaCarico=new RichiestaCarico(gson.fromJson(readMessage , Animale.class).getIdAnimale(),auth.getCurrentUser().getEmail(),gson.fromJson(readMessage , Animale.class).getEmailProprietario(),"in sospeso");
                    db.collection("richiestaCarico").document(gson.fromJson(readMessage , Animale.class).getIdAnimale()).set(richiestaCarico);
                    mDataset.add(gson.fromJson(readMessage , Animale.class));
                    mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);
                    //Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                    mRecyclerView.setItemViewCacheSize(mDataset.size());
                    mRecyclerView.setAdapter(mAdapter);




                    if (richiesteIncaricoBt.size() > 1) {

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);

                        builder.setMessage("Hai ricevuto " + richiesteIncaricoBt.size() + " incarichi...")
                                        .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                richiesteIncaricoBt.clear();
                                serverSoket.cancel();
                            }
                        });

                        builder.show();
                    } else {


                        builder = new AlertDialog.Builder(getActivity());

                        builder.setCancelable(false);
                        builder.setMessage("Hai ricevuto " + richiesteIncaricoBt.size()+ " incarico...")
                                        .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                richiesteIncaricoBt.clear();
                                serverSoket.cancel();


                            }
                        });

                        builder.show();
                    }


                }
            }
        };

        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        if (auth.getCurrentUser() != null) {
            CollectionReference animaliReference = db.collection("animali");
            caricoDB.getVetRichiesteCarichi(auth,db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            richiesteDataset.add(document.toObject(RichiestaCarico.class));
                            Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale", document.toObject(RichiestaCarico.class).getIdAnimale());
                            queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Salvare animale in un array con elementi oggetto animale
                                            mDataset.add(document.toObject(Animale.class));
                                        }
                                    }
                                    mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);
                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                    mRecyclerView.setItemViewCacheSize(mDataset.size());
                                    mRecyclerView.setAdapter(mAdapter);

                                }
                            });
                        }
                    }

                }

            });
        }
    }


    public void showDialogPermission()
    {
        new AlertDialog.Builder(getContext())
                .setTitle("Attenzione")
                .setMessage("Per utilizzare questa funzionalità è molto importante accettare i permessi per utilittare il BT!!")
                .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().requestPermissions(
                                PERMISSIONS_STORAGE,
                                Request_code_bt
                        );
                    }
                })
                .setCancelable(false)

                .create().show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);
            MainActivity mainActivity= (MainActivity) getActivity();
            mainActivity.searchFilterListener();
        }
    }


}
