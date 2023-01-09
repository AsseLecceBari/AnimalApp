package fragments_adozioni;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;


public class aggiungi_annuncio_Adozione extends Fragment {


    private static final String ARG_PARAM1 = "adozione";
    private static final String ARG_PARAM2 = "animale";
    private Adozione adozione;
    private Animale animale;

    private FirebaseFirestore db;

    private ImageView immagineAnimale ;
    private TextInputEditText descrizioneAnimale;
    private TextView dettagliAnimale;
    private View aggiungiAdozione;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adozione= (Adozione) getArguments().getSerializable(ARG_PARAM1);
            animale= (Animale) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        caricaInfoAnimale();
aggiungiAdozione();



    }

    public Fragment newInstance(Adozione param1, Animale animale) {
        aggiungi_annuncio_Adozione fragment = new aggiungi_annuncio_Adozione();
        Bundle args = new Bundle();

       args.putSerializable(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2,animale);

        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_aggiungi_annuncio_adozione, container, false);
        immagineAnimale=root.findViewById(R.id.imageAnimal);
        descrizioneAnimale=root.findViewById(R.id.DescrizioneAnimale);
        dettagliAnimale=root.findViewById(R.id.dettagliAnimale);
        aggiungiAdozione=root.findViewById(R.id.btnaggiungiadozioni);



        return root;
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

        descrizioneAnimale.setText(adozione.getDescrizione());

        dettagliAnimale.setText(getString(R.string.dettagli)+"\n\n\n"+getString(R.string.nome)+"       "+getString(R.string.data_di_nascita)+"       "+getString(R.string.specie)+"\n"+animale.getNome()+"            "+animale.getDataDiNascita()+"            "+animale.getSpecie() );

    }
    public void aggiungiAdozione() {

        aggiungiAdozione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(descrizioneAnimale!= null)
                {
                    adozione.setDescrizione(descrizioneAnimale.getText().toString());
                }
                db= FirebaseFirestore.getInstance();
                db.collection("adozioni").document(adozione.getIdAdozione()).set(adozione);
                Toast.makeText(getActivity(), R.string.AnimaleAggiuntoInBachecaAdozioni, Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }
}