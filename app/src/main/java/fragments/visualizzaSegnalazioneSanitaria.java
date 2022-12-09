package fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;

public class visualizzaSegnalazioneSanitaria extends Fragment {

    private SegnalazioneSanitaria s;

    private TextView data, emailVet, motivoConsultazione, diagnosi, farmaci, trattamento, fattaDa;
    private TextInputLayout diagnosiLayout, motivoConsultazioneLayout;
    private FloatingActionButton modifica;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;



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

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        startDatabase();



        //setFocusable(false);

        data.setText(s.getData());
        motivoConsultazione.setText(s.getMotivoConsultazione());
        diagnosi.setText(s.getDiagnosi());
        farmaci.setText(s.getFarmaci());
        trattamento.setText(s.getTrattamento());
        if(s.getEmailVet().equals("proprietario"))
            fattaDa.setText("Segnalazione fatta dal proprietario e NON certificata");
        else
            fattaDa.setText("Segnalazione fatta dal veterinario e CERTIFICATA");

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se la segnalazione è del veterinario è modificiacabile solo da quello specifico veterinario
                if(s.getEmailVet() != "proprietario"){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if(s.getEmailVet().equals(auth.getCurrentUser())){
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new modificaSegnalazioneSanitariaFragment(s)).commit();
                    }else{
                        Toast.makeText(getContext(), "Questa segnalazione è modificabile solo dal veterinario che l'ha fatta!", Toast.LENGTH_SHORT).show();
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