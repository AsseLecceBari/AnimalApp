package fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;

import adapter.AggiungiAnimaleAdapter;
import adapter.AnimalAdapter;
import class_general.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Veterinario;


public class aggiungi_adozione_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected AggiungiAnimaleAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Animale> mDataset= new ArrayList<>();
    private Checkable checkbox;
    private final ArrayList <String> idAnimali = new ArrayList<>();
    private View botton;

    androidx.appcompat.widget.Toolbar main_action_bar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitData();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =inflater.inflate(R.layout.fragment_aggiungi_adozione_fragment, container, false);
         main_action_bar = getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Seleziona Animali");
       if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
        }
        main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleaggiungiAdozione);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            main_action_bar.inflateMenu(R.menu.menu_bar_main);
            main_action_bar.setTitle("AnimalApp");
        }




    }


    @Override
    public void onResume() {
        super.onResume();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                       // Log.d("ciao", String.valueOf(view.getId()));
                        checkbox= view.findViewById(R.id.checkBoxadozioni);

                        if(!checkbox.isChecked()){
                            idAnimali.add(new String(String.valueOf(view.getId())));
                           // Log.d("ciao", String.valueOf(mRecyclerView.getId()));
                            Log.d("ciao", String.valueOf(position));

                            checkbox.setChecked(true);

                        }
                        else

                            checkbox.setChecked(false);





                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );
        botton= getView().findViewById(R.id.Btnaggiungiadozioni);
        botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //   Log.d("ciao", mRecyclerView.getId());
            }
        });




    }


    public void InitData() {



        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
       ;
        CollectionReference animali=db.collection("animali");


        if(auth.getCurrentUser()!=null) {
            Query query = animali.whereEqualTo("emailProprietario", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            //Salvare animale in un array con elementi oggetto animale
                            mDataset.add(document.toObject(Animale.class));


                            Log.e("animale", document.getId() + " => " + document.getData());
                            //Passo i dati presi dal database all'adapter
                            mAdapter = new AggiungiAnimaleAdapter(mDataset);
                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                            mRecyclerView.setAdapter(mAdapter);
                            //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                        }
                    } else {
                        Log.d("ciao", "Error getting documents: ", task.getException());
                    }
                }
            });
        }





    }



}