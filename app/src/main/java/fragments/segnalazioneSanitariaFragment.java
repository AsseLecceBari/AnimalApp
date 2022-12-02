package fragments;

import static android.graphics.Color.parseColor;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;

public class segnalazioneSanitariaFragment extends Fragment {

    private SegnalazioneSanitaria s;

    public segnalazioneSanitariaFragment(SegnalazioneSanitaria s) {
        this.s = s;
    }

    private TextView data, emailVet, motivoConsultazione, diagnosi, farmaci, trattamento;
    private FloatingActionButton conferma;
    private FloatingActionButton modifica;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_segnalazione_sanitaria, container, false);

        modifica = rootView.findViewById(R.id.modifica);
        conferma = rootView.findViewById(R.id.conferma);
        data = rootView.findViewById(R.id.data);
        emailVet = rootView.findViewById(R.id.emailvet);
        motivoConsultazione = rootView.findViewById(R.id.motivoConsultazione);
        diagnosi = rootView.findViewById(R.id.diagnosi);
        farmaci = rootView.findViewById(R.id.farmaci);
        trattamento = rootView.findViewById(R.id.trattamento);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        cldr = Calendar.getInstance();

        startDatabase();

        // Al click nel campo Nascita si apre il date picker
        data.setText(new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString());
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);

        setFocusable(false);

        data.setText(s.getData());
        emailVet.setText(s.getEmailVet());
        motivoConsultazione.setText(s.getMotivoConsultazione());
        diagnosi.setText(s.getDiagnosi());
        farmaci.setText(s.getFarmaci());
        trattamento.setText(s.getTrattamento());

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                setFocusable(true);

                modifica.setVisibility(View.GONE);
                conferma.setVisibility(View.VISIBLE);

            }
        });

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SegnalazioneSanitaria segn;
                segn = new SegnalazioneSanitaria(data.getText().toString(), emailVet.getText().toString(), motivoConsultazione.getText().toString(), diagnosi.getText().toString(), farmaci.getText().toString(), trattamento.getText().toString(), s.getId(), s.getIdAnimale());
                db.collection("segnalazioneSanitaria").document(s.getId()).set(segn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }

    private void setFocusable(boolean bol) {

        if(bol == false){
            emailVet.setInputType(InputType.TYPE_NULL);
            motivoConsultazione.setInputType(InputType.TYPE_NULL);
            diagnosi.setInputType(InputType.TYPE_NULL);
            farmaci.setInputType(InputType.TYPE_NULL);
            trattamento.setInputType(InputType.TYPE_NULL);
        }else{
            emailVet.setInputType(InputType.TYPE_CLASS_TEXT);
            motivoConsultazione.setInputType(InputType.TYPE_CLASS_TEXT);
            diagnosi.setInputType(InputType.TYPE_CLASS_TEXT);
            farmaci.setInputType(InputType.TYPE_CLASS_TEXT);
            trattamento.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        emailVet.setFocusable(bol);
        motivoConsultazione.setFocusable(bol);
        diagnosi.setFocusable(bol);
        farmaci.setFocusable(bol);
        trattamento.setFocusable(bol);
        emailVet.setFocusableInTouchMode(bol);
        motivoConsultazione.setFocusableInTouchMode(bol);
        diagnosi.setFocusableInTouchMode(bol);
        farmaci.setFocusableInTouchMode(bol);
        trattamento.setFocusableInTouchMode(bol);

    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}