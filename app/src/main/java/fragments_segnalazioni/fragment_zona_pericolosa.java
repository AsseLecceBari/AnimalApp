package fragments_segnalazioni;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import class_general.GetCoordinates;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class fragment_zona_pericolosa extends Fragment {



    private FirebaseAuth auth;
    ImageView imgZonaPericolosa;
    Button scattaFotoZona;
    TextInputEditText descrzioneZonaPericolosa;
    TextInputEditText titoloZonaPericolosa;
    FloatingActionButton confermaZonaPericolosa;
    private Toolbar main_action_bar;
    private FirebaseFirestore db;

    private File file;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private final int PHOTO_REQUEST_CODE=1;

    String address;
    double lat,lng;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    Bitmap bp;
    String path="images/";
    private Segnalazione s1;



    //intent per poter ricevere il risultato dalla fotocamera e settare l'immagine
    ActivityResultLauncher<Intent> photoResult1= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data=result.getData();

                bp = (Bitmap) data.getExtras().get("data");
                imgZonaPericolosa.setImageBitmap(bp);


            }
        }
    });

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
    public void uploadImage() {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        try{

            File file = new File(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            Log.e("datafoto",data+"");
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("/imagesZonaPericolosa/"+s1.getIdSegnalazione()).putBytes(data);
            if(isInternetAvailable()) {
                storageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.e("storage", storageTask + "");

                    }
                });
            }else{
            }

        }catch (Exception e){
        }
    }
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

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                    photoResult1.launch(photoIntent);
                } else {
                    //Dire all'utente di andare nelle impostazioni e dare i permessi dello storage all'app

                }
            });
    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Per poter utilizzare questa applicazione con tutte le sue funzionalità, è consigliato accettare i permessi");
        alertDialogBuilder.setPositiveButton("Ho capito",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

        alertDialogBuilder.setNegativeButton("Magari più tardi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public fragment_zona_pericolosa() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_zona_pericolosa, container, false);


        db=FirebaseFirestore.getInstance();
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Segnala pericolo");
        imgZonaPericolosa=rootView.findViewById(R.id.imgZonaPericolosa);
        scattaFotoZona=rootView.findViewById(R.id.scattaFotoZona);
        descrzioneZonaPericolosa=rootView.findViewById(R.id.descrzioneZonaPericolosa);
        titoloZonaPericolosa=rootView.findViewById(R.id.titoloZonaPericolosa);
        confermaZonaPericolosa=rootView.findViewById((R.id.confermaZonaPericolosa));



        //Intent per poter avviare la fotocamera nell'app
        scattaFotoZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                    photoResult1.launch(photoIntent);
                    imgZonaPericolosa.setVisibility(View.VISIBLE);
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showAlertDialog();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }

            }
        });

        //Autocomplete Indirizzo

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyDlX6obgKqLyk_7MU5HD6hKzZeWQo0xEaA", Locale.ITALY);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteZona);



        // Start the autocomplete intent.


        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("place21", "Place: " + place.getName() + ", " + place.getId());
                address=place.getName();
                LatLng latlng=place.getLatLng();
                lat=latlng.latitude;
                lng=latlng.longitude;
              /*

                Log.i("place21", "coordinate: " + place.getLatLng() );
                Log.i("place21", "coordinate: " + lat );
                Log.i("place21", "coordinate: " + lng );*/

            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("placeerror", "An error occurred: " + status);
            }
        });

        confermaZonaPericolosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=0;

                String tipo="Zona Pericolosa";

                Random rand=new Random();
                String descrizione=descrzioneZonaPericolosa.getText().toString();
                String titolo=titoloZonaPericolosa.getText().toString();
                String idSegnalazione=rand.nextInt()+"";

                SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                String data= dateFor.format(new Date());



                String urlFoto="/imagesZonaPericolosa/"+idSegnalazione;

                if (TextUtils.isEmpty(descrizione)){
                    descrzioneZonaPericolosa.setError("Inserire una descrizione");
                    i=1;
                }
                if (TextUtils.isEmpty(titolo)){
                    titoloZonaPericolosa.setError("Inserire un Titolo");
                    i=1;
                }
                if (TextUtils.isEmpty(address)){
                    Toast.makeText(getContext(), "Inserire un indirizzo di ritrovamento", Toast.LENGTH_SHORT).show();
                    i=1;
                }

                if(i==0) {
                    s1 = new Segnalazione(auth.getCurrentUser().getEmail(), titolo, tipo, "", idSegnalazione, descrizione, lat, lng, data, urlFoto, " ");
                    db.collection("segnalazioni").document(s1.getIdSegnalazione()).set(s1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                            //da attivare una volta salvata la foto
                            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new reports_fragment()).addToBackStack(null).commit();
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    });
                    uploadImage();
                }else{
                    Toast.makeText(getContext(), "Controllare di aver inserito tutti i campi e riprovare", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return rootView;
    }
}