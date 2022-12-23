package fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Objects;

import adapter.SegnalazioneSanitariaAdapter;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Carico;
import model.SegnalazioneSanitaria;

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
                        SegnalazioneSanitaria s = mDataset.get(position);
                        ProfiloAnimale p = (ProfiloAnimale) getActivity();
                        p.setS(s);
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new visualizzaSegnalazioneSanitaria()).commit();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        addSegnalazioneSanitaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new aggiungiSegnalazioneSanitaria()).commit();
            }
        });

        if(isProprietario()){
            addSegnalazioneSanitaria.setVisibility(View.VISIBLE);
        }else{
            // se non è proprietario, SE è colui che ce lha attualmente in carico  && è veterinario allora visualizza il pulsante per aggiungere la segnalazione sanitaria
            CollectionReference docRef = db.collection("carichi");
            Query query = docRef.whereEqualTo("inCorso", true).whereEqualTo("idProfessionista", Objects.requireNonNull(auth.getCurrentUser()).getEmail()).whereEqualTo("idAnimale", animale.getIdAnimale());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().size() == 0){
                            // nascondo
                            addSegnalazioneSanitaria.setVisibility(View.GONE);
                        }else{
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Carico  trovato
                                Carico c = document.toObject(Carico.class);

                                // se è veterianario vedo il pulsante
                                CollectionReference animaliReference=db.collection("utenti");
                                if(auth.getCurrentUser()!=null) {
                                    Query query = animaliReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                String ruolo = null;

                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                    ruolo = Objects.requireNonNull(document.get("ruolo")).toString();
                                                    if(ruolo.equals("veterinario")){
                                                        addSegnalazioneSanitaria.setVisibility(View.VISIBLE);
                                                    }else{
                                                        addSegnalazioneSanitaria.setVisibility(View.GONE);
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                    });
                                }

                                break;
                            }
                        }
                    }
                }
            });
        }

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

    private boolean isProprietario() {
        return animale.getEmailProprietario().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail());

    }
}