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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;
import model.Animale;
import adapter.ReportAdapter;
import adapter.AnimalAdapter;


public class reports_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected ReportAdapter mAdapter;
    protected ArrayList<Segnalazione> mDataset= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Prendo i dati degli animali dal database
        initDataset();


    }



    @Override
    public void onStart() {
        super.onStart();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reports_fragment, container, false);

        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleReport);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initDataset() {

        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");


        if(auth.getCurrentUser()!=null){

        segnalazioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Salvare animale in un array con elementi oggetto animale
                        mDataset.add(document.toObject(Segnalazione.class));
                        Log.e("animale", document.getId() + " => " + document.getData());
                    }
                    //Passo i dati presi dal database all'adapter
                    mAdapter = new ReportAdapter(mDataset);
                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                    mRecyclerView.setAdapter(mAdapter);

                }else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }



            }
        });

        }



    }



}