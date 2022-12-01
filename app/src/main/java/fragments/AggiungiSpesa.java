package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SpesaAnimale;

public class AggiungiSpesa extends Fragment {
    private TextView descrizione, categoria, costoUnitario, quantita;
    private Button crea;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_spesa, container, false);
        descrizione = rootView.findViewById(R.id.descrizione);
        categoria  = rootView.findViewById(R.id.descrizione);
        costoUnitario = rootView.findViewById(R.id.descrizione);
        quantita = rootView.findViewById(R.id.descrizione);
        crea = rootView.findViewById(R.id.registraSpesaBtn);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        startDatabase();

        crea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo: aprire un fragment per inserire la spesa
                SpesaAnimale s = new SpesaAnimale(categoria.getText().toString(), new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString(), descrizione.getText().toString(), new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), 10, 2);
                db.collection("spese").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                Toast.makeText(getActivity().getApplicationContext(), "Spesa aggiunta!", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}