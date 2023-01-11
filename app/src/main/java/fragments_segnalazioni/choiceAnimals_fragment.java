package fragments_segnalazioni;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import adapter.AnimalAdapter;
import fragments.RecyclerItemClickListener;
import fragments_adozioni.aggiungi_annuncio_Adozione;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;

public class choiceAnimals_fragment extends Fragment {
    private int flag = 0;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Animale a;

    protected RecyclerView mRecyclerView;
    protected AnimalAdapter mAdapter;

    protected ArrayList<Animale> mDataset= new ArrayList<>();
    private Toolbar main_action_bar;
    public choiceAnimals_fragment() {
        // Required empty public constructor
    }

    public choiceAnimals_fragment(int a) {
        flag = 1;
    }

    public choiceAnimals_fragment(int a, int b) {
        flag = 2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDataset.clear();
        if(flag == 2){
            InitData();
        }else{
            initDataset();
        }
        View rootView = inflater.inflate(R.layout.fragment_choice_animals_fragment, container, false);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Seleziona un animale");
        main_action_bar.getMenu().clear();
        main_action_bar.setNavigationIcon(R.drawable.back);
        main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });
        main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);

        //da implementare la parte dei non loggati con il fragment creata da enrico
        auth=FirebaseAuth.getInstance();



        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleChoiceAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        a = mDataset.get(position);
                        if(flag == 0){
                            // smarrimento
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new smarrimento_fragments().newInstance(a)).addToBackStack(null).commit();
                        }else if (flag == 1){
                            // raccolta fondi
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungiRaccoltaFondi(a)).addToBackStack(null).commit();
                        }else{
                            //creo l'adozione
                            Random idAdozione=new Random();
                            String id= String.valueOf(idAdozione.nextInt());
                            SimpleDateFormat dataFor= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                            String data= dataFor.format(new Date());
                            Adozione adozione =new Adozione(a.getIdAnimale(),id,a.getEmailProprietario(),data,"");

                            // animale da mandare in adozione
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungi_annuncio_Adozione().newInstance(adozione, a)).addToBackStack(null).commit();
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );

        return rootView;
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
                    }
                }
            });
        }
    }

    public void InitData() {
        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        CollectionReference animali=db.collection("animali");
        CollectionReference adozioni=db.collection("adozioni");

        if(auth.getCurrentUser()!=null) {
            Query query = animali.whereEqualTo("emailProprietario", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Animale temporaneo= document.toObject(Animale.class);

                            Query query = adozioni.whereEqualTo("idAnimale", document.getId());

                            adozioni.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        int contatore=0;
                                        for (QueryDocumentSnapshot document1 : task.getResult()) {
                                            Adozione a= document1.toObject(Adozione.class);
                                            if(Objects.equals(a.getIdAnimale(), document.getId()))
                                            {
                                                contatore++;


                                            }


                                        }
                                        if(contatore==0)
                                        {
                                            mDataset.add(document.toObject(Animale.class));
                                            Log.d("ciao", String.valueOf(mDataset.size()));
                                            mAdapter = new AnimalAdapter(mDataset);
                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                            mRecyclerView.setAdapter(mAdapter);
                                        }

                                    }
                                }
                            });



                        }
                    }
                }
            });
        }

    }

}