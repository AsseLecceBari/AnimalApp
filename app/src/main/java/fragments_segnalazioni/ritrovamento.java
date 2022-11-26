package fragments_segnalazioni;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.ReportAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class ritrovamento extends Fragment {
    private TextView indirizzo;
    private Button gps, mostra;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected ReportAdapter mAdapter;
    protected ArrayList<Segnalazione> mDataset= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ritrovamento, container, false);
        indirizzo = rootView.findViewById(R.id.etRegIndirizzo);
        gps = rootView.findViewById(R.id.gps);
        mostra = rootView.findViewById(R.id.cerca);

        // imposto i listener
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aggiornaTextviewConCordinate();
            }
        });
        mostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostraRyclerView();
            }
        });

        return rootView;
    }

    public void mostraRyclerView() {
        // TODO: mostrare solo gli smarrimenti vicini l'indirizzo _____

        // !!!!!!!!!! attualmente mostra tutti le segnalazioni di smarrimenti !!!!!!!!!!!!!!!!
        Toast.makeText(getActivity().getApplicationContext(), "Mostro in base ai tuoi filtri", Toast.LENGTH_LONG).show();

        initDataset();

    }

    private void initDataset() {
        mDataset.clear();
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");


        if(auth.getCurrentUser()!=null){
            segnalazioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Salvare animale in un array con elementi oggetto animale
                            mDataset.add(document.toObject(Segnalazione.class));
                            //Passo i dati presi dal database all'adapter
                            mAdapter = new ReportAdapter(mDataset);
                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }else {
                        Log.d("ERROR", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    public void aggiornaTextviewConCordinate() {
        // todo: il gps impostera nella textview le cordinate

        // es:
        // indirizzo.setText(cordinate gps);
        Toast.makeText(getActivity().getApplicationContext(), "Coordinate gps inserite nel indirizzo di ritrovo", Toast.LENGTH_LONG).show();
    }

}