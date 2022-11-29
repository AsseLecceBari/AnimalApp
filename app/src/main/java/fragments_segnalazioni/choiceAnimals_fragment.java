package fragments_segnalazioni;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.AnimalAdapter;
import fragments.RecyclerItemClickListener;

import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class choiceAnimals_fragment extends Fragment {


    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Animale a;

    protected RecyclerView mRecyclerView;
    protected AnimalAdapter mAdapter;

    protected ArrayList<Animale> mDataset= new ArrayList<>();

    public choiceAnimals_fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDataset.clear();
        initDataset();
        View rootView = inflater.inflate(R.layout.fragment_choice_animals_fragment, container, false);


        //da implementare la parte dei non loggati con il fragment creata da enrico
        auth=FirebaseAuth.getInstance();




        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleChoiceAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        a = mDataset.get(position);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new smarrimento_fragments().newInstance(a)).addToBackStack(null).commit();



                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );





        return rootView;
    }









    private void initDataset() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference animaliReference=db.collection("animali");
        if(auth.getCurrentUser()!=null) {
            Query query = animaliReference.whereEqualTo("emailProprietario", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Salvare animale in un array con elementi oggetto animale
                            mDataset.add(document.toObject(Animale.class));
                            Log.e("animale", document.getId() + " => " + document.getData());
                        }

                        //Passo i dati presi dal database all'adapter
                        mAdapter = new AnimalAdapter(mDataset);

                        // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                        mRecyclerView.setAdapter(mAdapter);
                        //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                    }
                }
            });
        }
    }



}