package fragments_mieiAnimali;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Associazione;
import model.Ente;
import model.Persona;
import model.Veterinario;

public class myanimals_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    FloatingActionButton addAnimale;
    private Animale a;
    private MaterialCheckBox mostraSoloIncarico;

    protected RecyclerView mRecyclerView;
    protected static AnimalAdapter mAdapter;


    public ArrayList<Animale> getmDataset() {
        return mDataset;
    }

    protected static ArrayList<Animale> mDataset= new ArrayList<>();
    private ArrayList<Animale> filteredlist=new ArrayList<>();
    private   AnimaleDB animaleDAO;

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
                                break;
                            }else{
                                //Todo: init dataset alternatvo ---------------------------------------------------
                            }
                        }
                    }

                }
            });
        }

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
                    }//Passo i dati presi dal database all'adapter
                    mAdapter = new AnimalAdapter(mDataset);
                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                    mRecyclerView.setAdapter(mAdapter);
                    //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET

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
                        if(filteredlist.size()==0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"
                            a = mDataset.get(position);
                        }else{
                            Log.e("filtered",filteredlist+"");

                            a = filteredlist.get(position);}
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




    public void filter(String text) {
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




}

