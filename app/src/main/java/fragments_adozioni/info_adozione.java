package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.util.Objects;
import java.util.Set;

import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Persona;
import model.Preferenze;

public class info_adozione extends Fragment {
    private Animale animale;
    private Adozione adozione;
    private Persona proprietario;
    private ImageView immagineAnimale ;
    private TextView descrizioneAnimale;
    private TextView dettagliAnimale;
    private View btnaggiungiPreferiti;
    private View btineliminaPreferiti;
    private SharedPreferences share;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseStore;
    private Preferenze preferenze;

    private static String Email  ;
    private Set<String> set = new HashSet<>() ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");
        proprietario=(Persona) getActivity().getIntent().getSerializableExtra("proprietario");

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View root=inflater.inflate(R.layout.fragment_info_adozione, container, false);

        immagineAnimale=root.findViewById(R.id.imageAnimal);
        descrizioneAnimale=root.findViewById(R.id.DescrizioneAnimale);
        dettagliAnimale=root.findViewById(R.id.dettagliAnimale);
        btnaggiungiPreferiti=root.findViewById(R.id.BtnaggiungiPreferiti);
        btineliminaPreferiti=root.findViewById(R.id.btneliminaPreferiti);

        return  root;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        caricaInfoAnimale();
        initDataPreferiti();
        eliminadaPreferiti();
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

        dettagliAnimale.setText(getString(R.string.dettagli)+"\n\n\n"+getString(R.string.nome)+"       "+getString(R.string.data_di_nascita)+"       "+getString(R.string.specie)+"\n"+animale.getNome()+"            "+animale.getDataDiNascita()+"            "+animale.getSpecie() );
        descrizioneAnimale.setText(adozione.getDescrizione());
    }


    public void aggiungiPreferiti()
    {
        btnaggiungiPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth=FirebaseAuth.getInstance();
                Log.d("ciao10","ciao");

                if(preferenze!= null) {


                        int ultimaposiz= preferenze.getAdozioni().size();

                        if( preferenze.getAdozioni().get(ultimaposiz-1)== null) {

                            preferenze.getAdozioni().remove(ultimaposiz - 1);

                        }


                            preferenze.getAdozioni().add(adozione.getIdAdozione());

                        btineliminaPreferiti.setVisibility(View.VISIBLE);
                    btnaggiungiPreferiti.setVisibility(View.GONE);



                    writeData(preferenze);
                }
                else
                {
                    Log.d("ciao10","ciao1");
                    ArrayList <String> adozionipreferite= new ArrayList<>();
                    adozionipreferite.add(adozione.getIdAdozione());
                    ArrayList <String> segnalazioni= new ArrayList<>();
                  segnalazioni.add(null);

                    preferenze= new Preferenze(auth.getCurrentUser().getEmail(),adozionipreferite,segnalazioni);
                    btnaggiungiPreferiti.setVisibility(View.GONE);

                    btineliminaPreferiti.setVisibility(View.VISIBLE);
                    writeData(preferenze);
                }










            }
        });
    }



    public void initDataPreferiti() {

        firebaseStore = FirebaseFirestore.getInstance();

        auth= FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference preferenzeRef = firebaseStore.collection("preferenze");
        //CollectionReference animali = firebaseStore.collection("animali");
if (auth.getCurrentUser()!= null) {
    Query query = preferenzeRef.whereEqualTo("emailUtente", auth.getCurrentUser().getEmail());

    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    preferenze = document.toObject(Preferenze.class);

                }

                if (preferenze != null) // controllo se l'annuncio sta gia nella lista preferiti
                {
                    int cont = 0;
                    for (int a = 0; a < preferenze.getAdozioni().size(); a++) {


                        if (Objects.equals(preferenze.getAdozioni().get(a), adozione.getIdAdozione())) {
                            cont++;

                        }

                    }

                    if (cont == 0) {
                        btnaggiungiPreferiti.setVisibility(View.VISIBLE);
                    } else {
                        btineliminaPreferiti.setVisibility(View.VISIBLE);
                    }
                } else {
                    btnaggiungiPreferiti.setVisibility(View.VISIBLE);

                }

            }
        }
    });
}

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



    public void eliminadaPreferiti()

    {
        btineliminaPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int a=0; a<preferenze.getAdozioni().size(); a++)
                {

                    if(Objects.equals(preferenze.getAdozioni().get(a), adozione.getIdAdozione()))
                    {
                        preferenze.getAdozioni().remove(a);
                        if(preferenze.getAdozioni().size()==0)
                        {
                            preferenze.getAdozioni().add(null);
                        }
                        // Log.d("ciao10", String.valueOf(preferenze.getAdozioni().size()));
                    }
                }
                btineliminaPreferiti.setVisibility(View.GONE);
                btnaggiungiPreferiti.setVisibility(View.VISIBLE);
                writeData(preferenze);

            }
        });

    }

    public String differenzaDataPubblicazione(String datadiPublicazione)
    {


return null;


    }









}