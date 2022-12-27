package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

import DB.AnimaleDB;
import DB.CaricoDB;
import adapter.AnimalAdapter;
import adapter.GestioneRichiesteCaricoAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Carico;
import model.RichiestaCarico;


public class gestioneRichiesteCaricoFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private AnimaleDB animaleDB;
    private CaricoDB caricoDB;
    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset = new ArrayList<>();
    private ArrayList<RichiestaCarico> richiesteDataset = new ArrayList<>();
    private ArrayList<Animale> filteredlist = new ArrayList<>();
    private GestioneRichiesteCaricoAdapter mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);

    public gestioneRichiesteCaricoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gestione_richieste_carico, container, false);
        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleGestioneRichiesteCarico);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        animaleDB = new AnimaleDB();
        caricoDB= new CaricoDB();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataset.clear();
        filteredlist.clear();

        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        if (auth.getCurrentUser() != null) {
            CollectionReference animaliReference = db.collection("animali");
            caricoDB.getVetRichiesteCarichi(auth,db).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            richiesteDataset.add(document.toObject(RichiestaCarico.class));
                            Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale", document.toObject(RichiestaCarico.class).getIdAnimale());
                            queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Salvare animale in un array con elementi oggetto animale
                                            mDataset.add(document.toObject(Animale.class));
                                        }
                                    }
                                    mAdapter = new GestioneRichiesteCaricoAdapter(mDataset);
                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                    mRecyclerView.setItemViewCacheSize(mDataset.size());
                                    mRecyclerView.setAdapter(mAdapter);

                                }
                            });
                        }
                    }

                }

            });
        }
    }
}
