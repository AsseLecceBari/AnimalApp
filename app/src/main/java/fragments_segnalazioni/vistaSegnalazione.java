package fragments_segnalazioni;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import adapter.ReportAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;


public class vistaSegnalazione extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected ReportAdapter mAdapter;
    protected ArrayList<Segnalazione> mDataset = new ArrayList<>();
    private TextView descrizioneReport;
    private TextView tipoReport;
    private ImageView immagineSegnalazione;
    private TextView dataVistaReport;

    private Uri file;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    //MainActivity main1 = (MainActivity)getActivity() ;
    //private String id;
    private static final String ARG_PARAM1 = "obj";
    private Segnalazione s;
    //SupportMapFragment map;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            s= (Segnalazione) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vista_segnalazione, container, false);



        descrizioneReport=rootView.findViewById(R.id.DescrizioneVistaReport);
        tipoReport=rootView.findViewById(R.id.TipoReportVista);
        descrizioneReport.setText(s.getDescrizione());
        tipoReport.setText(s.getTipo());

        dataVistaReport=rootView.findViewById(R.id.dataVistaReport);
        dataVistaReport.setText(s.getData());

        immagineSegnalazione=rootView.findViewById(R.id.ImmagineReport);

        //setto immagine
        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        storageRef.child(s.getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(immagineSegnalazione.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineSegnalazione);
            }
        });


        return rootView;
    }



    public Fragment newInstance(Segnalazione param1) {
        vistaSegnalazione fragment = new vistaSegnalazione();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        //Log.e("ciao",param1+"");
        return fragment;


    }
}