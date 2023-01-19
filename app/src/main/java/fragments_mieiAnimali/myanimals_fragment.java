package fragments_mieiAnimali;

import static it.uniba.dib.sms2223_2.R.string.seleziona_almeno_un_animale;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.AnimaleDB;
import DB.CaricoDB;
import DB.UtentiDB;
import adapter.AnimalAdapter;
import class_general.Bluetooh.Bluetooth;
import class_general.Bluetooh.ConnectionManager;
import fragments.RecyclerItemClickListener;
import fragments.RicercaDispositiviBluetooth;
import fragments.gestioneRichiesteCaricoFragment;
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
    private FloatingActionButton addAnimale;

    public FloatingActionButton getAddIncarico() {
        return addIncarico;
    }
    private Button richiediCarico;
    private FloatingActionButton addIncarico;
    private MaterialCheckBox mostraSoloIncarico;

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset= new ArrayList<>();
    private ArrayList<Animale> filteredlist=new ArrayList<>();

    public AnimalAdapter getmAdapter() {
        return mAdapter;
    }

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
    private boolean flagCheckBox;
    private nonSeiRegistrato_fragment nonSeiRegistrato_fragment;
    private RecyclerItemClickListener recyclerItemClickListener;
    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";
    private FloatingActionButton fab, modificaAnimaliBtn;
    private boolean expanded = false;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;
    private float offset1;
    private float offset2;
    private float offset3;
    private Bluetooth bluetooth;
    private  ArrayList <Animale> animaliPerCarico= new ArrayList<>();




    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NewApi")
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("ciao", String.valueOf(result.getResultCode()));

                    if (result.getResultCode() == 0) {
                Toast.makeText(getContext(),"Invio Annullato",Toast.LENGTH_SHORT).show();

                    } else if (result.getResultCode() == -1) {

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView,   RicercaDispositiviBluetooth.newInstance(animaliPerCarico)).addToBackStack(null).commit();


                    }
                }
            });





    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(getString(R.string.consiglio_accettare_permessi));
        alertDialogBuilder.setPositiveButton(getString(R.string.ho_capito),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.magari_piu_tardi), new DialogInterface.OnClickListener() {
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
        bluetooth = new Bluetooth(getActivity(),activityResultLaunch);



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
        richiediCarico= rootView.findViewById(R.id.richiediCarico);
        fab =  rootView.findViewById(R.id.fabAnimals);
        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        FloatingActionButton fabInvisible= rootView.findViewById(R.id.fab);
        fabInvisible.setVisibility(View.INVISIBLE);
        


        mostraSoloIncarico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //se la check box è premuta il bottone cambia

                if(!mostraSoloIncarico.isChecked()){
                    filterMieiAnimali();
                }else{
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
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiCarico()).commit();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showAlertDialog();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        richiediCarico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             ArrayList<Integer> positionSelectedCB= mAdapter.getPositionSelectedCB();

                if(positionSelectedCB.isEmpty()) {
                    Toast.makeText(getContext(), seleziona_almeno_un_animale, Toast.LENGTH_SHORT).show();
                }else {
                    Bluetooth bluetooth = new Bluetooth(getActivity(),activityResultLaunch);
                    //VERIFICO CHE IL BLUETOOTH E' SUPPORTATO
                    if(!bluetooth.VerificaBtSupportato())
                    {

                        if (filteredlist.size() == 0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"

                            for(int a=0;a<positionSelectedCB.size();a++){
                                int finalA = a;
                                caricoDB.checkAnimaleCarico(auth,db,mDataset.get(positionSelectedCB.get(a))).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.getResult().size()==0){
                                            Log.e("carico",mDataset.get(positionSelectedCB.get(finalA))+"");
                                            animaliPerCarico.add(mDataset.get(positionSelectedCB.get(finalA)));
                                        }else{
                                            Toast.makeText(getContext(),mDataset.get(positionSelectedCB.get(finalA)).getNome()+" è già in carico",Toast.LENGTH_SHORT).show();
                                        }
                                        if(finalA==positionSelectedCB.size()-1){
                                            if(animaliPerCarico.size()>0){
                                            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,RichiediCaricoFragment.newInstance(animaliPerCarico)).commit();
                                            }
                                        }
                                    }
                                });

                            }
                        } else {
                            for (int a = 0; a < positionSelectedCB.size(); a++) {
                                int finalA = a;
                                caricoDB.checkAnimaleCarico(auth, db, filteredlist.get(positionSelectedCB.get(a))).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().size() == 0) {
                                            animaliPerCarico.add(filteredlist.get(positionSelectedCB.get(finalA)));
                                        } else {
                                            Toast.makeText(getContext(), filteredlist.get(positionSelectedCB.get(finalA)).getNome() + " è già in carico", Toast.LENGTH_SHORT).show();
                                        }
                                        if (finalA == positionSelectedCB.size() - 1) {
                                            if(animaliPerCarico.size()>0){
                                                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,RichiediCaricoFragment.newInstance(animaliPerCarico)).commit();
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    }
                    else{
                        showDialogBluetooth(positionSelectedCB);
                    }






                }

            }
        });

       

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab(getView());
    }

    @Override
    public void onResume() {

        ruolo="";
        mDataset.clear();
        caricoDataset.clear();
        filteredlist.clear();
        countMyAnimals=0;



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
                    if(auth.getCurrentUser()!=null) {
                        utentiDB.getUtenti(auth, db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if ((document.get("ruolo").toString().equals("veterinario")) || document.get("ruolo").toString().equals("ente") || document.get("ruolo").toString().equals("associazione")) {
                                            // Nascondo la checkbox che mi mostra gli in carico
                                            mostraSoloIncarico.setVisibility(View.VISIBLE);
                                            ruolo = RUOLOVETERINARIO;
                                            break;
                                        }
                                    }
                                }
                                Log.e("DOVESONO", "GETRUOLO");
                                if (ruolo.equals(RUOLOVETERINARIO)) {

                                    CollectionReference animaliReference = db.collection("animali");
                                    caricoDB.getVetCarichi(auth, db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Salvare animale in un array con elementi oggetto animale
                                                caricoDataset.add(document.toObject(Carico.class));
                                                Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale", document.toObject(Carico.class).getIdAnimale());
                                                queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                //Salvare animale in un array con elementi oggetto animale
                                                                mDataset.add(document.toObject(Animale.class));
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                            //Passo i dati presi dal database all'adapter
                                            mAdapter = new AnimalAdapter(mDataset);
                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                            mRecyclerView.setItemViewCacheSize(mDataset.size());
                                            mRecyclerView.setAdapter(mAdapter);
                                            //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                                        }
                                    });
                                } else {
                                    fab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            closeSearchView();
                                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right).addToBackStack(null).replace(R.id.fragmentContainerView, new aggiungiAnimaleFragment()).commit();
                                        }
                                    });

                                    //Passo i dati presi dal database all'adapter
                                    mAdapter = new AnimalAdapter(mDataset);
                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                    mRecyclerView.setItemViewCacheSize(mDataset.size());
                                    mRecyclerView.setAdapter(mAdapter);
                                    //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                                }
                            }
                        });
                    }

                }
            });
        }
        addAnimale=getView().findViewById(R.id.aggiungiAnimaliBtn);
        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
            nonSeiRegistrato_fragment=new nonSeiRegistrato_fragment();
            getChildFragmentManager().beginTransaction().replace(R.id.myAnimalsFragment,nonSeiRegistrato_fragment).commit();
            addAnimale.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }else{
            if (nonSeiRegistrato_fragment!=null){
            getChildFragmentManager().beginTransaction().remove(nonSeiRegistrato_fragment).commit();
            }
            fab.setVisibility(View.VISIBLE);
        }
        addAnimale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearchView();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiAnimaleFragment()).commit();
            }
        });


        //Inizializzo l'ascoltatore al click dell'item
      recyclerItemClickListener= new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
               // AnimalAdapter.ViewHolder holder = (AnimalAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                //checkBoxAnimal= (MaterialCheckBox) holder.getCheckBox();

                if (flagCheckBox == false) {
                    Animale a;
                    Intent i = new Intent(getActivity().getApplicationContext(), ProfiloAnimale.class);
                    Log.e("filtered", filteredlist.size() + "");
                    if (filteredlist.size() == 0) {
                        //Ottengo l'oggetto dalla lista in posizione "position"

                        a = mDataset.get(position);
                        closeSearchView();
                        mDataset.clear();
                        filteredlist.clear();
                    } else {

                        a = filteredlist.get(position);
                        mDataset.clear();
                        filteredlist.clear();
                        closeSearchView();
                    }
                    //Inserisco l'oggetto nel bundle
                    i.putExtra("animale", a);
                    startActivity(i);
                }
            }

            @Override public void onLongItemClick(View view, int position) {
                //AnimalAdapter.ViewHolder holder = (AnimalAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
                //checkBoxAnimal= (MaterialCheckBox) holder.getCheckBox();
                if(!mostraSoloIncarico.isChecked()) {
                    if (flagCheckBox == false) {
                        mAdapter.setFlagCheckBox(true);
                        flagCheckBox = true;
                        mAdapter.notifyDataSetChanged();
                        richiediCarico.setVisibility(View.VISIBLE);
                        removeRecycleListener(mRecyclerView);
                    }
                }
            }
        });

        addRecycleListener(mRecyclerView);


        super.onResume();
        mostraSoloIncarico.setChecked(false);
    }

    @Override
    public void onStop() {
        mAdapter.setFlagCheckBox(false);
        flagCheckBox = false;
        mAdapter.notifyDataSetChanged();
        super.onStop();
    }

    public void addRecycleListener(RecyclerView mRecyclerView) {
        flagCheckBox=false;
        richiediCarico.setVisibility(View.GONE);
       mRecyclerView.addOnItemTouchListener(recyclerItemClickListener);

    }

    private void removeRecycleListener(RecyclerView mRecyclerView) {
        mRecyclerView.removeOnItemTouchListener(recyclerItemClickListener);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void filterMieiAnimali() {

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

    private int countRichieste = 0;
    private void fab(View rootView) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */

        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
        CollectionReference collection = db.collection("richiestaCarico");
        Query query = collection.whereEqualTo("idVeterinario", auth.getCurrentUser().getEmail()).whereEqualTo("stato","in sospeso");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        countRichieste= (int) snapshot.getCount();
                        setBadgeRichiesteFab(fab);
                        setBadgeRichiesteFabRichieste(fabAction3);
                    }
                });
            }
        });
        fabAction1 = rootView.findViewById(R.id.aggiungiAnimaliBtn);
        fabAction1.setVisibility(View.VISIBLE);



        fabAction2 = rootView.findViewById(R.id.aggiungiAnimaliInCaricoBtn);
        fabAction2.setVisibility(View.VISIBLE);



        fabAction3 = rootView.findViewById(R.id.richiesteAnimaliInCarico);
        fabAction3.setVisibility(View.VISIBLE);

        fabAction3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right).addToBackStack(null).replace(R.id.fragmentContainerView,new gestioneRichiesteCaricoFragment()).commit();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    if(badgeDrawableFab != null)
                        badgeDrawableFab.setVisible(false);
                    expandFab();

                } else {
                    if(badgeDrawableFab!=null) {
                        badgeDrawableFab.setNumber(countRichieste);
                    }
                    collapseFab();

                }
            }
        });

        /*
        Restituisce ViewTreeObserver per la gerarchia di questa vista.
         L'osservatore dell'albero di visualizzazione può essere utilizzato per ricevere notifiche
         quando si verificano eventi globali, come il cambio del layout.
         */
        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - fabAction1.getY();
                fabAction1.setTranslationY(offset1);
                offset2 = fab.getY() - fabAction2.getY();
                fabAction2.setTranslationY(offset2);
                offset3 = fab.getY() - fabAction3.getY();
                fabAction3.setTranslationY(offset3);

                return true;
            }
        });
    }

    BadgeDrawable badgeDrawableFabRichieste;
    private void setBadgeRichiesteFabRichieste(FloatingActionButton fab) {
        fab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onGlobalLayout() {
                BadgeDrawable badgeDrawableFab;
                try{
                    badgeDrawableFabRichieste = BadgeDrawable.create(getContext());


                    badgeDrawableFabRichieste.setBadgeGravity(BadgeDrawable.TOP_END);
                    Log.e("count2",countRichieste+"fab2");
                    badgeDrawableFabRichieste.setNumber(countRichieste);

                    //Important to change the position of the Badge
                    BadgeUtils.attachBadgeDrawable(badgeDrawableFabRichieste, fab, null);

                }catch(Exception e){

                }
                
            }
        });
    }
    BadgeDrawable badgeDrawableFab;
    private void setBadgeRichiesteFab(FloatingActionButton fab) {
        fab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onGlobalLayout() {
                try {
                    badgeDrawableFab = BadgeDrawable.create(getContext());

                    badgeDrawableFab.setBadgeGravity(BadgeDrawable.TOP_END);
                    Log.e("count2",countRichieste+"fab");
                    badgeDrawableFab.setNumber(countRichieste);

                    //Important to change the position of the Badge
                    BadgeUtils.attachBadgeDrawable(badgeDrawableFab, fab, null);

                }catch (Exception e){

                }

            }
        });
    }


    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3));
        animatorSet.start();

        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3));
        animatorSet.start();

        //animateFab();
    }

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    /*
    SERVONO PER CREARE I MOVIMENTI DELLE ANIMAZIONI CHE VENGONO POI CHIAMATI DAI VARI AnimatorSet
     */
    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }


    private void  showDialogBluetooth(ArrayList<Integer> positionSelectedCB)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage(getString(R.string.se_lo_specialista_si_trova_vicino_a_te) +
                        getString(R.string.seleziona_modalita_invio_carico))
                .setTitle(R.string.modalita_invio_carico)
                .setPositiveButton(R.string.continua_senza_bluetooth, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList <Animale> animaliPerCarico= new ArrayList<>();
                        if (filteredlist.size() == 0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"

                            for(int a=0;a<positionSelectedCB.size();a++){
                                int finalA = a;
                                caricoDB.checkAnimaleCarico(auth,db,mDataset.get(positionSelectedCB.get(a))).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.getResult().size()==0){
                                                Log.e("carico",mDataset.get(positionSelectedCB.get(finalA))+"");
                                                animaliPerCarico.add(mDataset.get(positionSelectedCB.get(finalA)));
                                            }else{
                                                Toast.makeText(getContext(),mDataset.get(positionSelectedCB.get(finalA)).getNome()+" è già in carico",Toast.LENGTH_SHORT).show();
                                            }
                                            if(finalA==positionSelectedCB.size()-1){
                                                if(animaliPerCarico.size()>0){
                                                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,RichiediCaricoFragment.newInstance(animaliPerCarico)).commit();
                                                }
                                            }
                                    }
                                });

                            }

                        } else {
                            for (int a = 0; a < positionSelectedCB.size(); a++) {
                                int finalA = a;
                                caricoDB.checkAnimaleCarico(auth, db, filteredlist.get(positionSelectedCB.get(a))).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().size() == 0) {
                                            animaliPerCarico.add(filteredlist.get(positionSelectedCB.get(finalA)));
                                        } else {
                                            Toast.makeText(getContext(), filteredlist.get(positionSelectedCB.get(finalA)).getNome() + " è già in carico", Toast.LENGTH_SHORT).show();
                                        }
                                        if (finalA == positionSelectedCB.size() - 1) {
                                            if(animaliPerCarico.size()>0){
                                                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,RichiediCaricoFragment.newInstance(animaliPerCarico)).commit();
                                            }
                                        }
                                    }
                                });

                            }

                        }

                    }
                })
                .setNegativeButton(R.string.invia_con_bluetooth, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ArrayList<Animale> animaliPerCarico = new ArrayList<>();

                        if (filteredlist.size() == 0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"

                            for (int a = 0; a < positionSelectedCB.size(); a++) {
                                animaliPerCarico.add(mDataset.get(positionSelectedCB.get(a)));
                            }
                            mDataset.clear();
                            filteredlist.clear();
                        } else {

                            for (int a = 0; a < positionSelectedCB.size(); a++) {
                                animaliPerCarico.add(filteredlist.get(positionSelectedCB.get(a)));
                            }
                            mDataset.clear();
                            filteredlist.clear();
                        }
                        Bluetooth bluetooth = new Bluetooth(getActivity(), activityResultLaunch);

                        bluetooth.AbilitazioneBT(animaliPerCarico);


                        mAdapter.notifyDataSetChanged();



                    }
                });

        builder.show();
    }
}

