package fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;
import model.Animale;
import model.Persona;

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
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri file;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    public aggiungiAnimaleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_aggiungi_animale, container, false);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        selectImgButton = rootView.findViewById(R.id.selectImgButton);
        imgAnimaleReg=rootView.findViewById(R.id.imgAnimaleReg);
        selectImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");

            }
        });

        etRegNomeAnimale=rootView.findViewById(R.id.etRegNomeAnimale);
        etRegGenereAnimale=rootView.findViewById(R.id.etRegGenereAnimale);
        etRegSpecieAnimale=rootView.findViewById(R.id.etRegSpecieAnimale);
        registraAnimaleBtn=rootView.findViewById(R.id.registraAnimaleBtn);



        dataLayout = rootView.findViewById(R.id.dataNascitaAnimaleIL);
        data=rootView.findViewById(R.id.dataNascitaAnimaleText);
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
                String fotoProfilo="images/"+file.getLastPathSegment();  //Verificare che si usi Bitmap
                String idAnimale="203886"; //Stesso id del documento
                Boolean isAssistito=false;
                uploadImage();
                Animale a = new Animale(nome, genere, specie, emailProprietario, dataDiNascita, fotoProfilo, idAnimale, isAssistito);
                db.collection("animali").document(idAnimale).set(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(rootView.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }else{
                            Toast.makeText(rootView.getContext(), getString(R.string.registrationError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

        return rootView;
    }




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

    public void uploadImage() {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
// Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
// Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
// Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Log.d("Tag", "Upload is " + progress + "% done");
            }
        });

    }
}