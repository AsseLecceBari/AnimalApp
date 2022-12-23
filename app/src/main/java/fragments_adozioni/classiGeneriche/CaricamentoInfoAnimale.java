package fragments_adozioni.classiGeneriche;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import model.Adozione;
import model.Animale;

public class CaricamentoInfoAnimale {




    public void caricaInfoAnimale(Animale animale, TextView descrizioneAdozione, Adozione adozione, TextView dettagliAnimale, Context context)
    {
        // setto l'immagine dell'animale
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView immagineAnimale = null;

                Glide.with(context)
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineAnimale);
            }
        });

        descrizioneAdozione.setText(adozione.getDescrizione());

        dettagliAnimale.setText("Dettagli"+"\n\n\n"+"Nome"+"       "+"Data Di Nascita"+"       "+"Specie"+"\n"+animale.getNome()+"            "+animale.getDataDiNascita()+"            "+animale.getSpecie() );

    }
}
