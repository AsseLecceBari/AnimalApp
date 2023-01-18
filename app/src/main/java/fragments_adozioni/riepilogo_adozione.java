package fragments_adozioni;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
import it.uniba.dib.sms2223_2.scanAnimale;
import model.Adozione;
import model.Animale;


public class riepilogo_adozione extends Fragment {
    private ImageView immagineAnimale ;
    private TextInputLayout aggiungidescrizioneAnnuncioLayout;
    private TextView dettagliAnimale;
    private TextView descrizioneAnnuncio;
    private TextInputEditText aggiungiDescrizioneAnnuncio;
    private Button cediProprieta;
    private View vista;
    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                .replace(R.id.fragmentContainerView,new scanAnimale(1, animale, adozione.getIdAdozione())).commit();
                    } else {
                        //Dire all'utente di andare nelle impostazioni e dare i permessi dello storage all'app

                    }
                });
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

        dettagliAnimale.setText(getString(R.string.dettagli)+"\n\n\n"+getString(R.string.nome)+"        "+getString(R.string.data_di_nascita)+"       "+"\n"+animale.getNome()+"            "+animale.getDataDiNascita() );
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
        vista = root.findViewById(R.id.vista);

        fab fab= new fab();

        fab.iniziallizazioneFab(root);


        fab.aggiungiFabModifica(root,getContext(),aggiungidescrizioneAnnuncioLayout,descrizioneAnnuncio,aggiungiDescrizioneAnnuncio);
        fab.aggiungiFabElimina(root,getContext(),"adozioni",adozione.getIdAdozione(),getActivity());
        //fab.aggiungiFabAnnullaModifica(root,getContext(),"adozioni",adozione.getIdAdozione(),aggiungidescrizioneAnnuncioLayout,descrizioneAnnuncio,aggiungiDescrizioneAnnuncio);
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {

            //startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new scanAnimale()).commit();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showAlertDialog();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.fragmentContainerView,new scanAnimale(1, animale, adozione.getIdAdozione())).commit();
    }

    public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(getString(R.string.consiglio_accettare_permessi));
        alertDialogBuilder.setPositiveButton(getString(R.string.ho_capito),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.magari_piu_tardi), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}