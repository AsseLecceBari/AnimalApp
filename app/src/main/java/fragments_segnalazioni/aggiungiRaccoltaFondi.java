package fragments_segnalazioni;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Segnalazione;

public class aggiungiRaccoltaFondi extends Fragment {
    private Animale animale;
    private TextInputEditText titolo, descrizione, link;
    private FloatingActionButton conferma;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView imgAnimale;
    private FirebaseFirestore db;

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

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registra();
            }
        });

        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imgAnimale.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgAnimale);
            }
        });

        return rootView;
    }

    private void registra() {
        // todo: controlli campi
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Segnalazione s =  new Segnalazione(auth.getCurrentUser().getEmail(), titolo.getText().toString(),"raccoltaFondi", animale.getIdAnimale(), new Random().nextInt(999999999)+"", descrizione.getText().toString(), -1, -1, new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString(), animale.getFotoProfilo(), link.getText().toString());
        db.collection("segnalazioni").document(s.getIdSegnalazione()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}