package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapter.AnimalAdapter;
import adapter.SpeseAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SpesaAnimale;

public class spese extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private FloatingActionButton addSpesa;
    protected RecyclerView mRecyclerView;

    protected ArrayList<SpesaAnimale> mDataset= new ArrayList<>();
    protected SpeseAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spese, container, false);
        addSpesa = rootView.findViewById(R.id.aggiungiSpesaBtn);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleSpese);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // simulo un dataset TODO ------
        mDataset.add(new SpesaAnimale());
        mDataset.add(new SpesaAnimale());

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
                Toast.makeText(getActivity().getApplicationContext(), "Aggiungi spesa!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}