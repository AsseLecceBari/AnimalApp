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
import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;
import model.Animale;

public class myanimals_fragment extends Fragment {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvLogin;
    private TextView tvRegistrati;
    FloatingActionButton addAnimale;
    private Animale a;

    private enum LayoutManagerType {

        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;


    protected RecyclerView mRecyclerView;
    protected AnimalAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
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

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

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
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        return rootView;
    }


    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
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
                        Log.d("ERROR", "Error getting documents: ", task.getException());
                    }
                }
            });
        }



    }
}

