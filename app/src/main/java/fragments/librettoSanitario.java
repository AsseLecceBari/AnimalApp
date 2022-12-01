package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.Date;
import java.util.Random;

import adapter.SegnalazioneSanitariaAdapter;
import adapter.SpeseAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;
import model.SpesaAnimale;

public class librettoSanitario extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;

    private FloatingActionButton addSegnalazioneSanitaria;
    protected RecyclerView mRecyclerView;

    protected ArrayList<SegnalazioneSanitaria> mDataset= new ArrayList<>();
    protected SegnalazioneSanitariaAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_libretto_sanitario, container, false);
        startDatabase();

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleLibrettoSanitario);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addSegnalazioneSanitaria = rootView.findViewById(R.id.aggiungiBtn);

        initDataset();

        //Inizializzo l'ascoltatore al click dell'item
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        addSegnalazioneSanitaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new AggiungiSegnalazioneSanitariaSpesa()).commit();
                SegnalazioneSanitaria s = new SegnalazioneSanitaria(new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString(), "provaemail", "motivo consultazione", "diagnosi", "farmaci", "trattamento",new Random().nextInt(999999999)+"", animale.getIdAnimale().toString());
                db.collection("segnalazioneSanitaria").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                Toast.makeText(getActivity().getApplicationContext(), "Segnalazione aggiunta!", Toast.LENGTH_SHORT).show();

            }
        });
        return rootView;
    }

    private void initDataset() {
        mDataset.clear();
        CollectionReference reference=db.collection("segnalazioneSanitaria");
        if(auth.getCurrentUser()!=null) {
            Query query = reference.whereEqualTo("idAnimale", animale.getIdAnimale());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mDataset.add(document.toObject(SegnalazioneSanitaria.class));
                            Log.e("animale", document.getId() + " => " + document.getData());
                        }
                        //Passo i dati presi dal database all'adapter
                        mAdapter = new SegnalazioneSanitariaAdapter(mDataset);
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