package fragments_segnalazioni;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import adapter.ReportAdapter;
import fragments.main_fragment;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class ritrovamento extends Fragment {
    private TextView indirizzo, descrizione;
    private Button gps,upImgRitrovamento, creaSegnalazione;
    private TextView vaiSmarrimento;
    private Segnalazione s;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ritrovamento, container, false);
        db= FirebaseFirestore.getInstance();
        descrizione = rootView.findViewById(R.id.etDescrizione);
        indirizzo = rootView.findViewById(R.id.etIndirizzo);
        gps = rootView.findViewById(R.id.gps);
        upImgRitrovamento= rootView.findViewById(R.id.upImgRitrovamento);
        creaSegnalazione=rootView.findViewById(R.id.creaSegnalazioneBtn);
        vaiSmarrimento=rootView.findViewById(R.id.vaiSmarrimento);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aggiornaTextviewConCordinate();
            }
        });
        upImgRitrovamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        creaSegnalazione.setOnClickListener(new View.OnClickListener() {
            Random r= new Random();

            @Override
            public void onClick(View view) {
                //TODO fare i controlli sui campi
                s= new Segnalazione(auth.getCurrentUser().getEmail(),
                        "ritrovamento",
                        r.nextInt()+"",
                        descrizione.getText().toString()+"",
                        0.0,
                        0.0,
                        "27/11/22",
                        " "

                );
                db.collection("segnalazioni").document(s.getIdSegnalazione()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity().getApplicationContext(),"Aggiunto",Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
        vaiSmarrimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vai a segnalazioni con filtro smarrimento
               getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new reports_fragment()).commit();
            }
        });
        return rootView;
    }



    public void aggiornaTextviewConCordinate() {
        // todo: il gps impostera nella textview le cordinate

        // es:
        // indirizzo.setText(cordinate gps);
        Toast.makeText(getActivity().getApplicationContext(), "Coordinate gps inserite nel indirizzo di ritrovo", Toast.LENGTH_LONG).show();
    }

}