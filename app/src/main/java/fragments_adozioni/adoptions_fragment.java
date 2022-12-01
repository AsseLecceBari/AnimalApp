package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import adapter.AdozioniAdapter;
import class_general.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;


public class adoptions_fragment extends Fragment {

    private FirebaseAuth auth;
    private   SharedPreferences share;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected AdozioniAdapter mAdapter;
    protected ArrayList<Animale> mDataset = new ArrayList<>();
    private LinearLayout paginalogin;
    private View btnaccesso;
    private RadioButton rdbimieiAnnunci;
    private RadioButton rdbannuncigenerale;
    private RadioButton rdbannuncipreferiti;
    private View layoutfiltri;
    private View layoutopenfiltri;
    private View btnopenFiltri;
    private View bottonechiudifiltri;
    private View layoutPreferiti;
    private int tipoannunci=2;
    private View barrachilometri;
    private ArrayList <Adozione> adozione= new ArrayList<>();

    public static final String mypreference =  "animalipreferiti";
    public static final String Name = "nameKey";
    public static String Email ;
    private Set<String> set  = new HashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        filtri();
        shareprefr();
      Log.d("ciao5", String.valueOf(getActivity().getSharedPreferences(mypreference, Context.MODE_MULTI_PROCESS))) ;



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
                        Adozione ad= adozione.get(position);
                        // Log.d("ciao", animale.getIdAnimale());

                        Intent intent = new Intent(getContext(), ProfiloAnimale.class);
                        intent.putExtra("animale", animale);
                        intent.putExtra("adozione",ad );
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

        if(savedInstanceState!= null)
        {
           tipoannunci= savedInstanceState.getInt("tipoannunci");
        }

        View rootView = inflater.inflate(R.layout.fragment_adoptions_fragment, container, false);
        mDataset.clear();
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!= null) {

            initDataAnnunci(tipoannunci);
        }
        else{
            tipoannunci=3;
            initDataAnnunci(tipoannunci);
        }


        paginalogin = rootView.findViewById(R.id.paginalogin);
        btnaccesso = rootView.findViewById(R.id.btnLogin);
        rdbimieiAnnunci = rootView.findViewById(R.id.radioImieiannunci);
        rdbannuncigenerale = rootView.findViewById(R.id.radioannunciesterni);
        rdbannuncipreferiti = rootView.findViewById(R.id.radioannunciPreferiti);
        layoutfiltri = rootView.findViewById(R.id.layoutfiltri);
        layoutopenfiltri = rootView.findViewById(R.id.layoutaprifiltri);
        btnopenFiltri = rootView.findViewById(R.id.btnaprifiltri);
        bottonechiudifiltri = rootView.findViewById(R.id.chiudifiltri);
        layoutPreferiti = rootView.findViewById(R.id.layoutPreferiti);
        barrachilometri=rootView.findViewById(R.id.barrachilometri);


        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleadoption);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return rootView;
    }




    public void initDataAnnunci(int tip){
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

                        adozione.add(document1.toObject(Adozione.class));




                        Adozione temporanea= document1.toObject(Adozione.class);


                        animali.whereEqualTo("idAnimale", temporanea.getIdAnimale()).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Animale t;
                                                switch (tip) {
                                                    case 1:
                                                         t = document.toObject(Animale.class);
                                                        if (Objects.equals(auth.getCurrentUser().getEmail(), t.getEmailProprietario())) {
                                                            mDataset.add(document.toObject(Animale.class));
                                                             Log.d("ciao4", t.getNome());
                                                            mAdapter = new AdozioniAdapter(mDataset);
                                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                            mRecyclerView.setAdapter(mAdapter);
                                                        } return;
                                                    case 2:

                                                         t = document.toObject(Animale.class);
                                                        Log.d("ciao4", t.getNome());
                                                        if (!Objects.equals(auth.getCurrentUser().getEmail(), t.getEmailProprietario())) {
                                                            mDataset.add(document.toObject(Animale.class));
                                                            // Log.d("ciao", String.valueOf(mDataset.size()));
                                                            mAdapter = new AdozioniAdapter(mDataset);
                                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                            mRecyclerView.setAdapter(mAdapter);
                                                        }   return;
                                                    case 3:
                                                            mDataset.add(document.toObject(Animale.class));
                                                            // Log.d("ciao", String.valueOf(mDataset.size()));
                                                            mAdapter = new AdozioniAdapter(mDataset);
                                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                            mRecyclerView.setAdapter(mAdapter);
                                                           return;
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
        rdbannuncigenerale.setChecked(true);
        layoutopenfiltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri.setVisibility(View.VISIBLE);
               // layoutopenfiltri.setVisibility(View.GONE);
                //btnopenFiltri.setVisibility(View.GONE);
               // layoutopenfiltri.setVisibility(View.GONE);
               // btnopenFiltri.setVisibility(View.GONE);
                bottonechiudifiltri.setVisibility(View.VISIBLE);

                //bottonechiudifiltri.setVisibility(View.VISIBLE);

            }
        });

        btnopenFiltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri.setVisibility(View.VISIBLE);
                // layoutopenfiltri.setVisibility(View.GONE);
                //btnopenFiltri.setVisibility(View.GONE);
                // layoutopenfiltri.setVisibility(View.GONE);
                btnopenFiltri.setVisibility(View.GONE);
                bottonechiudifiltri.setVisibility(View.VISIBLE);

                //bottonechiudifiltri.setVisibility(View.VISIBLE);

            }
        });

        bottonechiudifiltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri.setVisibility(View.GONE);
               // btnopenFiltri.setVisibility(View.VISIBLE);
                bottonechiudifiltri.setVisibility(View.GONE);
                btnopenFiltri.setVisibility(View.VISIBLE);

                //layoutopenfiltri.setVisibility(View.VISIBLE);
            }
        });


        rdbannuncigenerale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    mRecyclerView.setAdapter(null);

                }
                else{
                    barrachilometri.setEnabled(true);

                    tipoannunci=2;
                    mDataset.clear();
                    initDataAnnunci(tipoannunci);
                }
            }
        });


        rdbimieiAnnunci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    mRecyclerView.setAdapter(null);

                }
                else{
                    tipoannunci=1;
                   // barrachilometri.setClickable(false);
                    barrachilometri.setEnabled(false);



                    mDataset.clear();
                    initDataAnnunci(tipoannunci);
                }
            }
        });

        rdbannuncipreferiti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    mRecyclerView.setAdapter(null);

                }
                else{

                    tipoannunci=3;
                    mDataset.clear();
                    initDataAnnunci(tipoannunci);
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("tipoannunci",tipoannunci);
    }
    public void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Animale> filteredlist = new ArrayList<Animale>();

        // running a for loop to compare elements.
        for (Animale item : mDataset) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getNome().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredlist);
        }
    }

    public void sharepref()
    {
        String a= "ciao";

        SharedPreferences share= getActivity().getSharedPreferences(mypreference,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= share.edit();
        edit.putString(Name,a);
        edit.commit();

        shareprefr();


    }

    public void shareprefr()
    {
        share = getActivity().getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= share.edit();
       // edit.clear();
        //edit.commit();

        if(share.contains("preferiti")) {
           // Log.d("ciao6","ciao");

            Log.d("ciao6", String.valueOf(share.getStringSet("preferiti",null)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
