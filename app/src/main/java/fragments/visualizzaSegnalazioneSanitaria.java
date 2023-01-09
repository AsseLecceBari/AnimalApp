package fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;

public class visualizzaSegnalazioneSanitaria extends Fragment {

    private SegnalazioneSanitaria s;

    private TextView data, emailVet, motivoConsultazione, diagnosi, farmaci, trattamento, fattaDa, note;
    private TextInputLayout diagnosiLayout, motivoConsultazioneLayout;
    private FloatingActionButton modifica;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;
    private View nonEsame;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visualizza_segnalazione_sanitaria, container, false);

        ProfiloAnimale profilo = (ProfiloAnimale) getActivity();
        if (profilo != null) {
            s = profilo.getS();
        }

        modifica = rootView.findViewById(R.id.modifica);
        data = rootView.findViewById(R.id.dataS);
        motivoConsultazione = rootView.findViewById(R.id.sintomiS);
        diagnosi = rootView.findViewById(R.id.diagnosiS);
        farmaci = rootView.findViewById(R.id.farmaciS);
        trattamento = rootView.findViewById(R.id.trattamentoS);
        fattaDa = rootView.findViewById(R.id.fattaDaS);
        note = rootView.findViewById(R.id.considerazioni);
        nonEsame = rootView.findViewById(R.id.nonEsame);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        startDatabase();

        if(s.getIsEsameSpecifico()){
            nonEsame.setVisibility(View.GONE);

            if(s.getIsDiagnosiPositiva()){
                note.setText(getString(R.string.l_esame_e_andato_a_buon_fine) + s.getStatoTrattamento());
            }else{
                note.setText(getString(R.string.l_esame_non_e_andato_a_buon_fine) + s.getStatoTrattamento());
            }

        }else{
            diagnosi.setText(getString(R.string.diagnosi)+": ".toUpperCase(Locale.ROOT)+s.getDiagnosi());
            farmaci.setText(getString(R.string.farmaci)+": ".toUpperCase(Locale.ROOT) + s.getFarmaci());

            nonEsame.setVisibility(View.VISIBLE);

            if(s.getIsDiagnosiPositiva()){
                note.setText(getString(R.string.la_visita_e_andata_a_buon_fine) + s.getStatoTrattamento());
            }else{
                note.setText(getString(R.string.la_visita_non_e_andata_a_buon_fine) + s.getStatoTrattamento());
            }

        }

        trattamento.setText(getString(R.string.trattamento)+": ".toUpperCase(Locale.ROOT)+s.getTrattamento());
        motivoConsultazione.setText(getString(R.string.motivo_consultazione)+": ".toUpperCase(Locale.ROOT)+s.getMotivoConsultazione());
        data.setText(getString(R.string.data)+": " +s.getData());
        if(s.getEmailVet().equals("proprietario"))
            fattaDa.setText(R.string.segnalazione_fatta_dal_proprietario);
        else
            fattaDa.setText(R.string.segnalazione_fatta_dal_veterinario);

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se la segnalazione è del veterinario è modificiacabile solo da quello specifico veterinario
                if(!s.getEmailVet().equals("proprietario")){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if(auth.getCurrentUser().getEmail().equals(s.getEmailVet())){
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new modificaSegnalazioneSanitariaFragment(s)).commit();
                    }else{
                        Toast.makeText(getContext(), R.string.questa_segnalazione_è_modificabile_solo_dal_vet_che_l_ha_fatta, Toast.LENGTH_SHORT).show();
                    }
                }else
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new modificaSegnalazioneSanitariaFragment(s)).commit();
            }
        });
        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}