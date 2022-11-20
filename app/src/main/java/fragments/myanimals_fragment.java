package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
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
import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;
import model.Animale;

public class myanimals_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvLogin;
    private TextView tvRegistrati;
    FloatingActionButton addAnimale;
    private Animale a;




    protected RecyclerView mRecyclerView;
    protected AnimalAdapter mAdapter;

    protected ArrayList<Animale> mDataset= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //Prendo i dati degli animali dal database
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myanimals_fragment, container, false);
        tvLogin=rootView.findViewById(R.id.tvLoginMyAnimals);
        tvRegistrati=rootView.findViewById(R.id.tvRegisterHereMyAnimals);
        addAnimale=rootView.findViewById(R.id.aggiungiAnimaliBtn);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
                addAnimale.setVisibility(View.GONE);
                tvLogin.setOnClickListener(view ->{
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            });
            tvRegistrati.setOnClickListener(view ->{
                startActivity(new Intent(getActivity().getApplicationContext(), RegisterActivity.class));
            });
        }else {
            ConstraintLayout cl= rootView.findViewById(R.id.noLoggedLayoutMyAnimals);
            cl.setVisibility(View.INVISIBLE);
        }
        addAnimale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungiAnimaleFragment()).addToBackStack(null).commit();
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
                        Intent i = new Intent(getActivity().getApplicationContext(), ProfiloAnimale.class);
                        //Ottengo l'oggetto dalla lista in posizione "position"
                        Animale a = mDataset.get(position);
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
                    } else {
                        Log.d("ciao", "Error getting documents: ", task.getException());
                    }
                }

            });
        }



    }
}

