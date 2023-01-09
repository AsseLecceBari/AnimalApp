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
import com.google.android.libraries.places.api.model.AddressComponents;
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

public class animale_in_pericolo_fragments extends Fragment {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


    private FirebaseAuth auth;
    ImageView imgAnimaleInPericolo;
    Button scattaFotoButton;
    TextInputEditText etDescrizioneAnimaleFerito;
    FloatingActionButton confermaAnimaleFerito;
    TextInputEditText titoloAnimaleInPericolo;
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
    private  Segnalazione s1;
    //intent per poter ricevere il risultato dalla fotocamera e settare l'immagine
    ActivityResultLauncher<Intent> photoResult= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
         if(result.getResultCode() == Activity.RESULT_OK){
            Intent data=result.getData();

              bp = (Bitmap) data.getExtras().get("data");
             imgAnimaleInPericolo.setImageBitmap(bp);


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
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("/imagesAnimaliInPericolo/"+s1.getIdSegnalazione()).putBytes(data);
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

  public animale_in_pericolo_fragments(){

  }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                    photoResult.launch(photoIntent);
                } else {
                    //Dire all'utente di andare nelle impostazioni e dare i permessi dello storage all'app

                }
            });
    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(getString(R.string.consiglio_accettare_permessi));
        alertDialogBuilder.setPositiveButton(getString(R.string.ho_capito),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.magari_piu_tardi), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(policy);
        View rootView=inflater.inflate(R.layout.fragment_animale_in_pericolo_fragments, container, false);

        db=FirebaseFirestore.getInstance();

        imgAnimaleInPericolo=rootView.findViewById(R.id.imgAnimaleInPericolo);
        scattaFotoButton=rootView.findViewById(R.id.scattaFotoButton);
        etDescrizioneAnimaleFerito=rootView.findViewById(R.id.etDescrizioneAnimaleFerito);
        titoloAnimaleInPericolo=rootView.findViewById(R.id.titoloAnimaleInPericolo);
        confermaAnimaleFerito=rootView.findViewById((R.id.confermaAnimaleFerito));
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(R.string.segnala_animale_ferito);

        //Intent per poter avviare la fotocamera nell'app
        scattaFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
                    photoResult.launch(photoIntent);
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
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyDlX6obgKqLyk_7MU5HD6hKzZeWQo0xEaA", Locale.US);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);
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


        //bug: se non ce una foto crusha per nel reportadapter abbiamo getUrlFoto da capire bene come si potrebbe bypassare
        confermaAnimaleFerito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipo="Animale Ferito";

                Random rand=new Random();
                String descrizione=etDescrizioneAnimaleFerito.getText().toString();
                String titolo=titoloAnimaleInPericolo.getText().toString();
                String idSegnalazione=rand.nextInt()+"";

                SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                String data= dateFor.format(new Date());



                String urlFoto="/imagesAnimaliInPericolo/"+idSegnalazione;



                s1=new Segnalazione(auth.getCurrentUser().getEmail(),titolo,tipo,"",idSegnalazione,descrizione,lat,lng,data,urlFoto," ");
                db.collection("segnalazioni").document(s1.getIdSegnalazione()).set(s1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        //da attivare una volta salvata la foto
                         //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new reports_fragment()).addToBackStack(null).commit();
                        // startActivity(new Intent(getContext(),MainActivity.class).putExtra("posizione", 2));
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                });
                uploadImage();



            }
        });

        return rootView;
    }



}