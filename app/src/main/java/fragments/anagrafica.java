package fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class anagrafica extends Fragment {
    private TextView nome, genere, specie, nascita;
    private Animale animale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);
        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        nome = rootView.findViewById(R.id.nome);
        genere = rootView.findViewById(R.id.genere);
        specie = rootView.findViewById(R.id.specie);
        nascita = rootView.findViewById(R.id.nascita);

        if(animale!= null){
            nome.setText(animale.getNome());
            genere.setText(animale.getGenere());
            specie.setText(animale.getSpecie());
            nascita.setText(animale.getDataDiNascita());
        }

        return rootView;
    }



    public void setAnimale(Animale a){
        animale = a;
    }
}