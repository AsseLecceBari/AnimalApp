package fragments;

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

public class anagrafica extends Fragment {
    private TextView nome, genere, specie, nascita, assistito;
    private Animale animale;

    private ImageView imgAnimaleReg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);

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

        return rootView;
    }



    public void setAnimale(Animale a){
        animale = a;
    }
}