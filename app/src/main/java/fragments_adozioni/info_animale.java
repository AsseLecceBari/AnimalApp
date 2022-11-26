package fragments_adozioni;

import android.annotation.SuppressLint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class info_animale extends Fragment {
    private Animale animale;
    private ImageView immagineAnimale ;
    private TextView descrizioneAnimale;
    private TextView dettagliAnimale;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View root=inflater.inflate(R.layout.fragment_info_animale, container, false);

        immagineAnimale=root.findViewById(R.id.imageAnimal);
        descrizioneAnimale=root.findViewById(R.id.DescrizioneAnimale);
        dettagliAnimale=root.findViewById(R.id.dettagliAnimale);


        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
       caricaInfoAnimale();


    }



    public void caricaInfoAnimale()
    {
        // setto l'immagine dell'animale
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(immagineAnimale.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineAnimale);
            }
        });

        dettagliAnimale.setText("Dettagli"+"\n\n\n"+"Nome"+"       "+"Data Di Nascita"+"       "+"Specie"+"\n"+animale.getNome()+"            "+animale.getDataDiNascita()+"            "+animale.getSpecie() );
        descrizioneAnimale.setText("Descrizione");
    }
}