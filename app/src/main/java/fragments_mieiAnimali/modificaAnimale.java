package fragments_mieiAnimali;

import static it.uniba.dib.sms2223_2.R.string.inserire_una_immagine_del_profilo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import DB.AnimaleDB;
import class_general.Utils;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class modificaAnimale extends Fragment {
    private Animale a;
    private Calendar cldr;
    private TextInputLayout dataLayout;
    private TextInputEditText data;
    private  TextInputEditText etRegNomeAnimale;
    private  TextInputEditText etRegGenereAnimale;
    private  TextInputEditText etRegSpecieAnimale;
    private   TextInputLayout iLSessoAnimale;
    private Spinner etRegSessoAnimale;
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
    private Utils utils;
    private AnimaleDB animaleDB;
    private TextInputEditText dataRitrovamento, box, microChip;


    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    file=uri;
                    if(file!=null){
                        imgAnimaleReg.setVisibility(View.VISIBLE);
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
        alertDialogBuilder.setMessage(R.string.consiglio_accettare_permessi);
        alertDialogBuilder.setPositiveButton(R.string.ho_capito,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.magari_piu_tardi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public modificaAnimale(Animale animale) {
        a = animale;
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
        main_action_bar.setTitle(R.string.aggiungi_animale);
        animaleDB = new AnimaleDB();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
            main_action_bar.getMenu().clear();
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

        // se è vet vede microchip,  se è professionista vede box
        microChip = rootView.findViewById(R.id.microChip);
        box = rootView.findViewById(R.id.etbox);
        mostraInBaseAlProfilo();

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
        etRegSessoAnimale=rootView.findViewById(R.id.etRegSessoAnimale);
        iLSessoAnimale=rootView.findViewById(R.id.inputSessoAnimale);
        registraAnimaleBtn=rootView.findViewById(R.id.registraAnimaleBtn);
        dataLayout = rootView.findViewById(R.id.dataNascitaAnimaleIL);
        data=rootView.findViewById(R.id.dataNascitaAnimaleText);
        isAssistito = rootView.findViewById(R.id.isAssistito);
        dataRitrovamento = rootView.findViewById(R.id.dataRitrovamentoAnimaleAnimaleText);

        // setto gia i box
        etRegNomeAnimale.setText(a.getNome());
        etRegGenereAnimale.setText(a.getGenere());
        etRegSpecieAnimale.setText(a.getSpecie());
        if(a.getSesso().equals("Maschio")){
            etRegSessoAnimale.setSelection(0);
        }else{
            etRegSessoAnimale.setSelection(1);
        }
        data.setText(a.getDataDiNascita());
        isAssistito.setChecked(a.getIsAssistito());
        dataRitrovamento.setText(a.getDataRitrovamento());


        // setto l'immagine
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(a.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgAnimaleReg);
                imgAnimaleReg.setVisibility(View.VISIBLE);

            }
        });

        registraAnimaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome= etRegNomeAnimale.getText().toString();
                String genere= etRegGenereAnimale.getText().toString();
                String specie= etRegSpecieAnimale.getText().toString();
                String sesso= etRegSessoAnimale.getSelectedItem().toString();
                String emailProprietario= auth.getCurrentUser().getEmail();
                String dataDiNascita= data.getText().toString();
                String fotoProfilo= null;
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
                if(TextUtils.isEmpty(sesso)){
                    iLSessoAnimale.setError(getString(R.string.sesso_obbligatorio));
                    flag = 1;
                }
                if(TextUtils.isEmpty(fotoProfilo)){
                    Toast.makeText(getContext(), inserire_una_immagine_del_profilo,Toast.LENGTH_LONG).show();
                    flag = 1;
                }
                if (TextUtils.isEmpty(dataDiNascita)){
                    data.setError(getString(R.string.dateBornRequired));
                    flag=1;
                }

                // se tutto va bene registro
                if(flag == 1) {

                }else{
                    // SE SEI UN PROFESSIONISTA PUOI INSERIRE ANCHE IL BOX IN CUI LO TIENI, mentre la data di ritrovamento va inserita da tutti

                    registraAnimaleBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Caricamento..", Toast.LENGTH_LONG).show();
                    a = new Animale(nome, genere, specie, emailProprietario, dataDiNascita, fotoProfilo, idAnimale, assistito,sesso, box.getText().toString(),dataRitrovamento.getText().toString(),microChip.getText().toString());
                    utils= new Utils();
                    animaleDB.registraAnimale(a,db).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                        }
                    });
                    StorageTask<UploadTask.TaskSnapshot> storageTask= animaleDB.uploadImageAnimale(storage,storageRef,file);
                    if(utils.isInternetAvailable()) {
                        storageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        });
        return rootView;
    }

    private void ascoltatorePulsanteData(TextInputEditText data) {
        // Al click nel campo Nascita si apre il date picker
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText(R.string.seleziona_una_data);


        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"));
                        calendar.setTimeInMillis((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate  = format.format(calendar.getTime());
                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        data.setText(formattedDate);
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });
    }

    private void mostraInBaseAlProfilo() {
        // se è veterianario vedo il pulsante
        CollectionReference animaliReference=db.collection("utenti");
        if(auth.getCurrentUser()!=null) {
            Query query = animaliReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        String ruolo = null;

                        for(QueryDocumentSnapshot document : task.getResult()){
                            ruolo = Objects.requireNonNull(document.get("ruolo")).toString();
                            if(!ruolo.equals("proprietario") && !ruolo.equals("veterinario")){
                                box.setVisibility(View.VISIBLE);
                            }

                            if(ruolo.equals("veterinario")){
                                microChip.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                    }

                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);

        }
    }
}