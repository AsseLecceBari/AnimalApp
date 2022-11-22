package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

import adapter.AdozioniAdapter;
import adapter.AnimalAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;


public class adoptions_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected AdozioniAdapter mAdapter;
    protected ArrayList<Animale> mDataset= new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        View aggiungiAdozione = getView().findViewById(R.id.aggiungiAdozione);
        aggiungiAdozione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungi_adozione_fragment()).addToBackStack(null).commit();
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        initDataset();
        View rootView = inflater.inflate(R.layout.fragment_adoptions_fragment, container, false);

        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleadoption);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        return rootView;
    }

    private void initDataset() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference adozioniRef=db.collection("adozioni");
        CollectionReference animali=db.collection("animali");

        if(auth.getCurrentUser()!=null) {
         adozioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document1: task.getResult()) {
                            animali.whereEqualTo("idAnimale",document1.getId()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document: task.getResult()) {
                                                   // QuerySnapshot document = task.getResult();


                                                    mDataset.add(document.toObject(Animale.class));
                                                   // Log.d("ciao", String.valueOf(mDataset.size()));
                                                    mAdapter = new AdozioniAdapter(mDataset);
                                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }

                                            }
                                        }
                                    });
                            //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                        }
                    }
                }
            });
        }
    }

}