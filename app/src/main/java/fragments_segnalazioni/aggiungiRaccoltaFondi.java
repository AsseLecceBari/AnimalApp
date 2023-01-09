package fragments_segnalazioni;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Associazione;
import model.Ente;
import model.Persona;
import model.Segnalazione;
import model.Veterinario;

public class aggiungiRaccoltaFondi extends Fragment {
    private Animale animale;
    private TextInputEditText titolo, descrizione, link;
    private FloatingActionButton conferma;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView imgAnimale;
    private FirebaseFirestore db;
    private Toolbar main_action_bar;
    public aggiungiRaccoltaFondi(Animale a) {
        animale = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_raccolta_fondi, container, false);
        db = FirebaseFirestore.getInstance();

        titolo = rootView.findViewById(R.id.titolo);
        descrizione = rootView.findViewById(R.id.etDescrizione);
        link = rootView.findViewById(R.id.etLink);
        conferma = rootView.findViewById(R.id.conferma);
        imgAnimale = rootView.findViewById(R.id.imgAnimale);


        //default
        titolo.setText("Ho bisogno di voi per aiutare "+ animale.getNome());
        descrizione.setText("Se avete la possibilità, aiutate il mio animale con una donazione!");

        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(R.string.segnala_raccolta_fondi);

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registra();
            }
        });

        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        if (animale.getFotoProfilo() != null) {
            storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(imgAnimale.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAnimale);
                }
            });
        }else{
            imgAnimale.setVisibility(View.GONE);
        }


        return rootView;
    }

    private void registra() {
        // todo: controlli campi
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // trovo il proprietario dell animale
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
                            ruolo = document.get("ruolo").toString();
                            Map<String,String> indirizzo = null;
                            switch(ruolo){
                                case "proprietario":
                                    Persona p = document.toObject(Persona.class);
                                    indirizzo = p.getIndirizzo();
                                    break;

                                case "ente":
                                    Ente e = document.toObject(Ente.class);
                                    indirizzo = e.getIndirizzo();
                                    break;

                                case "associazione":
                                    Associazione a = document.toObject(Associazione.class);
                                    indirizzo = a.getIndirizzo();
                                    break;

                                case "veterinario":
                                    Veterinario v = document.toObject(Veterinario.class);
                                    indirizzo = v.getIndirizzo();
                                    break;
                            }
                            // qui registro passando le cordinate
                            registrazione(auth, indirizzo);
                            break;
                        }
                    }

                }
            });
        }
    }

    private void registrazione(FirebaseAuth auth, Map<String,String> indirizzo) {
        if(indirizzo!=null){
            Segnalazione s =  new Segnalazione(auth.getCurrentUser().getEmail(), titolo.getText().toString(),"Raccolta Fondi", animale.getIdAnimale(), new Random().nextInt(999999999)+"", descrizione.getText().toString(), Double.parseDouble(indirizzo.get("latitudine")), Double.parseDouble(indirizzo.get("longitudine")), new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString(), animale.getFotoProfilo(), link.getText().toString());
            db.collection("segnalazioni").document(s.getIdSegnalazione()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(), "Fatto", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
}