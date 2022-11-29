package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.internal.MaterialCheckable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.Objects;

import adapter.AdozioniAdapter;
import class_general.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;


public class adoptions_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected AdozioniAdapter mAdapter;
    protected ArrayList<Animale> mDataset = new ArrayList<>();
    private LinearLayout paginalogin;
    private View btnaccesso;
    private CheckBox chbimieiAnnunci;
    private CheckBox chbannunciesterni;
    private CheckBox chbannuncigenerale;
    private View layoutfiltri;
    private View layoutopenfiltri;
    private View btnopenFiltri;
    private View bottonechiudifiltri;
    private View layoutPreferiti;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        filtri();


        View aggiungiAdozione = getView().findViewById(R.id.aggiungiAdozione);
        aggiungiAdozione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new aggiungi_adozione_fragment()).addToBackStack(null).commit();
            }
        });


        mRecyclerView.addOnItemTouchListener(
                new class_general.RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        Animale animale = mDataset.get(position);
                        // Log.d("ciao", animale.getIdAnimale());

                        Intent intent = new Intent(getContext(), ProfiloAnimale.class);
                        intent.putExtra("animale", animale);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_adoptions_fragment, container, false);
        mDataset.clear();
        initDataAnnunciEsterni();
        paginalogin = rootView.findViewById(R.id.paginalogin);
        btnaccesso = rootView.findViewById(R.id.btnLogin);
        chbimieiAnnunci = rootView.findViewById(R.id.checkBoxImieiannunci);
        chbannunciesterni = rootView.findViewById(R.id.checkBox2annunciesterni);
        chbannuncigenerale = rootView.findViewById(R.id.checkBox3annunciGenerale);
        layoutfiltri = rootView.findViewById(R.id.layoutfiltri);
        layoutopenfiltri = rootView.findViewById(R.id.layoutaprifiltri);
        btnopenFiltri = rootView.findViewById(R.id.btnaprifiltri);
        bottonechiudifiltri = rootView.findViewById(R.id.chiudifiltri);
        layoutPreferiti = rootView.findViewById(R.id.layoutPreferiti);


        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleadoption);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return rootView;
    }

    private void initDataset() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference adozioniRef = db.collection("adozioni");
        CollectionReference animali = db.collection("animali");

        //if(auth.getCurrentUser()!=null) {
        adozioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task.getResult()) {
                        animali.whereEqualTo("idAnimale", document1.getId()).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
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
        // }
    }


    public void initDataAnnunciEsterni(){
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference adozioniRef = db.collection("adozioni");
        CollectionReference animali = db.collection("animali");

        //if(auth.getCurrentUser()!=null) {
        adozioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document1 : task.getResult()) {
                        animali.whereEqualTo("idAnimale", document1.getId()).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // QuerySnapshot document = task.getResult();

                                                Animale prova= document.toObject(Animale.class);
                                                Log.d("ciao3", prova.getEmailProprietario());

                                                if(!Objects.equals(auth.getCurrentUser().getEmail(), prova.getEmailProprietario())) {


                                                    mDataset.add(document.toObject(Animale.class));
                                                    // Log.d("ciao", String.valueOf(mDataset.size()));
                                                    mAdapter = new AdozioniAdapter(mDataset);
                                                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                    mRecyclerView.setAdapter(mAdapter);
                                                }
                                            }

                                        }
                                    }
                                });
                        //LA FUNZIONE GET DI FIREBASE è ASINCRONA QUINDI HO SETTATO QUI L'ADAPTER VIEW PERCHè SE NO FINIVA PRIMA LA BUILD DEL PROGRAMMA E POI LA FUNZIONE GET
                    }
                }
            }
        });
        // }
    }


    public void filtri() {
        chbannunciesterni.setChecked(true);
        layoutopenfiltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri.setVisibility(View.VISIBLE);
               // layoutopenfiltri.setVisibility(View.GONE);
                //btnopenFiltri.setVisibility(View.GONE);
                layoutopenfiltri.setVisibility(View.GONE);

                //bottonechiudifiltri.setVisibility(View.VISIBLE);

            }
        });

        bottonechiudifiltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri.setVisibility(View.GONE);
               // btnopenFiltri.setVisibility(View.VISIBLE);

                layoutopenfiltri.setVisibility(View.VISIBLE);
            }
        });


        chbannunciesterni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    mRecyclerView.setAdapter(null);

                }
                else{
                    mDataset.clear();
                    initDataAnnunciEsterni();
                }
            }
        });

    }
}
