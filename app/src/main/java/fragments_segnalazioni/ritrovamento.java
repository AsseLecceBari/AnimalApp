package fragments_segnalazioni;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.uniba.dib.sms2223_2.R;

public class ritrovamento extends Fragment {
    private TextView indirizzo;
    private Button gps, mostra;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ritrovamento, container, false);
        indirizzo = rootView.findViewById(R.id.etRegIndirizzo);
        gps = rootView.findViewById(R.id.gps);
        mostra = rootView.findViewById(R.id.cerca);

        // imposto i listener
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aggiornaTextviewConCordinate();
            }
        });
        mostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostraRyclerView();
            }
        });

        return rootView;
    }

    public void mostraRyclerView() {
        // TODO: mostrare solo gli smarrimenti vicini l'indirizzo _____
        // attualmente mostra tutti gli smarrimenti
        Toast.makeText(getActivity().getApplicationContext(), "Mostro in base ai tuoi filtri", Toast.LENGTH_LONG).show();


    }

    public void aggiornaTextviewConCordinate() {
        // todo: il gps impostera nella textview le cordinate

        // es:
        // indirizzo.setText(cordinate gps);
        Toast.makeText(getActivity().getApplicationContext(), "Coordinate gps inserite nel indirizzo di ritrovo", Toast.LENGTH_LONG).show();
    }

}