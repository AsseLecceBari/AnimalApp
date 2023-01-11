package fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import DB.AnimaleDB;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;

public class modificaSegnalazioneSanitariaFragment extends Fragment {

    private SegnalazioneSanitaria s;
    private View visita;

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

    private MaterialCheckBox isDiagnosiPositiva, isEsame;
    private Spinner statoTrattamento;
    private Toolbar main_action_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modifica_segnalazione_sanitaria, container, false);

        conferma = rootView.findViewById(R.id.conferma);
        data = rootView.findViewById(R.id.data);
        motivoConsultazione = rootView.findViewById(R.id.motivoConsultazione);
        diagnosi = rootView.findViewById(R.id.diagnosi);
        farmaci = rootView.findViewById(R.id.farmaci);
        trattamento = rootView.findViewById(R.id.trattamento);
        isDiagnosiPositiva = rootView.findViewById(R.id.isDiagnosiPositiva);
        isEsame = rootView.findViewById(R.id.isSpecifico);
        statoTrattamento = rootView.findViewById(R.id.statoTrattamento);
        visita = rootView.findViewById(R.id.visita);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Modifica Segnalazione sanitaria");

        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.profiloAnimaleGroup,false);
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();

                }
            });
        }


        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        cldr = Calendar.getInstance();

        startDatabase();

        // Al click nel campo Nascita si apre il date picker
        data.setText(new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString());
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);

        isEsame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isEsame.isChecked()){
                    visita.setVisibility(View.GONE);
                }else{
                    visita.setVisibility(View.VISIBLE);
                }
            }
        });

        // imposto i campi
        data.setText(s.getData());
        motivoConsultazione.setText(s.getMotivoConsultazione());
        diagnosi.setText(s.getDiagnosi());
        farmaci.setText(s.getFarmaci());
        trattamento.setText(s.getTrattamento());
        isDiagnosiPositiva.setChecked(s.getIsDiagnosiPositiva());
        isEsame.setChecked(s.getIsEsameSpecifico());

        final String[] values = getResources().getStringArray(R.array.statoTrattamento);
        if(values[0].equals(s.getStatoTrattamento())){
            statoTrattamento.setSelection(0);
        }else{
            if(s.getStatoTrattamento().equals(values[1]))
                statoTrattamento.setSelection(1);
            else
                if(values[2].equals(s.getStatoTrattamento()))
                    statoTrattamento.setSelection(2);
        }




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

                String statoT = statoTrattamento.getSelectedItem().toString();
                if(isEsame.isChecked()){
                    segn = new SegnalazioneSanitaria(data.getText().toString(), s.getEmailVet(), motivoConsultazione.getText().toString(), "", "", trattamento.getText().toString(), s.getId(), animale.getIdAnimale().toString(), true, isDiagnosiPositiva.isChecked(), statoT);
                }else{
                    segn = new SegnalazioneSanitaria(data.getText().toString(), s.getEmailVet(), motivoConsultazione.getText().toString(), diagnosi.getText().toString(), farmaci.getText().toString(), trattamento.getText().toString(),s.getId(), animale.getIdAnimale().toString(), false, isDiagnosiPositiva.isChecked(), statoT);
                }

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);

        }
    }
}