package fragments_segnalazioni;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import class_general.GetCoordinates;
import class_general.HttpDataHandler;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.main_fragment;
import io.grpc.okhttp.internal.proxy.Request;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;
import model.Utente;


public class smarrimento_fragments extends Fragment {


    private FirebaseAuth auth;


    private static final String ARG_PARAM1 = "obj";
    private Animale a;

    CircleImageView imgAnimaleSmarrito;
    TextView nomeAnimaleSmarrito,genereAnimaleSmarrito,specieANimaleSmarrito,sessoAnimaleSmarrito,nascitaAnimaleSmarrito;
    TextInputLayout descrizioneTextLayout,indirizzoTextLayout;
    TextInputEditText descrizioneEditText,indirizzoEditText,titoloSmarrimento;
    FloatingActionButton confermaSmarrimento;
    private FirebaseFirestore db;


    private Uri file;

    private FirebaseStorage storage;
    private StorageReference storageRef;




    String address;
    double lat,lng;


    //Autocompleate
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("PLACE", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("PLACEeRROR", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }







    public smarrimento_fragments() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            a= (Animale) getArguments().getSerializable(ARG_PARAM1);
        }

        auth= FirebaseAuth.getInstance();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


      View rootView=inflater.inflate(R.layout.fragment_smarrimento_fragments, container, false);

        db=FirebaseFirestore.getInstance();

      imgAnimaleSmarrito=rootView.findViewById(R.id.imgAnimaleSmarrito);

      nomeAnimaleSmarrito=rootView.findViewById(R.id.nomeAnimaleSmarrito);
      genereAnimaleSmarrito=rootView.findViewById(R.id.genereAnimaleSmarrito);
      specieANimaleSmarrito=rootView.findViewById(R.id.specieANimaleSmarrito);
      sessoAnimaleSmarrito=rootView.findViewById(R.id.sessoAnimaleSmarrito);
      nascitaAnimaleSmarrito=rootView.findViewById(R.id.nascitaAnimaleSmarrito);
      titoloSmarrimento=rootView.findViewById(R.id.titoloSmarrimento);
      descrizioneTextLayout=rootView.findViewById(R.id.descrizioneTextLayout);

      descrizioneEditText=rootView.findViewById(R.id.descrizioneEditText);


      confermaSmarrimento=rootView.findViewById(R.id.confermaSmarrimento);

        if(a!= null) {
            nomeAnimaleSmarrito.setText(a.getNome());
            genereAnimaleSmarrito.setText(a.getGenere());
            specieANimaleSmarrito.setText(a.getSpecie());
            sessoAnimaleSmarrito.setText(a.getSesso());
            nascitaAnimaleSmarrito.setText(a.getDataDiNascita());


            //setto immagine
            // setto l'immagine dell'animale
            storage= FirebaseStorage.getInstance();
            storageRef=storage.getReference();

            storageRef.child(a.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(imgAnimaleSmarrito.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAnimaleSmarrito);
                }
            });

            //Autocomplete Indirizzo

            if (!Places.isInitialized()) {
                Places.initialize(getActivity().getApplicationContext(), "AIzaSyDlX6obgKqLyk_7MU5HD6hKzZeWQo0xEaA", Locale.US);
            }

            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);




            // Start the autocomplete intent.
           /* Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);*/

            // Specify the types of place data to return.

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.i("place", "Place: " + place.getName() + ", " + place.getId());
                    address=place.getName();
                    LatLng latlng=place.getLatLng();
                    lat=latlng.latitude;
                    lng=latlng.longitude;


                }


                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i("placeerror", "An error occurred: " + status);
                }
            });







            confermaSmarrimento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i=0;
                     String tipo="Smarrimento";


                     Random idSegnalazione = new Random();
                     String descrizione=descrizioneEditText.getText().toString();
                    String titolo=titoloSmarrimento.getText().toString();

                     //da prendere la foto
                     String data;
                     String urlFoto=a.getFotoProfilo();
                    SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                     data = dateFor.format(new Date());



                    if (TextUtils.isEmpty(descrizione)){
                        descrizioneEditText.setError("Inserire una descrizione");
                        i=1;
                    }
                    if (TextUtils.isEmpty(titolo)){
                        titoloSmarrimento.setError("Inserire un Titolo");
                        i=1;
                    }
                    if (TextUtils.isEmpty(address)){
                        Toast.makeText(getContext(), "Inserire un indirizzo di ritrovamento", Toast.LENGTH_SHORT).show();
                        i=1;
                    }
                    if (i==0) {
                        Segnalazione s1 = new Segnalazione(auth.getCurrentUser().getEmail(), titolo, tipo, a.getIdAnimale(), idSegnalazione.nextInt() + "", descrizione, lat, lng, data, urlFoto, " ");
                        db.collection("segnalazioni").document(s1.getIdSegnalazione()).set(s1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                                getActivity().getSupportFragmentManager().popBackStack();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Controllare di aver inserito tutti i campi e riprovare", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }










        return rootView;
    }

    public Fragment newInstance(Animale a) {
        smarrimento_fragments fragment = new smarrimento_fragments();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, a);
        fragment.setArguments(args);
        //Log.e("ciao",param1+"");
        return fragment;

    }



    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }





}