package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.ReportAdapter;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;


public class vistaSegnalazione extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    protected ReportAdapter mAdapter;
    protected ArrayList<Segnalazione> mDataset = new ArrayList<>();
    private TextView descrizioneReport;
    private TextView tipoReport;
    private Segnalazione seg;
    //MainActivity main1 = (MainActivity)getActivity() ;
    //private String id;
    private static final String ARG_PARAM1 = "obj";
    private Segnalazione s;
    SupportMapFragment map;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            s= (Segnalazione) getArguments().getSerializable(ARG_PARAM1);
        }
        //initDataset(" ");

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