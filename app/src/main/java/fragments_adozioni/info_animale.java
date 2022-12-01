package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Preferenze;

public class info_animale extends Fragment {
    private Animale animale;
    private Adozione adozione;
    private ImageView immagineAnimale ;
    private TextView descrizioneAnimale;
    private TextView dettagliAnimale;
    private View btnaggiungiPreferiti;
    private SharedPreferences share;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseStore;
    private Preferenze preferenze;

   private static final String mypreference = "animalipreferiti";

    private static String Email  ;
    private Set<String> set = new HashSet<>() ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");

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
        btnaggiungiPreferiti=root.findViewById(R.id.BtnaggiungiPreferiti);

        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
       caricaInfoAnimale();
       initDataAnnunci();

       aggiungiPreferiti();




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


    public void aggiungiPreferiti()
    {
        btnaggiungiPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth=FirebaseAuth.getInstance();

                if(preferenze!= null) {

                        int ultimaposiz= preferenze.getAdozioni().size();

                        if( preferenze.getAdozioni().get(ultimaposiz-1)== null) {

                            preferenze.getAdozioni().remove(ultimaposiz - 1);

                        }

                            preferenze.getAdozioni().add(adozione.getIdAdozione());



                    writeData(preferenze);
                }
                else
                {
                    ArrayList <String> adozionipreferite= new ArrayList<>();
                    adozionipreferite.add(adozione.getIdAdozione());
                    ArrayList <String> segnalazioni= new ArrayList<>();
                  segnalazioni.add(null);

                    preferenze= new Preferenze(auth.getCurrentUser().getEmail(),adozionipreferite,segnalazioni);
                    writeData(preferenze);
                }










            }
        });
    }



    public void initDataAnnunci() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        firebaseStore = FirebaseFirestore.getInstance();

        auth= FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference preferenzeRef = firebaseStore.collection("preferenze");
        //CollectionReference animali = firebaseStore.collection("animali");

        Query query= preferenzeRef.whereEqualTo("emailUtente",auth.getCurrentUser().getEmail());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        preferenze=document.toObject(Preferenze.class);

                    }

                }
            }
        });

    }


    public void writeData(Preferenze preferenze) {

        firebaseStore = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference preferenzeRef = firebaseStore.collection("preferenze");
        //CollectionReference animali = firebaseStore.collection("animali");


        firebaseStore.collection("preferenze").document(auth.getCurrentUser().getEmail()).set(preferenze).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    Toast.makeText(getActivity(), R.string.AggiuntoListaPreferiti, Toast.LENGTH_LONG).show();
                }
            }
        });






    }









}