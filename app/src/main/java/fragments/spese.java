package fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import adapter.AnimalAdapter;
import adapter.SpeseAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SpesaAnimale;

public class spese extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;

    private FloatingActionButton addSpesa;
    protected RecyclerView mRecyclerView;

    protected ArrayList<SpesaAnimale> mDataset= new ArrayList<>();
    protected SpeseAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        startDatabase();
        View rootView = inflater.inflate(R.layout.fragment_spese, container, false);
        addSpesa = rootView.findViewById(R.id.aggiungiSpesaBtn);
        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleSpese);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // simulo un dataset TODO ------
        initDataset();

        // lo setto -------- TODO-------
        mAdapter = new SpeseAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        //Inizializzo l'ascoltatore al click dell'item
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );


        addSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo: aprire un fragment per inserire la spesa
                SpesaAnimale s = new SpesaAnimale("toilettatura test", new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString(), "niente da aggiungere test", new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), 10, 2);
                db.collection("spese").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                Toast.makeText(getActivity().getApplicationContext(), "Spesa di test aggiunta!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void initDataset() {
        CollectionReference speseReference=db.collection("spese");
        if(auth.getCurrentUser()!=null) {
            Query query = speseReference.whereEqualTo("idAnimale", animale.getIdAnimale());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mDataset.add(document.toObject(SpesaAnimale.class));
                            Log.e("animale", document.getId() + " => " + document.getData());
                        }
                        //Passo i dati presi dal database all'adapter
                        mAdapter = new SpeseAdapter(mDataset);
                        // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            });
        }
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}