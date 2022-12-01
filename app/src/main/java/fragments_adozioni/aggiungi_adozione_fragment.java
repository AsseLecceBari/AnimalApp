package fragments_adozioni;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Toast;
import android.widget.Toolbar;

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

import adapter.AggiungiAnimaleAdapter;
import class_general.RecyclerItemClickListener;
import fragments_mieiAnimali.aggiungiAnimaleFragment;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import fragments.main_fragment;

public class aggiungi_adozione_fragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected AggiungiAnimaleAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Animale> mDataset= new ArrayList<>();
    private Checkable checkbox;
    private final ArrayList <String> adozioni = new ArrayList<>();
    private View botton;
    private View card;
    private ArrayList<Adozione> animaliAdozione =  new ArrayList<>();
    private  int contatore=0;
    private androidx.appcompat.widget.Toolbar main_action_bar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_adozione_fragment, container, false);
        main_action_bar = getActivity().findViewById(R.id.main_action_bar);


        main_action_bar.setTitle("Seleziona Animali");


        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
        }
        main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);
        card= rootView.findViewById(R.id.cardAnimal);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleaggiungiAdozione);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataset.clear();
        InitData();

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(main_action_bar.getMenu()!=null) {
            main_action_bar.inflateMenu(R.menu.menu_bar_main);
            main_action_bar.setTitle("AnimalApp");
        }
    }


    @Override
    public void onResume() {
        super.onResume();


        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungiAnimaleFragment()).addToBackStack(null).commit();
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        checkbox= view.findViewById(R.id.checkBoxadozioni);
                        if(!checkbox.isChecked()){
                            Random idAdozione=new Random();
                            SimpleDateFormat dataFor= new SimpleDateFormat("dd-MM-yyyy");
                            String data= dataFor.format(new Date());
                            animaliAdozione.add(new Adozione(mDataset.get(position).getIdAnimale(),idAdozione.toString(),mDataset.get(position).getEmailProprietario(),data));
                            checkbox.setChecked(true);
                        }
                        else if(checkbox.isChecked()) {
                            checkbox.setChecked(false);
                            //se abbiamo gia aggiunto l'animale nell'array lo elimina
                            for (int a = 0; a < animaliAdozione.size(); a++) {
                                if (Objects.equals(animaliAdozione.get(a).getIdAnimale(), mDataset.get(position).getIdAnimale())){
                                 animaliAdozione.remove(a);

                                }
                            }
                        }
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );

     aggiungiAnimaliSelezionati();
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

                            Query query = adozioni.whereNotEqualTo("emailProprietario", auth.getCurrentUser().getEmail());

                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        contatore=0;

                                        for (QueryDocumentSnapshot document1 : task.getResult()) {


                                            //   if (document.getId().equals(document1.getId())) {
                                            contatore += 1;
                                            Log.d("ciao1", document1.getId());//faccio query per verificare se l'animale è gia in adozione


                                            // }

                                            //  }
                                            //  if(contatore==0){
                                            mDataset.add(document.toObject(Animale.class));
                                            Log.d("ciao", String.valueOf(mDataset.size()));
                                            mAdapter = new AggiungiAnimaleAdapter(mDataset);
                                            // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                                            mRecyclerView.setAdapter(mAdapter);
                                        }
                                        //}


                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

    }

    public void aggiungiAnimaliSelezionati() {
        botton= getView().findViewById(R.id.Btnaggiungiadozioni);
        botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(animaliAdozione.size()>0) {//controllo se ha selezionato almeno un animale
                    for (int a = 0; a < animaliAdozione.size(); a++) {
                        //Adozione adozione = new Adozione();
                        db.collection("adozioni").document(animaliAdozione.get(a).getIdAdozione()).set(animaliAdozione.get(a));

                    }

                    if(animaliAdozione.size()>1) {
                        Toast.makeText(getActivity(), R.string.AnimaliAggiungiInBachecaAdozioni, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), R.string.AnimaleAggiuntoInBachecaAdozioni, Toast.LENGTH_LONG).show();

                    }
                    getActivity().onBackPressed();

                }
                else{
                    Toast.makeText(getActivity(), R.string.SelezioneVuota, Toast.LENGTH_SHORT).show();
                }


                animaliAdozione.clear();//elimino tutti gli animali selezionati
            }
        });

    }
}