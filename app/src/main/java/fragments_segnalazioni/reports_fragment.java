package fragments_segnalazioni;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import class_general.GeolocationClass;
import class_general.HttpDataHandler;
import fragments.RecyclerItemClickListener;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;
import adapter.ReportAdapter;


public class reports_fragment extends Fragment {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected RecyclerView mRecyclerView;
    protected static ReportAdapter mAdapter;
    protected static ArrayList<Segnalazione> mDataset= new ArrayList<>();

    private String id;

    private ArrayList<Segnalazione> filteredlist =new ArrayList<>();
    private double lat5,lng5;
    private GeolocationClass g5;

    private Slider sliderReport;
    private ImageView imageSliderRep;


    private View layoutfiltri1;
    private View layoutopenfiltri1;
    private View btnopenFiltri1;
    private View bottonechiudifiltri1;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }





    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        //questo è il floating button
        View aggiungiSegnalazione = getView().findViewById(R.id.aggiungiSegnalazione);
        aggiungiSegnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new aggiungi_segnalazione_fragment()).addToBackStack(null).commit();
            }
        });

        filtri();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataset.clear();
        initDataset();
        View rootView = inflater.inflate(R.layout.fragment_reports_fragment, container, false);
        StrictMode.setThreadPolicy(policy);


        layoutfiltri1 = rootView.findViewById(R.id.layoutfiltri1);
        layoutopenfiltri1 = rootView.findViewById(R.id.layoutaprifiltri1);
        btnopenFiltri1 = rootView.findViewById(R.id.btnaprifiltri1);
        bottonechiudifiltri1 = rootView.findViewById(R.id.chiudifiltri1);






        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleReport);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sliderReport=rootView.findViewById(R.id.sliderReport);
        imageSliderRep= rootView.findViewById(R.id.imageSliderRep);
        imageSliderRep.setClickable(true);
        imageSliderRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //touch listener
        sliderReport.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {

            }
        });
        //on change value listener
        sliderReport.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                sliderReport.setLabelFormatter(new LabelFormatter() {
                    @NonNull
                    @Override
                    public String getFormattedValue(float value1) {
                        return value+"km";
                    }
                });
            }
        });


        //Inizializzo l'ascoltatore al click dell'item
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Segnalazione s;
                        if(filteredlist.size()==0) {
                            //Ottengo l'oggetto dalla lista in posizione "position"
                            s= mDataset.get(position);
                        }else{
                            Log.e("filteredlist", filteredlist+"");
                            s = filteredlist.get(position);}


                        switch (s.getTipo()) {
                            case "Smarrimento":
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new vistaSegnalazione().newInstance(s)).addToBackStack(null).commit();
                                break;
                            case "Animale Ferito":
                                //da cambiare con vistaAnimaleFerito
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_animaleInPericolo().newInstance(s)).addToBackStack(null).commit();
                                break;
                            case "Zona Pericolosa":
                                //da cambiare con vistaRitrovamento
                                 getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_zonaPericolosa().newInstance(s)).addToBackStack(null).commit();
                                 break;
                            case "News":
                                //da cambiare con vistaRitrovamento
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_news().newInstance(s)).addToBackStack(null).commit();
                                break;
                            case "Raccolta Fondi":
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_raccoltaFondi(s)).addToBackStack(null).commit();
                                break;
                            case "Ritrovamento":
                                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_raccoltaFondi(s)).addToBackStack(null).commit();
                                Toast.makeText(getContext(), "Fare vista ritrovamento", Toast.LENGTH_SHORT).show();
                                break;


                            default:
                                break;

                        }

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );





        // Inflate the layout for this fragment
        return rootView;
    }

    private void initDataset() {

        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");

        //tolto perche con questo non mostrava le segnalazioni se non sei loggato
       // if(auth.getCurrentUser()!=null){

        segnalazioniRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Salvare animale in un array con elementi oggetto animale
                        mDataset.add(document.toObject(Segnalazione.class));
                        //Passo i dati presi dal database all'adapter

                    }
                    mAdapter = new ReportAdapter(mDataset);
                    // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                    mRecyclerView.setAdapter(mAdapter);



                }else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }



            }
        });

       // }



    }
    public void filter(String text) {
        // creating a new array list to filter our data.
        filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Segnalazione item : mDataset) {
            // checking if the entered string matched with any item of our recycler view.

            //TODO CAMBIARE CON getTitolo vedere se è corretto
            if (item.getTitolo().toLowerCase().contains(text.toLowerCase()) || item.getTipo().toLowerCase().contains(text.toLowerCase())) {
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



    public void filtri(){

        btnopenFiltri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri1.setVisibility(View.VISIBLE);
                // layoutopenfiltri.setVisibility(View.GONE);
                //btnopenFiltri.setVisibility(View.GONE);
                // layoutopenfiltri.setVisibility(View.GONE);
                btnopenFiltri1.setVisibility(View.GONE);
                bottonechiudifiltri1.setVisibility(View.VISIBLE);

                //bottonechiudifiltri.setVisibility(View.VISIBLE);

            }
        });

        bottonechiudifiltri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri1.setVisibility(View.GONE);
                // btnopenFiltri.setVisibility(View.VISIBLE);
                bottonechiudifiltri1.setVisibility(View.GONE);
                btnopenFiltri1.setVisibility(View.VISIBLE);

                //layoutopenfiltri.setVisibility(View.VISIBLE);
            }
        });


    }

}