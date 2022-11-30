package fragments_mieiAnimali;
import android.annotation.SuppressLint;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class aggiungiAnimaleFragment extends Fragment {
    private DatePickerDialog picker;
    private Calendar cldr;
    private TextInputLayout dataLayout;
    private TextInputEditText data;
    private  TextInputEditText etRegNomeAnimale;
    private  TextInputEditText etRegGenereAnimale;
    private  TextInputEditText etRegSpecieAnimale;
    private Button selectImgButton;
    private Button registraAnimaleBtn;
    private ImageView imgAnimaleReg;
    private Toolbar main_action_bar;
    private Uri file;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private MaterialCheckBox isAssistito;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    file=uri;
                    if(file!=null){
                        imgAnimaleReg.setImageURI(file);
                    }
                }
            });






    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    mGetContent.launch("image/*");
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
                                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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

    public aggiungiAnimaleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_animale, container, false);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Aggiungi Animale");
        if(main_action_bar.getMenu()!=null) {
                main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
                main_action_bar.setNavigationIcon(R.drawable.back);
                main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });
        }
        main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        selectImgButton = rootView.findViewById(R.id.selectImgButton);
        imgAnimaleReg=rootView.findViewById(R.id.imgAnimaleReg);
        selectImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    mGetContent.launch("image/*");
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                   showAlertDialog();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });

        etRegNomeAnimale=rootView.findViewById(R.id.etRegNomeAnimale);
        etRegGenereAnimale=rootView.findViewById(R.id.etRegGenereAnimale);
        etRegSpecieAnimale=rootView.findViewById(R.id.etRegSpecieAnimale);
        registraAnimaleBtn=rootView.findViewById(R.id.registraAnimaleBtn);
        dataLayout = rootView.findViewById(R.id.dataNascitaAnimaleIL);
        data=rootView.findViewById(R.id.dataNascitaAnimaleText);
        isAssistito = rootView.findViewById(R.id.isAssistito);

        cldr = Calendar.getInstance();

        // Al click nel campo Nascita si apre il date picker
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(rootView.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                data.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        registraAnimaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome= etRegNomeAnimale.getText().toString();
                String genere=etRegGenereAnimale.getText().toString();
                String specie=etRegSpecieAnimale.getText().toString();
                String emailProprietario=auth.getCurrentUser().getEmail();
                String dataDiNascita=data.getText().toString();
                String fotoProfilo=null;
                if (file!=null){
                    fotoProfilo="images/"+file.getLastPathSegment();
                }

                Random r= new Random();
                String idAnimale=r.nextInt()+""; ; //Stesso id del documento
                Boolean assistito= isAssistito.isChecked();

                // Controllo se gli input sono corretti
                int flag = 0;
                // Generali
                if (TextUtils.isEmpty(nome)){
                    etRegNomeAnimale.setError(getString(R.string.nameRequired));
                    flag = 1;
                }
                if (TextUtils.isEmpty(genere)) {
                    etRegGenereAnimale.setError(getString(R.string.genereRequired));
                    flag = 1;
                }
                if(TextUtils.isEmpty(specie)){
                    etRegSpecieAnimale.setError(getString(R.string.specieRequired));
                    flag = 1;
                }
                if(TextUtils.isEmpty(fotoProfilo)){
                    Toast.makeText(getContext(),"Inserire una immagine del profilo",Toast.LENGTH_LONG).show();
                    flag = 1;
                }
                if (TextUtils.isEmpty(dataDiNascita)){
                    data.setError(getString(R.string.dateBornRequired));
                    flag=1;
                }

                // se tutto va bene registro
                if(flag == 1) {

                }else{
                    registraAnimaleBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Caricamento..", Toast.LENGTH_LONG).show();
                    Animale a = new Animale(nome, genere, specie, emailProprietario, dataDiNascita, fotoProfilo, idAnimale, assistito);
                    db.collection("animali").document(idAnimale).set(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                    uploadImage();
                }
            }
        });

        return rootView;
    }

    public void uploadImage() {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        try{
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
            if(isInternetAvailable()) {
                storageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.e("storage", storageTask + "");
                        startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }else{
                startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

        }catch (Exception e){
        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.aggiungiAnimaleGroup);
            main_action_bar.inflateMenu(R.menu.menu_bar_main);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.setNavigationIcon(null);
        }
    }
}