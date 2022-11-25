package fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;

import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;
import model.Animale;

public class anagrafica extends Fragment {
    private TextView nome, genere, specie, nascita, assistito;
    private Animale animale;

    private ImageView imgAnimaleReg;

    private Uri file;
    private ActivityResultLauncher<String> mGetContent;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Button selectImgButton;








    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);
        db=FirebaseFirestore.getInstance();

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        imgAnimaleReg = rootView.findViewById(R.id.imgAnimaleReg);
        nome = rootView.findViewById(R.id.nome);
        genere = rootView.findViewById(R.id.genere);
        specie = rootView.findViewById(R.id.specie);
        nascita = rootView.findViewById(R.id.nascita);
        assistito = rootView.findViewById(R.id.assistito);

        if(animale!= null){
            nome.setText(animale.getNome());
            genere.setText(animale.getGenere());
            specie.setText(animale.getSpecie());
            nascita.setText(animale.getDataDiNascita());

            if(animale.getIsAssistito()){
                assistito.setText(R.string.eassistito);
            }else{
                assistito.setText(R.string.noneassistito);
            }

            // setto l'immagine dell'animale
            FirebaseStorage storage;
            StorageReference storageRef;
            storage= FirebaseStorage.getInstance();
            storageRef=storage.getReference();

            storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(imgAnimaleReg.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAnimaleReg);
                }
            });
        }

        // tasto cambia immagine
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        file=uri;
                        if(file!=null){
                            animale.setFotoProfilo("images/"+file.getLastPathSegment());

                            // aggiornare il db
                            db.collection("animali").document(animale.getIdAnimale()).set(animale).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    }
                            });
                            uploadImage();
                            imgAnimaleReg.setImageURI(file);
                        }
                    }
                });

        selectImgButton = rootView.findViewById(R.id.selectImgButton);
        selectImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });


      /*  if(!proprietario)
        {   bottoneMar.setVisibility(View.INVISIBLE);
            inviaemail.setVisibility(View.VISIBLE);
        }
        else
        {
         //   bottoneMar.setVisibility(View.VISIBLE);
           // inviaemail.setVisibility(View.INVISIBLE);

        }*/







        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    public void setAnimale(Animale a){
        animale = a;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
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
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
            if(isInternetAvailable()) {
                storageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    }
                });
            }
        }catch (Exception e){
        }
    }
}