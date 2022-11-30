package fragments_segnalazioni;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import class_general.GetCoordinates;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class animale_in_pericolo_fragments extends Fragment {

    ImageView imgAnimaleInPericolo;
    Button scattaFotoButton;
    TextInputEditText etDescrizioneAnimaleFerito;
    FloatingActionButton confermaAnimaleFerito;

    private FirebaseFirestore db;

    private Uri file;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private final int PHOTO_REQUEST_CODE=1;

    String address;
    double lat,lng;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    //intent per poter ricevere il risultato dalla fotocamera e settare l'immagine
    ActivityResultLauncher<Intent> photoResult= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
         if(result.getResultCode() == Activity.RESULT_OK){
            Intent data=result.getData();

             Bitmap bp = (Bitmap) data.getExtras().get("data");
             imgAnimaleInPericolo.setImageBitmap(bp);
            }
        }
    });

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

  public animale_in_pericolo_fragments(){

  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_animale_in_pericolo_fragments, container, false);

        db=FirebaseFirestore.getInstance();

        imgAnimaleInPericolo=rootView.findViewById(R.id.imgAnimaleInPericolo);
        scattaFotoButton=rootView.findViewById(R.id.scattaFotoButton);
        etDescrizioneAnimaleFerito=rootView.findViewById(R.id.etDescrizioneAnimaleFerito);

        confermaAnimaleFerito=rootView.findViewById((R.id.confermaAnimaleFerito));



        //Intent per poter avviare la fotocamera nell'app
        scattaFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                photoResult.launch(photoIntent);

            }
        });

        //Autocomplete Indirizzo

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyBcUs-OmuzIiP9WP_DShttueADR-GqvSwk", Locale.US);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);


        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("place", "Place: " + place.getName() + ", " + place.getId());
                address=place.getName();

            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("placeerror", "An error occurred: " + status);
            }
        });


        //bug: se non ce una foto crusha per nel reportadapter abbiamo getUrlFoto da capire bene come si potrebbe bypassare
        confermaAnimaleFerito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipo="animaleFerito";

                Random idSegnalazione=new Random();
                String descrizione=etDescrizioneAnimaleFerito.getText().toString();



                SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                String data= dateFor.format(new Date());


                //todo: da prendere la foto
                String urlFoto="";

                //creo l'oggetto per effettuare la geocodifica passandogli le variabili da riempire e l'indirizzo preso dall'autocomplet
                GetCoordinates geocoder= new GetCoordinates(lat,lng,address);
                //prendo le coordinate dalle variabili dell'oggetto
                lat=geocoder.getLat();
                lng=geocoder.getLng();

                Segnalazione s1=new Segnalazione(tipo,"",idSegnalazione.nextInt()+"",descrizione,lat,lng,data,urlFoto," ");
                db.collection("segnalazioni").document(s1.getIdSegnalazione()).set(s1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        //da attivare una volta salvata la foto
                        // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new reports_fragment()).addToBackStack(null).commit();

                    }
                });



            }
        });

        return rootView;
    }



}