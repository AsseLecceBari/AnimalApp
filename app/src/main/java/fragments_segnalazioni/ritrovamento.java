package fragments_segnalazioni;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.util.Locale;
import java.util.Random;

import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class ritrovamento extends Fragment {
    private TextView indirizzo, descrizione;
    private Button gps,upImgRitrovamento, creaSegnalazione;
    private TextView vaiSmarrimento;
    private Segnalazione s;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ImageView imgAnimale;


    Bitmap bp;
    String path="images/";
    private File file;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private final int PHOTO_REQUEST_CODE=1;


    String address;
    double lat,lng;
    private String idSegnalazione;

    ActivityResultLauncher<Intent> photoResult= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data=result.getData();

                bp = (Bitmap) data.getExtras().get("data");
                imgAnimale.setImageBitmap(bp);
                imgAnimale.setVisibility(View.VISIBLE);

            }
        }
    });

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ritrovamento, container, false);
        db= FirebaseFirestore.getInstance();
        descrizione = rootView.findViewById(R.id.etDescrizione);
        upImgRitrovamento= rootView.findViewById(R.id.upImgRitrovamento);
        creaSegnalazione=rootView.findViewById(R.id.creaSegnalazioneBtn);
        vaiSmarrimento=rootView.findViewById(R.id.vaiSmarrimento);

        imgAnimale = rootView.findViewById(R.id.imgAnimale);

        Random r= new Random();
        idSegnalazione = String.valueOf(r.nextInt());

        upImgRitrovamento.setOnClickListener(new View.OnClickListener() {
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
        creaSegnalazione.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO fare i controlli sui campi
                SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                String urlFoto="/imagesAnimaliInPericolo/"+idSegnalazione;

                String data= dateFor.format(new Date());
                s= new Segnalazione(auth.getCurrentUser().getEmail(),"titolo","Ritrovamento",idSegnalazione+"",descrizione.getText().toString()+"",lat, lng, data,urlFoto);
                db.collection("segnalazioni").document(s.getIdSegnalazione()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(),"Aggiunto",Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                    }
                });
                uploadImage();

            }
        });
        vaiSmarrimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vai a segnalazioni con filtro smarrimento
               getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new reports_fragment()).commit();
            }
        });

        descrizione.setText("Ho trovato quest'animale: Perfavore aiutatelo!");

        //Autocomplete Indirizzo

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyDlX6obgKqLyk_7MU5HD6hKzZeWQo0xEaA", Locale.US);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autoCompleteFragment);



        // Start the autocomplete intent.


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
        return rootView;
    }


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
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("/imagesAnimaliRitrovati/"+idSegnalazione).putBytes(data);
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

}