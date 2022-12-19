package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.internal.MaterialCheckable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import adapter.AdozioniAdapter;
import adapter.ReportAdapter;
import class_general.OnSwipeListener;
import class_general.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.AdozioneActivity;
import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Persona;
import model.Preferenze;
import model.Utente;


public class adoptions_fragment extends Fragment {

    private FirebaseAuth auth;
    private Preferenze preferenze;
    private SharedPreferences share;
    private FirebaseFirestore db;
    private  RecyclerView mRecyclerView;
    protected RecyclerView recyclemieadozioni;

    private  ArrayList<Animale> mDataset= new ArrayList<>();
    private  AdozioniAdapter mAdapter=new AdozioniAdapter(mDataset);

    private LinearLayout paginalogin;
    private View btnaccesso;
    private RadioButton rdbimieiAnnunci;
    private RadioButton rdbannuncigenerale;
    private RadioButton rdbannuncipreferiti;
    private View layoutfiltri;
    private View layoutopenfiltri;
    private View btnopenFiltri;
    private View bottonechiudifiltri;
    private View eliminaAnnuncio;
    private View layoutPreferiti;
    private View listaAnimali;
    private TextView numeroAnnPreferiti;
    private int tipoannunci=2;
    private View barrachilometri;
    private ArrayList <Adozione> adozione= new ArrayList<>();
    private ArrayList<Persona> proprietari = new ArrayList<>();
    private ArrayList<Animale> filteredlist =new ArrayList<>();
    private MainActivity mainActivity;
    private Toolbar main_action_bar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        filtri();
        try {
            listnerAdozioni();
        }catch(Exception e){

        }



   /*    mRecyclerView.setOnTouchListener(new OnSwipeListener(getContext()) {


            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeBottom() {
                mDataset.clear();
                initDataAnnunci(tipoannunci);
            }

        });*/








        View aggiungiAdozione = getView().findViewById(R.id.aggiungiAdozione);
        aggiungiAdozione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSearchView();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new aggiungi_adozione_fragment()).addToBackStack(null).commit();
            }
        });

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




        btnaccesso = rootView.findViewById(R.id.btnLogin);
        rdbimieiAnnunci = rootView.findViewById(R.id.radioImieiannunci);
        rdbannuncigenerale = rootView.findViewById(R.id.radioannunciesterni);
        rdbannuncipreferiti = rootView.findViewById(R.id.radioannunciPreferiti);
        layoutfiltri = rootView.findViewById(R.id.layoutfiltri);
        layoutopenfiltri = rootView.findViewById(R.id.layoutaprifiltri);
        btnopenFiltri = rootView.findViewById(R.id.btnaprifiltri);
        bottonechiudifiltri = rootView.findViewById(R.id.chiudifiltri);
        listaAnimali= rootView.findViewById(R.id.listaAnimali);


        barrachilometri=rootView.findViewById(R.id.barrachilometri);
        numeroAnnPreferiti=rootView.findViewById(R.id.numeroannpref);
        if(auth.getCurrentUser()!=null) {
            initDataPreferiti();
        }

        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        recyclemieadozioni= rootView.findViewById(R.id.recyclemieadozioni);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleadoption);
        recyclemieadozioni.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return rootView;
    }




    public void initDataAnnunci(int tip){
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference adozioniRef = db.collection("adozioni");
        CollectionReference animali = db.collection("animali");
        CollectionReference preferenze = db.collection("preferenze");
        CollectionReference utenteref= db.collection("utenti");


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
                                                        if(auth.getCurrentUser()!=null) {
                                                            t = document.toObject(Animale.class);
                                                            if (Objects.equals(auth.getCurrentUser().getEmail(), t.getEmailProprietario())) {


                                                                mDataset.add(document.toObject(Animale.class));


                                                                mAdapter = new AdozioniAdapter(mDataset, 1);
                                                                // mAdapter.eliminaAnnuncio();

                                                                // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                                                mRecyclerView.setAdapter(mAdapter);
                                                                if (mAdapter != null) {
                                                                    onItemClick();


                                                                }


                                                            }
                                                            return;
                                                        }
                                                    case 2:
                                                        if(auth.getCurrentUser()!=null) {
                                                            t = document.toObject(Animale.class);
                                                            Log.d("ciao4", t.getNome());
                                                            if (!Objects.equals(auth.getCurrentUser().getEmail(), t.getEmailProprietario())) {
                                                                mDataset.add(document.toObject(Animale.class));

                                                                Log.d("ciao13", String.valueOf(mDataset.get(mDataset.size()-1).getEmailProprietario()));

                                                                utenteref.whereEqualTo("email",mDataset.get(mDataset.size()-1).getEmailProprietario()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            for (QueryDocumentSnapshot document3 : task.getResult()) {
                                                                                proprietari.add( document3.toObject(Persona.class));
                                                                                mAdapter = new AdozioniAdapter(mDataset, 2,proprietari);
                                                                                mRecyclerView.setAdapter(mAdapter);
                                                                                if (mAdapter != null) {
                                                                                    onItemClick();
                                                                                }

                                                                            }

                                                                        }

                                                                    }
                                                                });









                                                            }


                                                            return;
                                                        }
                                                    case 3: if(auth.getCurrentUser()!=null) {

                                                        preferenze.whereEqualTo("emailUtente", auth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document2 : task.getResult()) {
                                                                        Preferenze ad = document2.toObject(Preferenze.class);

                                                                        for (int a = 0; a < ad.getAdozioni().size(); a++) {
                                                                            if (Objects.equals(ad.getAdozioni().get(a), temporanea.getIdAdozione())) {
                                                                                Log.d("ciao10", "c");
                                                                                mDataset.add(document.toObject(Animale.class));

                                                                                utenteref.whereEqualTo("emailUtente",mDataset.get(mDataset.size()-1)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            for (QueryDocumentSnapshot document3 : task.getResult()) {
                                                                                                proprietari.add( document.toObject(Persona.class));

                                                                                            }

                                                                                        }

                                                                                    }
                                                                                });
                                                                                mAdapter = new AdozioniAdapter(mDataset, 2,proprietari);
                                                                                mRecyclerView.setAdapter(mAdapter);
                                                                                if (mAdapter != null) {
                                                                                    onItemClick();
                                                                                }

                                                                            }
                                                                        }
                                                                    }

                                                                }

                                                            }
                                                        });
                                                    }
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

       if (tipoannunci== 1) {

           rdbimieiAnnunci.setChecked(true);
       }
       else if(tipoannunci==2) {


           rdbannuncigenerale.setChecked(true);
       } else if(tipoannunci==3) {


           rdbannuncipreferiti.setChecked(true);
       }



       // rdbannuncigenerale.setChecked(true);
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
                    Log.d("ciao11","2");
                    barrachilometri.setEnabled(true);

                    tipoannunci=2;
                    mDataset.clear();
                    mAdapter= null;
                    mRecyclerView.setAdapter(null);
                    initDataAnnunci(tipoannunci);
                }
            }
        });


        rdbimieiAnnunci.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    recyclemieadozioni.setAdapter(null);

                }
                else{
                    tipoannunci=1;
                    Log.d("ciao11","1");

                   // barrachilometri.setClickable(false);
                    barrachilometri.setEnabled(false);



                    mDataset.clear();
                    mAdapter=null;
                    mRecyclerView.setAdapter(null);
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
                    Log.d("ciao11","3");
                    tipoannunci=3;
                    mDataset.clear();
                    mAdapter=null;
                    mRecyclerView.setAdapter(null);
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
        filteredlist = new ArrayList<Animale>();

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
            mAdapter.filterList(filteredlist);

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredlist);
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    public void initDataPreferiti() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db = FirebaseFirestore.getInstance();

        auth= FirebaseAuth.getInstance();

        CollectionReference preferenzeRef = db.collection("preferenze");
        CollectionReference adozioniref= db.collection("adozioni");



        Query query= preferenzeRef.whereEqualTo("emailUtente",auth.getCurrentUser().getEmail());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        preferenze = document.toObject(Preferenze.class);



                    }


                    if ( preferenze!= null ) {
                        checkPreferenze(preferenze);



                        } else {
                            rdbannuncipreferiti.setEnabled(false);

                            numeroAnnPreferiti.setText("0");

                        }




                }
            }
        });


    }


    public void onItemClick()
    {


            mAdapter.setOnClickListener(new AdozioniAdapter.OnClickListener() {

                @SuppressLint("SuspiciousIndentation")
                @Override
                public void onitemClick(View view, int position) {
                    int poszione_adozione = 0;
                    int posizione_proprietario=0;//la poszione dell'adozione non è uguale a quella dell'animale perche l'adapter è dell'animale


                    Animale animale;
                    if(filteredlist.size()==0) {

                        //Ottengo l'oggetto dalla lista in posizione "position"
                        animale = mDataset.get(position);
                        closeSearchView();
                        mDataset.clear();
                        filteredlist.clear();
                    }else{

                        Log.e("filteredlist", filteredlist+"");
                        animale = filteredlist.get(position);}
                    closeSearchView();
                    mDataset.clear();
                    filteredlist.clear();



                    for(int a=0; a< adozione.size(); a++)
                    {
                        if(Objects.equals(adozione.get(a).getIdAnimale(), animale.getIdAnimale()))
                        {
                            poszione_adozione=a;
                        }
                    }
                    Adozione ad= adozione.get(poszione_adozione);

                    for(int a=0; a<proprietari.size(); a++)
                    {
                        if(Objects.equals(proprietari.get(a).getEmail(),animale.getEmailProprietario()))
                        {
                          posizione_proprietario=a;

                            Log.d("ciao18",proprietari.get(posizione_proprietario).getEmail());
                        }
                    }

                    Persona b= proprietari.get(posizione_proprietario);


                    Intent intent = new Intent(getContext(), AdozioneActivity.class);
                    intent.putExtra("animale", animale);
                    intent.putExtra("adozione",ad );
                    intent.putExtra("proprietario",b);
                    startActivity(intent);


                }

                @Override
                public void oneliminaClick(View view, int position) {

                   db= FirebaseFirestore.getInstance();


                    Animale animale = mDataset.get(position);


                    int poszione_adozione = 0;
                    for(int a = 0; a< adozione.size(); a++)
                    {
                        if(Objects.equals(adozione.get(a).getIdAnimale(), animale.getIdAnimale()))
                        {
                            poszione_adozione=a;
                        }
                    }
                    Adozione ad= adozione.get(poszione_adozione);

                    db.collection("adozioni").document(ad.getIdAdozione())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ciao10", String.valueOf(position));
                                   // Log.d("ciao10", String.valueOf(mDataset.size()));







                                 if(mDataset.size()==1)
                                 {

                                     mRecyclerView.setAdapter(null);
                                 }
                                 else
                                 {
                                     mDataset.remove(position);

                                    mAdapter.aggiornadataset(mDataset);
                                    mRecyclerView.setAdapter(mAdapter);
                                 }
                                }
                            });


                }
            });

    }

public void listnerAdozioni()
{
    auth= FirebaseAuth.getInstance();
    final Query docRef = db.collection("adozioni");
    docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot snapshot,
                            @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Log.d("ciao12","ciaot");

                return;
            }



            assert snapshot != null;
            for (DocumentChange dc : snapshot.getDocumentChanges()) {


                switch (dc.getType()) {
                    case ADDED:

                        break;
                    case MODIFIED:

                      //  initDataPreferiti();
                        break;
                    case REMOVED:


                            for(int a=0; a<adozione.size();a++)
                            {
                                if(Objects.equals(adozione.get(a).getIdAdozione(), dc.getDocument().getId()))
                                {
                                    adozione.remove(a);
                                }
                            }

                            initDataPreferiti();






                        break;
                }
            }

        }



    });
}

    public void writeDataPreferiti(Preferenze preferenze) {

       db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();



        db.collection("preferenze").document(auth.getCurrentUser().getEmail()).set(preferenze).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {


                }
            }
        });






    }

    public void checkPreferenze(Preferenze preferenze)
    {

        for(int a =0; a<preferenze.getAdozioni().size();a++)
        {
            int cont=0;
            for(int b=0; b<adozione.size();b++)
            {
                if(Objects.equals(preferenze.getAdozioni().get(a), adozione.get(b).getIdAdozione()))
                {
                    cont++;
                }


            }
            if (cont==0)
            {
                preferenze.getAdozioni().remove(a);
                if(preferenze.getAdozioni().size()==0)
                {
                    preferenze.getAdozioni().add(null);
                }
                writeDataPreferiti(preferenze);

            }
        }


        if( preferenze.getAdozioni().get(0) != null) {
            rdbannuncipreferiti.setEnabled(true);
            String s = String.valueOf(preferenze.getAdozioni().size());
            numeroAnnPreferiti.setText(s);
        }
        else{
            rdbannuncipreferiti.setEnabled(false);

            numeroAnnPreferiti.setText("0");
        }
    }

    private void closeSearchView() {
        mainActivity= (MainActivity) getActivity();
        main_action_bar= mainActivity.getMain_action_bar();
        main_action_bar.collapseActionView();
    }





}
