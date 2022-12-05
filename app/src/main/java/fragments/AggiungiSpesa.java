package fragments;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;
import model.Animale;
import model.SpesaAnimale;

public class AggiungiSpesa extends Fragment {
    private TextView descrizione, categoria, costoUnitario, quantita, data;
    private Button crea;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Animale animale;
    private Calendar cldr;
    private DatePickerDialog picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aggiungi_spesa, container, false);
        descrizione = rootView.findViewById(R.id.descrizione);
        categoria  = rootView.findViewById(R.id.categoria);
        costoUnitario = rootView.findViewById(R.id.costoUnitario);
        quantita = rootView.findViewById(R.id.quantita);
        crea = rootView.findViewById(R.id.registraSpesaBtn);
        data = rootView.findViewById(R.id.data);

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        cldr = Calendar.getInstance();

        startDatabase();

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
            @Override
            public void onClick(View view) {
                SpesaAnimale s = new SpesaAnimale(categoria.getText().toString(), data.getText().toString(), descrizione.getText().toString(), new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), Integer.parseInt( costoUnitario.getText().toString()), Integer.parseInt(quantita.getText().toString()));
                db.collection("spese").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    private void startDatabase(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}