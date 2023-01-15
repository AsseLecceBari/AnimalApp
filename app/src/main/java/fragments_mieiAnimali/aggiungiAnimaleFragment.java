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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import DB.AnimaleDB;
import class_general.Utils;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class aggiungiAnimaleFragment extends Fragment {
    private Calendar cldr;
    private TextInputLayout dataLayout;
    private TextInputEditText data;
    private  TextInputEditText etRegNomeAnimale;
    private  AutoCompleteTextView etRegGenereAnimale;
    private AutoCompleteTextView etRegSpecieAnimale;
    //private  TextInputEditText etRegGenereAnimale;
   // private  TextInputEditText etRegSpecieAnimale;
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

    public aggiungiAnimaleFragment() {
    }
    private static final String[] genereAnimali = new String[] {
            "Cane","Gatto","Criceto","Topo","Gerbillo","Cincillà","Degu","Scoiattolo","Porcellino d'India",
            "Coniglio","Cavallo","Asino","Donnola","Furetto","Ermellino","Lama","Alpaca","Maiale",
            "Mucca","Capra nana","Capra","Pecora","Cocorita", "Cacatua", "Ara","Gallina","Canarino",
            "Colomba","Gallo","Anatra", "Diamantino mandarino","Tartaruga","Lucertola","Iguana",
            "Serpente", "Camaleonte","Tritone","Salamandra","Grillo","Tarantola","Scorpione"};
    private static final String[] specieCani = new String[] {
            "Meticcio",
            "Labrador Retriever",
            "Setter inglese",
            "Cocker",
            "Maltese",
            "Pinscher",
            "Pastore Tedesco",
            "Chihuahua",
            "Jack russel terrier",
            "Golden retriever",
            "Border collie",
            "Lagotto",
            "Bulldog",
            "Yorkshire terrier",
            "Beagle",
            "Pastore maremmano",
            "Corso",
            "Boxer",
            "Bassotto Tedesco Normale",
            "Pitbull",
            "Rottweiler",
            "Meticcio segugio",
            "Pointer inglese",
            "Bassotto tedesco nano",
            "Shih-tzu",
            "Pastore australiano",
            "Segugio maremmano",
            "Carlino",
            "Volpino italiano",
            "Meticcio",
            "Volpino di pomerania",
            "Bovaro del bernese",
            "Bracco tedesco",
            "Siberian husky",
            "Meticcio maremmano",
            "Bulldog inglese",
            "Fox terrier",
            "Dobermann",
            "Meticcio volpino",
            "Alaskan malamute",
            "Cane lupo cecoslovacco",
            "Shiba",
            "Bullmastiff",
            "Meticcio-bracco",
            "Akita inu",
            "Pechinese",
            "Meticcio setter",
            "Bracco italiano",
            "Chow Chow",
            "Alano tedesco",
            "Barboni nano nero",
            "Barboni toy",
            "Setter irlandese",
            "Pastore belga",
            "Setter gordon",
            "Shar pei",
            "Spinone",
            "Zwerg pinscher nano",
            "Meticcio spinone",
            "Terranova",
            "Barbone miniatura",
            "Boston terrier",
            "Galgo espanol",
            "Grande bovaro svizzero",
            "Staffordshire terrier amer",
            "Schnauzer normale",
            "Spitz",
            "Zwerg Schnauzer nano"};
    private static final String[] specieGatti = new String[] {
            "Persiano",
            "Maine coon",
            "Exotic shorthair",
            "Abissino",
            "Siamese",
            "Ragdoll",
            "Sphynx",
            "Birmano",
            "American shorthair",
            "Orientale"};
    private static final String[] specieDefault = new String[] {
            "Specie in arrivo"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private boolean checkSpecieItemExist(String text){
        boolean flagExist = false;
        int i;
        for(i=0;i<specieCani.length;i++) {
            if (text.equals(specieCani[i])) {
                flagExist = true;
                break;
            }
        }
        for(i=0;i<specieGatti.length;i++){
                if(text.equals(specieGatti[i])){
                    flagExist=true;
                    break;
                }
        }
        for(i=0;i<specieDefault.length;i++){
            if(text.equals(specieDefault[i])){
                flagExist=true;
                break;
            }
        }
        return flagExist;
    }
    private boolean checkGenereItemExist(String text){
        boolean flagExist = false;
        int i;
        for(i=0;i<genereAnimali.length;i++) {
            if (text.equals(genereAnimali[i])) {
                flagExist = true;
                break;
            }
        }
        return flagExist;
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
        ArrayAdapter<String> adapterGeneri = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, genereAnimali);
        etRegGenereAnimale=rootView.findViewById(R.id.etRegGenereAnimale);

        etRegSpecieAnimale=rootView.findViewById(R.id.etRegSpecieAnimale);
        etRegGenereAnimale.setAdapter(adapterGeneri);
        etRegGenereAnimale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etRegSpecieAnimale.setFocusable(true);
                etRegSpecieAnimale.setClickable(true);
                etRegSpecieAnimale.setFocusableInTouchMode(true);
                ArrayAdapter<String> adapterSpecie;
                TextView textView= (TextView) view;
                switch ((String) textView.getText()){
                    case "Cane":
                        etRegSpecieAnimale.setText("");
                      adapterSpecie = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, specieCani);
                        etRegSpecieAnimale.setAdapter(adapterSpecie);
                        break;
                    case "Gatto":
                        etRegSpecieAnimale.setText("");
                      adapterSpecie = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, specieGatti);
                        etRegSpecieAnimale.setAdapter(adapterSpecie);
                        break;
                    default:
                        etRegSpecieAnimale.setText("Specie in arrivo");
                        adapterSpecie = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, specieDefault);
                        etRegSpecieAnimale.setAdapter(adapterSpecie);
                        etRegSpecieAnimale.setFocusable(false);
                        break;
                }
            }
        });
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
       // etRegGenereAnimale=rootView.findViewById(R.id.etRegGenereAnimale);
       // etRegSpecieAnimale=rootView.findViewById(R.id.etRegSpecieAnimale);
        etRegSessoAnimale=rootView.findViewById(R.id.etRegSessoAnimale);
        iLSessoAnimale=rootView.findViewById(R.id.inputSessoAnimale);
        registraAnimaleBtn=rootView.findViewById(R.id.registraAnimaleBtn);
        dataLayout = rootView.findViewById(R.id.dataNascitaAnimaleIL);
        data=rootView.findViewById(R.id.dataNascitaAnimaleText);
        isAssistito = rootView.findViewById(R.id.isAssistito);
        dataRitrovamento = rootView.findViewById(R.id.dataRitrovamentoAnimaleAnimaleText);


        ascoltatorePulsanteData(data);
        ascoltatorePulsanteData(dataRitrovamento);

        registraAnimaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("genere",etRegGenereAnimale.getText().toString());
                String nome= etRegNomeAnimale.getText().toString();
                String genere=etRegGenereAnimale.getText().toString();
                String specie=etRegSpecieAnimale.getText().toString();
                String sesso= etRegSessoAnimale.getSelectedItem().toString();
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
                if(!checkGenereItemExist(etRegGenereAnimale.getText().toString())){
                    etRegGenereAnimale.setError("Selezionare un genere dalla lista");
                    flag=1;
                }
                if(!checkSpecieItemExist(etRegSpecieAnimale.getText().toString())){
                    etRegSpecieAnimale.setError("Selezionare una specie dalla lista");
                    flag=1;
                }
                // se tutto va bene registro
                if(flag == 1) {

                }else{
                    // SE SEI UN PROFESSIONISTA PUOI INSERIRE ANCHE IL BOX IN CUI LO TIENI, mentre la data di ritrovamento va inserita da tutti

                    registraAnimaleBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Caricamento..", Toast.LENGTH_LONG).show();
                    Animale a = new Animale(nome, genere, specie, emailProprietario, dataDiNascita, fotoProfilo, idAnimale, assistito,sesso, box.getText().toString(),dataRitrovamento.getText().toString(),microChip.getText().toString());
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