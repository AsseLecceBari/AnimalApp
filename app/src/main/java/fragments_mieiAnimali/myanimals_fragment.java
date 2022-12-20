package fragments_mieiAnimali;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

import DB.CaricoDB;
import DB.UtentiDB;
import adapter.AnimalAdapter;
import DB.AnimaleDB;
import fragments.RecyclerItemClickListener;
import fragments.nonSeiRegistrato_fragment;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Carico;

public class myanimals_fragment extends Fragment {

    public static final String RUOLOVETERINARIO = "veterinario";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FloatingActionButton addAnimale, addIncarico;
    private MaterialCheckBox mostraSoloIncarico;
    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset= new ArrayList<>();
    private ArrayList<Animale> filteredlist=new ArrayList<>();
    private AnimalAdapter mAdapter=new AnimalAdapter(mDataset);
    private AnimaleDB animaleDB;
    private CaricoDB caricoDB;
    private UtentiDB utentiDB;
    private MainActivity mainActivity;
    private Toolbar main_action_bar;
    private String ruolo="";
    private int countMyAnimals;
    private ArrayList<Carico> caricoDataset=new ArrayList<>();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Per poter utilizzare questa applicazione con tutte le sue funzionalità, è consigliato accettare i permessi");
        alertDialogBuilder.setPositiveButton("Ho capito",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

        alertDialogBuilder.setNegativeButton("Magari più tardi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                requestPermissionLauncher =
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                            if (isGranted) {
                                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).
                                        replace(R.id.fragmentContainerView, new aggiungiCarico()).commit();
                            } else {
                                //Dire all'utente di andare nelle impostazioni e dare i permessi dello storage all'app

                            }
                        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ruolo="";
        mDataset.clear();
        caricoDataset.clear();
        filteredlist.clear();
        countMyAnimals=0;
        View rootView = inflater.inflate(R.layout.fragment_myanimals_fragment, container, false);
        mostraSoloIncarico = rootView.findViewById(R.id.mostraInCarico);

        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleMyAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        animaleDB = new AnimaleDB();
        caricoDB= new CaricoDB();
        utentiDB= new UtentiDB();



        // controlli per la checkbox in carico


        addAnimale=rootView.findViewById(R.id.aggiungiAnimaliBtn);
        addIncarico=rootView.findViewById(R.id.aggiungiAnimaliInCaricoBtn);



        //se la check box è premuta il bottone cambia

        if(!mostraSoloIncarico.isChecked()){
            addAnimale.setVisibility(View.VISIBLE);
            addIncarico.setVisibility(View.GONE);
        }else{
            addAnimale.setVisibility(View.GONE);
            addIncarico.setVisibility(View.VISIBLE);

        }
        mostraSoloIncarico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //se la check box è premuta il bottone cambia

                if(!mostraSoloIncarico.isChecked()){
                    addAnimale.setVisibility(View.VISIBLE);
                    addIncarico.setVisibility(View.GONE);
                    if(caricoDataset.size()>0){
                        filterMieiAnimali(caricoDataset);
                    }


                }else{
                    addAnimale.setVisibility(View.GONE);
                    addIncarico.setVisibility(View.VISIBLE);
                    filterCarico(caricoDataset);
                }

            }
        });

        addIncarico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // passo al fragment per leggere il qr code dell'animale
                // e inserire la data di inizio carico
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {

                    //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiCarico()).commit();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showAlertDialog();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });


        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        if(auth.getCurrentUser()!=null) {
            animaleDB.getMieiAnimali(auth, db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Salvare animale in un array con elementi oggetto animale
                            mDataset.add(document.toObject(Animale.class));
                            countMyAnimals++;}}
                    //Se siamo loggati con il veterinario aggiungiamo nel dataset anche gli animali in carico
                        utentiDB.getUtenti(auth,db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        if((document.get("ruolo").toString().equals("veterinario"))){
                                            // Nascondo la checkbox che mi mostra gli in carico
                                            mostraSoloIncarico.setVisibility(View.VISIBLE);
                                            ruolo=RUOLOVETERINARIO;
                                            break;
                                        }
                                    }
                                }
                                Log.e("DOVESONO","GETRUOLO");
                                if(ruolo.equals(RUOLOVETERINARIO)){
                                    CollectionReference animaliReference = db.collection("animali");
                                    caricoDB.getVetCarichi(auth,db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Salvare animale in un array con elementi oggetto animale
                                                caricoDataset.add(document.toObject(Carico.class));
                                                Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale",document.toObject(Carico.class).getIdAnimale());
                                                queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                //Salvare animale in un array con elementi oggetto animale
                                                                mDataset.add(document.toObject(Animale.class));}}}});}
                                            //Passo i dati presi dal database all'adapter
                                            mAdapter = new AnimalAdapter(mDataset);
                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                            mRecyclerView.setAdapter(mAdapter);
                                            //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                                        }});
                                }
                                else{
                                    //Passo i dati presi dal database all'adapter
                                    mAdapter = new AnimalAdapter(mDataset);
                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                    mRecyclerView.setAdapter(mAdapter);
                                    //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                                }
                            }
                        });


                }
            });
        }
        addAnimale=rootView.findViewById(R.id.aggiungiAnimaliBtn);
        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
              getChildFragmentManager().beginTransaction().replace(R.id.myAnimalsFragment, new nonSeiRegistrato_fragment()).commit();
        }
        addAnimale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearchView();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiAnimaleFragment()).commit();
            }
        });


        //Inizializzo l'ascoltatore al click dell'item
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Animale a;
                        Intent i = new Intent(getActivity().getApplicationContext(), ProfiloAnimale.class);
                        Log.e("filtered",filteredlist.size()+"");
                        if(filteredlist.size()==0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"

                            a = mDataset.get(position);
                            closeSearchView();
                            mDataset.clear();
                            filteredlist.clear();
                        }else{

                            a = filteredlist.get(position);
                           mDataset.clear();
                           filteredlist.clear();
                            closeSearchView();
                        }
                        //Inserisco l'oggetto nel bundle
                        i.putExtra("animale", a);
                        startActivity(i);
                    }


                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mostraSoloIncarico.setChecked(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    public void filterMieiAnimali(ArrayList<Carico> caricoDataset) {

        filteredlist=new ArrayList<>();
        for(int i=0;i<countMyAnimals;i++){
            filteredlist.add(mDataset.get(i));
        }
            mAdapter.filterList(filteredlist);
    }

    public void filterCarico(ArrayList<Carico> caricoDataset) {

        filteredlist=new ArrayList<>();
            for (Carico animal : caricoDataset) {
                for (Animale item : mDataset) {
                    if (item.getIdAnimale().toLowerCase().contains(animal.getIdAnimale().toLowerCase())) {

                        filteredlist.add(item);
                    }
                }
            }
        if (filteredlist.isEmpty()) {
            mAdapter.filterList(filteredlist);
        } else {
            mAdapter.filterList(filteredlist);
        }
    }


    public void filter(String text) {
        Log.e("text",text);
        filteredlist=new ArrayList<>();
        // running a for loop to compare elements.
        for (Animale item : mDataset) {
            if (item.getNome().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            mAdapter.filterList(filteredlist);
        } else {
            mAdapter.filterList(filteredlist);
        }
    }
    private void closeSearchView() {
        mainActivity= (MainActivity) getActivity();
        main_action_bar= mainActivity.getMain_action_bar();
        main_action_bar.collapseActionView();
    }



}

