package fragments_adozioni;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fragments_segnalazioni.fragment_vista_news;
import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Segnalazione;


public class riepilogo_Adozione extends Fragment {


    private static final String ARG_PARAM1 = "obj";
    private Adozione adozione;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adozione= (Adozione) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ciao123456", adozione.getEmailProprietario());
    }

    public Fragment newInstance(Adozione param1) {
        riepilogo_Adozione fragment = new riepilogo_Adozione();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riepilogo__adozione, container, false);
    }
}