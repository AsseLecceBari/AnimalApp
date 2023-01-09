package fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.RichiestaCarico;


public class gestioneRichiesteCaricoFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private AnimaleDB animaleDB;
    private CaricoDB caricoDB;
    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset = new ArrayList<>();
    private ArrayList<RichiestaCarico> richiesteDataset = new ArrayList<>();
    private ArrayList<Animale> filteredlist = new ArrayList<>();
    private View buttonBluetooh;
    private GestioneRichiesteCaricoAdapter mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);
    FirebaseAuth firebaseAuth;

    private Toolbar main_action_bar;

    private AlertDialog.Builder builder;

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint({"NewApi", "SuspiciousIndentation"})
                @Override
                public void onActivityResult(ActivityResult result) {
                 if (result.getResultCode() == 2000) {
                     firebaseAuth= FirebaseAuth.getInstance();

                     builder = new AlertDialog.Builder(getActivity());



                        builder.setMessage("In attesa di ricevere un incarico...")
                                .setTitle("Ricezione Incarico")
                                .setPositiveButton("Annulla", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        builder.setCancelable(true);
                                    }
                                });






                        builder.show();
                     BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                     ServerSocket serverSoket= new ServerSocket(mBluetoothAdapter,mHandler,firebaseAuth.getCurrentUser().getEmail());
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

        View view = inflater.inflate(R.layout.fragment_gestione_richieste_carico, container, false);
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



                    Intent abilitavisibilità = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    abilitavisibilità.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 2000);

                    activityResultLaunch.launch(abilitavisibilità);

            }
        });




        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == RicercaDispositiviBluetooth.MessageConstants.MESSAGE_READ) {


                    byte[] mmBuffer = (byte[]) msg.obj;

                    String readMessage = new String(mmBuffer, 0, msg.arg1);
                    String[] arrayList;

                    arrayList = readMessage.split(" ");



                    if (arrayList.length > 1) {
                        builder.setMessage("Stai per ricevere " + arrayList.length + " incarichi...");

                        builder.show();
                    } else {
                        builder.setMessage("Stai per ricevere " + arrayList.length + " incarico...")
                        ;

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);

        }
    }
}
