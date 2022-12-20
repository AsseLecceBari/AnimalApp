package fragments_segnalazioni;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.os.StrictMode;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import class_general.GeolocationClass;
import class_general.HttpDataHandler;
import fragments.RecyclerItemClickListener;
import fragments.main_fragment;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;
import adapter.ReportAdapter;
import model.Utente;


public class reports_fragment extends Fragment {

    private Utente utente;
    private static final int REQUEST_CHECK_SETTINGS =0x1 ;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView mRecyclerView;
    private ReportAdapter mAdapter;
    private ArrayList<Segnalazione> mDataset= new ArrayList<>();


    private ArrayList<Segnalazione> filteredlist=new ArrayList<>();
    private ArrayList<Utente> utenteList=new ArrayList<>();

    private Slider sliderReport;

    private ImageView imageSliderRep;


    private View layoutfiltri1,layoutopenfiltri1,sliderLayout;

    private Button bottonechiudifiltri1,resetButton,btnopenFiltri1;

    private MaterialSwitch attivaSlider;

    private RadioButton radioTueSegnalazioni,radioTutti,radioPreferiti;





    private   MainActivity mainActivity;
    private Toolbar main_action_bar;
    private Button prova;




    private boolean requestingLocationUpdates=true;


    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int Request_code= 101;
    double lat,lng;


    int a=0;//0 viene dalla radio tutti, 1 dal radio mie segnalazioni

    //permessi posizione


    private ActivityResultLauncher<String[]> locationPermissionRequest;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());




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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleReport);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);



        layoutfiltri1 = rootView.findViewById(R.id.layoutfiltri1);
        layoutopenfiltri1 = rootView.findViewById(R.id.layoutaprifiltri1);
        btnopenFiltri1 = rootView.findViewById(R.id.btnaprifiltri1);
        bottonechiudifiltri1 = rootView.findViewById(R.id.chiudifiltri1);
        resetButton = rootView.findViewById(R.id.resetButton);
        sliderLayout = rootView.findViewById(R.id.sliderLayout);
        attivaSlider = rootView.findViewById(R.id.attivaSlider);
        sliderReport=rootView.findViewById(R.id.sliderReport);
        imageSliderRep= rootView.findViewById(R.id.imageSliderRep);
        radioTueSegnalazioni= rootView.findViewById(R.id.radioTueSegnalazioni);
        radioTutti= rootView.findViewById(R.id.radioTutti);
        radioPreferiti= rootView.findViewById(R.id.radioPreferiti);



        radioTutti.setChecked(true);

        //controllo se l'utente e loggato altrimenti non permetto la pressione dei radio pref e tuesegnalazioni
        if (auth.getCurrentUser()==null){
            radioTueSegnalazioni.setClickable(false);
            radioPreferiti.setClickable(false);
        }

        radioTutti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    a=0;
                    mDataset.clear();
                    filteredlist.clear();
                    initDataset();
                    attivaSlider.setChecked(false);
                    sliderReport.setValue(15.0F);
                    Log.d("prova222", String.valueOf(a));
                }
            }
        });


        radioTueSegnalazioni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    a=1;
                    mDataset.clear();
                    filteredlist.clear();
                    initDatasetTueSegnalazioni();
                    attivaSlider.setChecked(false);
                    sliderReport.setValue(15.0F);
                    Log.d("prova222", String.valueOf(a));

                }
            }
        });



        attivaSlider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    getCurrentLocation();
                    sliderLayout.setVisibility(View.VISIBLE);
                }else{
                    stopLocationUpdates();
                    sliderLayout.setVisibility(View.GONE);

                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attivaSlider.setChecked(false);
                mDataset.clear();
                filteredlist.clear();

                if (radioTutti.isChecked()){
                    initDataset();
                }else{
                    radioTutti.setChecked(true);
                }

                sliderReport.setValue(15.0F);
            }
        });


        //touch listener
        sliderReport.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                getCurrentLocation();



            }


            @SuppressLint("MissingPermission")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {

               filterCoordinates(lat,lng,slider.getValue());

            }
        });


        //on change value listener
        sliderReport.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {

                sliderReport.setLabelFormatter(new LabelFormatter() {
                    @NonNull
                    @Override
                    public String getFormattedValue(float value1) {

                            return value + "km";

                    }
                });
            }
        });


        //Inizializzo l'ascoltatore al click dell'item
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Segnalazione s;
                        Log.e("pippo",filteredlist.size()+"");
                        if(filteredlist.size()==0) {

                            //Ottengo l'oggetto dalla lista in posizione "position"
                            Log.e("dataset",mDataset.size()+"sono qui");
                            s= mDataset.get(position);
                            switchTipoSegnalazione(s);
                            closeSearchView();
                            mDataset.clear();
                            filteredlist.clear();
                        }else{
                            Log.e("dataset", "sono in filtered");
                            s = filteredlist.get(position);
                            closeSearchView();
                            switchTipoSegnalazione(s);
                            filteredlist.clear();
                            mDataset.clear();
                        }

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                }));





        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
            stopLocationUpdates();

    }

    //metodo per la chiusura della barra di ricerca
    private void closeSearchView() {
        mainActivity= (MainActivity) getActivity();
        main_action_bar= mainActivity.getMain_action_bar();
        main_action_bar.collapseActionView();
    }



    //switch per l'onClick delle card
    private void switchTipoSegnalazione(Segnalazione s) {
        closeSearchView();
        switch (s.getTipo()) {
            case "Smarrimento":
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new vistaSegnalazione().newInstance(s,a)).addToBackStack(null).commit();
                break;
            case "Animale Ferito":
                //da cambiare con vistaAnimaleFerito
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_animaleInPericolo().newInstance(s,a)).addToBackStack(null).commit();
                break;
            case "Zona Pericolosa":
                //da cambiare con vistaRitrovamento
                 getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_zonaPericolosa().newInstance(s,a)).addToBackStack(null).commit();
                 break;
            case "News":
                //da cambiare con vistaRitrovamento
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new fragment_vista_news().newInstance(s,a)).addToBackStack(null).commit();
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

    }



    private void initDatasetTueSegnalazioni() {

        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();

        //auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");

        //tolto perche con questo non mostrava le segnalazioni se non sei loggato
         if(auth.getCurrentUser()!=null) {

             segnalazioniRef.whereEqualTo("emailSegnalatore", auth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     if (task.isSuccessful()) {
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             //Salvare animale in un array con elementi oggetto animale
                             mDataset.add(document.toObject(Segnalazione.class));
                             //Passo i dati presi dal database all'adapter

                         }
                         mAdapter = new ReportAdapter(mDataset);
                         // Setto l'AnimalAdaper(mAdapter) come l'adapter per la recycle view
                         mRecyclerView.setAdapter(mAdapter);
                     } else {
                         Log.d("ERROR", "Error getting documents: ", task.getException());
                     }
                 }
             });
         }

    }

    //per usare coordinate di registrazione
    private void trovaUtente(double raggio) {


        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db=FirebaseFirestore.getInstance();

       // auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("utenti");

        //tolto perche con questo non mostrava le segnalazioni se non sei loggato
        if(auth.getCurrentUser()!=null) {

            segnalazioniRef.whereEqualTo("email", auth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {


                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Salvare animale in un array con elementi oggetto animale

                            //Passo i dati presi dal database all'adapter
                            utente=document.toObject(Utente.class);
                        }

                        double latitudine= Double.parseDouble(utente.getIndirizzo().get("latitudine"));
                        double longitudine= Double.parseDouble(utente.getIndirizzo().get("longitudine"));
                       //TODO inserire le coordinate della registrazione dell'utente
                       filterCoordinates(latitudine ,longitudine,raggio);



                    } else {
                        Log.d("ERROR", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }




    //filtro per lo slider
    public void filterCoordinates(double myLat,double myLng,double raggio) {

        // creating a new array list to filter our data.
        filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Segnalazione item : mDataset) {
            // checking if the entered string matched with any item of our recycler view.

            //dove latitudine e longitudine è la mia posizione + i km scelti
            if (distance(myLat,myLng, item.getLatitudine(), item.getLongitudine())<= raggio) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {

            // if no item is added in filtered list we are

            mAdapter.filterList(filteredlist);

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            mAdapter.filterList(filteredlist);
        }

    }


    //filtro per la barra di ricerca
    public void filter(String text) {
        // creating a new array list to filter our data.

        filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Segnalazione item : mDataset) {

            // checking if the entered string matched with any item of our recycler view.

            if (item.getTitolo().toLowerCase().contains(text.toLowerCase()) || item.getTipo().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are

                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {

            // if no item is added in filtered list we are

            mAdapter.filterList(filteredlist);

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

                btnopenFiltri1.setVisibility(View.GONE);
                bottonechiudifiltri1.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);


            }
        });

        bottonechiudifiltri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutfiltri1.setVisibility(View.GONE);

                bottonechiudifiltri1.setVisibility(View.GONE);
                btnopenFiltri1.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.GONE);


            }
        });


    }

    //gesitone permessi



/*
    public void showAlertDialog() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext());
        alertDialogBuilder.setMessage("Per poter utilizzare questa applicazione con tutte le sue funzionalità, è consigliato accettare i permessi");
        alertDialogBuilder.setPositiveButton("Ho capito",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        locationPermissionRequest.launch(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        });


                    }
                });

        alertDialogBuilder.setNegativeButton("Magari più tardi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/






    /** calculates the distance between two locations in Kilometer, with the HAVERSINE FORmULa*/
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6378;//raggio della terra

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in KILOMETER
    }



    @SuppressLint("MissingPermission")
    private  void getCurrentLocation(){


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},Request_code);

            return;
        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        putOnGPS(locationRequest);

        locationCallback=new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Toast.makeText(getContext(), "location result is= ", Toast.LENGTH_SHORT).show();
                if(locationResult==null){
                  //  Toast.makeText(getContext(),"Current location is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Location location:locationResult.getLocations()){
                    if(locationResult!=null){

                       // Toast.makeText(getContext()," "+ locationResult, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        };


        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
        Task<Location> task= fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location !=null){
                    lat=location.getLatitude();
                    lng=location.getLongitude();

                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (Request_code){

            case Request_code:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }

        }


    }

    public void putOnGPS(LocationRequest locationRequest){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }



    private void stopLocationUpdates(){
        if (locationCallback!=null) {

            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

}