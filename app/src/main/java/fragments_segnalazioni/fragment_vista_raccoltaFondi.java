package fragments_segnalazioni;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class fragment_vista_raccoltaFondi extends Fragment {
    private TextView titolo, descrizione, data;
    private FloatingActionButton vai;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView imgAnimale;
    private FirebaseFirestore db;
    private Segnalazione segnalazione;

    public fragment_vista_raccoltaFondi(Segnalazione segnalazione) {
        this.segnalazione = segnalazione;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vista_raccolta_fondi, container, false);

        titolo = rootView.findViewById(R.id.titolo);
        descrizione = rootView.findViewById(R.id.descrizione);
        data = rootView.findViewById(R.id.data);
        vai = rootView.findViewById(R.id.btnVaiAlLink);
        imgAnimale = rootView.findViewById(R.id.immagine);

        if (segnalazione != null){
            titolo.setText(segnalazione.getTitolo());
            descrizione.setText(segnalazione.getDescrizione());
            data.setText(segnalazione.getData());
        }

        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        if(segnalazione.getUrlFoto() != null){
            storageRef.child(segnalazione.getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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


        vai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://" + segnalazione.getLink()));
                startActivity(i);
            }
        });

        return rootView;
    }
}