package fragments_adozioni;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import class_general.fab;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;


public class riepilogo_adozione extends Fragment {
    private ImageView immagineAnimale ;
    private TextInputLayout aggiungidescrizioneAnnuncioLayout;
    private TextView dettagliAnimale;
    private TextView descrizioneAnnuncio;
    private TextInputEditText aggiungiDescrizioneAnnuncio;
    private Button cediProprieta;

    Animale animale;
    Adozione adozione;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public riepilogo_adozione() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");

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
        descrizioneAnnuncio.setText(adozione.getDescrizione());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View root =inflater.inflate(R.layout.fragment_riepilogo_adozione, container, false);

        immagineAnimale=root.findViewById(R.id.imageAnimal);
        aggiungidescrizioneAnnuncioLayout=root.findViewById(R.id.AggiungiDescrizioneAnimaleLayout);
        dettagliAnimale=root.findViewById(R.id.dettagliAnimale);
        descrizioneAnnuncio= root.findViewById(R.id.DescrizioneAnimale);
        aggiungiDescrizioneAnnuncio=root.findViewById(R.id.AggiungiDescrizioneAnimale);
        cediProprieta = root.findViewById(R.id.cedi);

        fab fab= new fab();

        fab.iniziallizazioneFab(root);


        fab.aggiungiFabModifica(root,getContext(),aggiungidescrizioneAnnuncioLayout,descrizioneAnnuncio,aggiungiDescrizioneAnnuncio);
        fab.aggiungiFabElimina(root,getContext(),"adozioni",adozione.getIdAdozione(),getActivity());
        fab.aggiungiFabAnnullaModifica(root,getContext(),"adozioni",adozione.getIdAdozione(),aggiungidescrizioneAnnuncioLayout,descrizioneAnnuncio,aggiungiDescrizioneAnnuncio);
        fab.aggiungiFabSalvaModifiche(root,getContext(), "adozioni",adozione.getIdAdozione(),aggiungidescrizioneAnnuncioLayout,descrizioneAnnuncio,aggiungiDescrizioneAnnuncio);
        fab.FabContainerListner();

        // passare la proprietà a qualcuno 
        cediProprieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cedi();
            }
        });

         return root;
    }

    private void cedi() {
        // todo scannerrizzo il qrCode cambio la proprieta dell'animale
    }
}