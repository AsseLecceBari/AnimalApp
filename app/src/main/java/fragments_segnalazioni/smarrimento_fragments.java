package fragments_segnalazioni;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;


public class smarrimento_fragments extends Fragment {
    private static final String ARG_PARAM1 = "obj";
    private Animale a;

    CircleImageView imgAnimaleSmarrito;
    TextView nomeAnimaleSmarrito,genereAnimaleSmarrito,specieANimaleSmarrito,nascitaAnimaleSmarrito;
    TextInputLayout descrizioneTextLayout,indirizzoTextLayout;
    TextInputEditText descrizioneEditText,indirizzoEditText;
    FloatingActionButton confermaSmarrimento;
    private FirebaseFirestore db;

    private Uri file;

    private FirebaseStorage storage;
    private StorageReference storageRef;








    public smarrimento_fragments() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            a= (Animale) getArguments().getSerializable(ARG_PARAM1);
        }

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
      nascitaAnimaleSmarrito=rootView.findViewById(R.id.nascitaAnimaleSmarrito);

      descrizioneTextLayout=rootView.findViewById(R.id.descrizioneTextLayout);
      indirizzoTextLayout=rootView.findViewById(R.id.indirizzoTextLayout);
      descrizioneEditText=rootView.findViewById(R.id.descrizioneEditText);
      indirizzoEditText=rootView.findViewById(R.id.indirizzoEditText);

      confermaSmarrimento=rootView.findViewById(R.id.confermaSmarrimento);

        if(a!= null) {
            nomeAnimaleSmarrito.setText(a.getNome());
            genereAnimaleSmarrito.setText(a.getGenere());
            specieANimaleSmarrito.setText(a.getSpecie());
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



            confermaSmarrimento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     String tipo="smarrimento";

                     Random idSegnalazione=new Random();
                     String descrizione=descrizioneEditText.getText().toString();
                     String coordinateGps=indirizzoEditText.getText().toString();

                     //da prendere la foto
                     String data;
                     String urlFoto=a.getFotoProfilo();
                    SimpleDateFormat dateFor = new SimpleDateFormat("dd-M-yyyy");
                     data = dateFor.format(new Date());


                     Segnalazione s1=new Segnalazione(tipo,a.getIdAnimale(),idSegnalazione.nextInt()+"",descrizione,coordinateGps,data,urlFoto," ");
                    db.collection("segnalazioni").document(s1.getIdSegnalazione()).set(s1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new reports_fragment()).addToBackStack(null).commit();

                        }
                    });

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