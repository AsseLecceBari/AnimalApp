package fragments_mieiAnimali;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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


import java.util.ArrayList;

import adapter.AnimalAdapter;
import DB.AnimaleDB;
import fragments.RecyclerItemClickListener;
import fragments.nonSeiRegistrato_fragment;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Associazione;
import model.Carico;
import model.Ente;
import model.Persona;
import model.Veterinario;

public class myanimals_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FloatingActionButton addAnimale, addIncarico;
    private Animale a;
    private MaterialCheckBox mostraSoloIncarico;

    protected RecyclerView mRecyclerView;
    private static AnimalAdapter mAdapter;


    public ArrayList<Animale> getmDataset() {
        return mDataset;
    }

    private static ArrayList<Animale> mDataset= new ArrayList<>();
    private ArrayList<Animale> filteredlist=new ArrayList<>();
    private ArrayList<Carico> caricoDataset= new ArrayList<>();
    private AnimaleDB animaleDAO;
    private MainActivity mainActivity;
    private Toolbar main_action_bar;
    String ruolo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataset.clear();
        View rootView = inflater.inflate(R.layout.fragment_myanimals_fragment, container, false);
        mostraSoloIncarico = rootView.findViewById(R.id.mostraInCarico);
        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleMyAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        animaleDAO= new AnimaleDB();



        // controlli per la checkbox in carico
        CollectionReference reference=db.collection("utenti");
        if(auth.getCurrentUser()!=null) {
            Query query = reference.whereEqualTo("email", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(!(document.get("ruolo").toString().equals("proprietario"))){
                                // Nascondo la checkbox che mi mostra gli in carico
                                mostraSoloIncarico.setVisibility(View.VISIBLE);
                                ruolo="veterinario";
                                break;
                            }else{
                                //Todo: init dataset alternatvo ---------------------------------------------------
                            }
                        }
                    }

                }
            });
        }

        addAnimale=rootView.findViewById(R.id.aggiungiAnimaliBtn);
        addIncarico=rootView.findViewById(R.id.aggiungiAnimaliInCaricoBtn);



        //se la check box è premuta il bottone cambia
        if(!mostraSoloIncarico.isChecked()){
            addAnimale.setVisibility(View.VISIBLE);
            addIncarico.setVisibility(View.GONE);
        }else{
            addAnimale.setVisibility(View.GONE);
            addIncarico.setVisibility(View.VISIBLE);
            filterCarico(caricoDataset);
        }
        mostraSoloIncarico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //se la check box è premuta il bottone cambia
                if(!mostraSoloIncarico.isChecked()){
                    addAnimale.setVisibility(View.VISIBLE);
                    addIncarico.setVisibility(View.GONE);
                    filterMieiAnimali(caricoDataset);
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
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiCarico()).commit();

            }
        });


        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        if(auth.getCurrentUser()!=null) {
            animaleDAO.getMieiAnimali(auth, db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Salvare animale in un array con elementi oggetto animale
                            mDataset.add(document.toObject(Animale.class));
                            Log.e("animale", document.getId() + " => " + document.getData());
                        }
                    }
                    //Se siamo loggati con il veterinario aggiungiamo nel dataset anche gli animali in carico
                    if(ruolo.equals("veterinario")){
                        CollectionReference carichiReference = db.collection("carichi");
                        CollectionReference animaliReference = db.collection("animali");
                        Query queryCarichi = carichiReference.whereEqualTo("idProfessionista", auth.getCurrentUser().getEmail());
                        queryCarichi.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Salvare animale in un array con elementi oggetto animale
                                    Log.e("carico",document.toObject(Carico.class)+"");
                                    caricoDataset.add(document.toObject(Carico.class));
                                    Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale",document.toObject(Carico.class).getIdAnimale());
                                    queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //Salvare animale in un array con elementi oggetto animale
                                                    mDataset.add(document.toObject(Animale.class));
                                                    Log.e("animale", document.getId() + " => " + document.getData());
                                                }
                                                Log.e("ehi","ehi");
                                                //Passo i dati presi dal database all'adapter
                                                mAdapter = new AnimalAdapter(mDataset);
                                                // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                mRecyclerView.setAdapter(mAdapter);
                                                //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                                                filterMieiAnimali(caricoDataset);

                                            }

                                        }
                                    });

                                }

                            }

                        });

                    }

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
        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleMyAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    public void filterMieiAnimali(ArrayList<Carico> caricoDataset) {

        filteredlist=new ArrayList<>();
        // running a for loop to compare elements.

            for (Animale item : mDataset) {
                for (Carico carico : caricoDataset) {
                // checking if the entered string matched with any item of our recycler view.
                    if (item.getIdAnimale().equals(carico.getIdAnimale())) {
                        }else {
                        if(filteredlist.size()>0){
                                for (Animale filter : filteredlist){
                                    if (filter.getIdAnimale().equals(carico.getIdAnimale())){
                                        Log.e("ou","ouif");
                                    }else{
                                        Log.e("ou","ou");
                                        filteredlist.add(item);
                                        break;
                                    }
                                }

                        }else{
                            filteredlist.add(item);
                        }
                    }
                    }
                }

            mAdapter.filterList(filteredlist);
    }

    public void filterCarico(ArrayList<Carico> caricoDataset) {

        filteredlist=new ArrayList<>();
        // running a for loop to compare elements.
            for (Carico animal : caricoDataset) {
                for (Animale item : mDataset) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (item.getIdAnimale().toLowerCase().contains(animal.getIdAnimale().toLowerCase())) {
                        // if the item is matched we are
                        // adding it to our filtered list.
                        filteredlist.add(item);
                    }
                }
            }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredlist);
        }
    }


    public void filter(String text) {
        Log.e("text",text);
        filteredlist=new ArrayList<>();
        // running a for loop to compare elements.
        for (Animale item : mDataset) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getNome().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredlist);
        }
    }
    private void closeSearchView() {
        mainActivity= (MainActivity) getActivity();
        main_action_bar= mainActivity.getMain_action_bar();
        main_action_bar.collapseActionView();
    }



}

