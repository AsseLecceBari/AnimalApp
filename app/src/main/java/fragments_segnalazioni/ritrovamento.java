package fragments_segnalazioni;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import adapter.ReportAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class ritrovamento extends Fragment {
    private TextView indirizzo, descrizione;
    private Button gps, scattaFoto, galleria, creaSegnalazione;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ritrovamento, container, false);
        //indirizzo = rootView.findViewById(R.id.etRegIndirizzo);
        //gps = rootView.findViewById(R.id.gps);

        return rootView;
    }



    public void aggiornaTextviewConCordinate() {
        // todo: il gps impostera nella textview le cordinate

        // es:
        // indirizzo.setText(cordinate gps);
        Toast.makeText(getActivity().getApplicationContext(), "Coordinate gps inserite nel indirizzo di ritrovo", Toast.LENGTH_LONG).show();
    }

}