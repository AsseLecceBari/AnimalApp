package fragments;

import static android.graphics.Color.blue;
import static android.graphics.Color.red;

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

public class modificaSegnalazioneSanitariaFragment extends Fragment {

    private SegnalazioneSanitaria s;

    public modificaSegnalazioneSanitariaFragment(SegnalazioneSanitaria s) {
        this.s = s;
    }

    private TextView data, emailVet, motivoConsultazione, diagnosi, farmaci, trattamento;
    TextInputLayout diagnosiLayout, motivoConsultazioneLayout;
    private FloatingActionButton conferma;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modifica_segnalazione_sanitaria, container, false);

        conferma = rootView.findViewById(R.id.conferma);
        data = rootView.findViewById(R.id.data);
        motivoConsultazione = rootView.findViewById(R.id.motivoConsultazione);
        diagnosi = rootView.findViewById(R.id.diagnosi);
        farmaci = rootView.findViewById(R.id.farmaci);
        trattamento = rootView.findViewById(R.id.trattamento);

        diagnosiLayout = rootView.findViewById(R.id.diagnosiLayout);
        motivoConsultazioneLayout = rootView.findViewById(R.id.motivoConsultazioneLayout);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        cldr = Calendar.getInstance();

        startDatabase();

        // Al click nel campo Nascita si apre il date picker
        data.setText(new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString());
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);

        //setFocusable(false);

        data.setText(s.getData());
        motivoConsultazione.setText(s.getMotivoConsultazione());
        diagnosi.setText(s.getDiagnosi());
        farmaci.setText(s.getFarmaci());
        trattamento.setText(s.getTrattamento());

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                data.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SegnalazioneSanitaria segn;
                segn = new SegnalazioneSanitaria(data.getText().toString(), s.getEmailVet(), motivoConsultazione.getText().toString(), diagnosi.getText().toString(), farmaci.getText().toString(), trattamento.getText().toString(), s.getId(), s.getIdAnimale());
                db.collection("segnalazioneSanitaria").document(s.getId()).set(segn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                Toast.makeText(getContext(), "Modificato!", Toast.LENGTH_SHORT).show();
                ProfiloAnimale p = (ProfiloAnimale) getActivity();
                if (p != null) {
                    p.setS(segn);
                }
                getActivity().getSupportFragmentManager().popBackStack();
                //getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new visualizzaSegnalazioneSanitaria(segn)).commit();
            }
        });
        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}