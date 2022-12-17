package fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.SegnalazioneSanitaria;

public class aggiungiSegnalazioneSanitaria extends Fragment {
    private TextView data, motivoConsultazione, diagnosi, farmaci, trattamento;
    private Button crea;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;
    private MaterialCheckBox isSpecifico, isDiagnosiPositiva;
    private View visita;
    private Spinner statoTrattamento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_segnalazione_sanitaria, container, false);

        crea = rootView.findViewById(R.id.registraBtn);
        data = rootView.findViewById(R.id.data);
        motivoConsultazione = rootView.findViewById(R.id.motivoConsultazione);
        diagnosi = rootView.findViewById(R.id.diagnosi);
        farmaci = rootView.findViewById(R.id.farmaci);
        trattamento = rootView.findViewById(R.id.trattamento);
        isSpecifico = rootView.findViewById(R.id.isSpecifico);
        visita = rootView.findViewById(R.id.visita);

        isDiagnosiPositiva = rootView.findViewById(R.id.isDiagnosiPositiva);
        statoTrattamento = rootView.findViewById(R.id.statoTrattamento);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        cldr = Calendar.getInstance();

        startDatabase();

        isSpecifico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isSpecifico.isChecked()){
                    visita.setVisibility(View.GONE);
                }else{
                    visita.setVisibility(View.VISIBLE);
                }
            }
        });

        // Al click nel campo Nascita si apre il date picker
        data.setText(new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString());
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);
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

        crea.setOnClickListener(new View.OnClickListener() {
            //TODO METTERE CONTROLLO RUOLO,  VETERINARIO è loggato ,emailvet deve essere l'email del veterinario...USIAMO IL FILE CONI DATI PERSISTENTI CHE CONOSCE IL DOTTOR DOMENICO
            @Override
            public void onClick(View view) {
                // se chi è loggato è un veterinario allora nel campo email vet va la sua mail


                CollectionReference reference=db.collection("utenti");
                if(auth.getCurrentUser()!=null) {
                    Query query = reference.whereEqualTo("email", auth.getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            String ruolo = null;
                            String emailveterinario;
                            if (task.isSuccessful()) {
                                for(QueryDocumentSnapshot document : task.getResult()) {

                                    ruolo = document.get("ruolo").toString();

                                    if(ruolo.equals("veterinario")){
                                        emailveterinario = document.get("email").toString();
                                    }else{
                                        emailveterinario = "proprietario";
                                    }

                                    //--------
                                    SegnalazioneSanitaria s;
                                    String statoT = statoTrattamento.getSelectedItem().toString();
                                    if(isSpecifico.isChecked()){
                                        s = new SegnalazioneSanitaria(data.getText().toString(), emailveterinario, motivoConsultazione.getText().toString(), "", "", trattamento.getText().toString(), new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), true, isDiagnosiPositiva.isChecked(), statoT);
                                    }else{
                                        s = new SegnalazioneSanitaria(data.getText().toString(), emailveterinario, motivoConsultazione.getText().toString(), diagnosi.getText().toString(), farmaci.getText().toString(), trattamento.getText().toString(),new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), false, isDiagnosiPositiva.isChecked(), statoT);
                                    }
                                    db.collection("segnalazioneSanitaria").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                    getActivity().onBackPressed();
                                    //--------

                                    break;
                                }
                            }

                        }
                    });
                }

            }
        });

        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}