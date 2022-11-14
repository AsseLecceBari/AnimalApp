package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import model.Associazione;
import model.Ente;
import model.Persona;
import model.Utente;
import model.Veterinario;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout numEFNInputLayout;
    private TextInputLayout piVAInputLayout;
    private TextInputLayout denomInputLayout;
    private TextInputLayout isPrivatoInputLayout;
    private TextInputLayout codFiscAssInputLayout;
    private TextInputLayout dataLayout;
    private TextInputLayout nomeLayout;
    private TextInputLayout cognomeLayout;

    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etRegConfPass;
    private TextInputEditText etRegTelefono;
    private TextInputEditText etRegIndirizzo;
    private Spinner etRegRuolo;
    private TextInputEditText etRegNumEFNOVI;
    private TextInputEditText etRegPartitaIva;
    private TextInputEditText etRegDenominazione;
    private TextInputEditText etRegIsPrivato;
    private TextInputEditText etRegCodiceFiscaleAssociazione;
    private TextInputEditText data;
    private TextInputEditText nome;
    private TextInputEditText cognome;

    private TextView tvLoginHere;
    private Button btnRegister;

    private DatePickerDialog picker;
    private Calendar cldr;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String ruolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cldr = Calendar.getInstance();

        // Informazioni generali
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        etRegConfPass = findViewById(R.id.etRegConfPass);
        etRegTelefono = findViewById(R.id.etRegTelefono);
        etRegIndirizzo = findViewById(R.id.etRegIndirizzo);
        etRegRuolo = findViewById(R.id.etRegRuolo);
        nome = findViewById(R.id.nome);
        cognome = findViewById(R.id.cognome);
        data = findViewById(R.id.data);

        // Informazioni specifiche
        numEFNInputLayout = findViewById(R.id.numEFNInputLayout);
        piVAInputLayout = findViewById(R.id.piVAInputLayout);
        denomInputLayout = findViewById(R.id.denomInputLayout);
        isPrivatoInputLayout = findViewById(R.id.isPrivatoInputLayout);
        codFiscAssInputLayout = findViewById(R.id.codFiscAssInputLayout);
        nomeLayout = findViewById(R.id.nomeLayout);
        cognomeLayout = findViewById(R.id.cognomeLayout);
        dataLayout = findViewById(R.id.dataLayout);

        // Al click nel campo Nascita si apre il date picker
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                data.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        // Rendo visibili alcuni campi in base al ruolo
        etRegRuolo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: // propietario
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.GONE);
                        denomInputLayout.setVisibility(View.GONE);
                        isPrivatoInputLayout.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        mostraNomeCognomeNascita();

                        ruolo = "proprietario";
                        break;

                    case 1: // veterinaio
                        numEFNInputLayout.setVisibility(View.VISIBLE);
                        piVAInputLayout.setVisibility(View.VISIBLE);
                        denomInputLayout.setVisibility(View.GONE);
                        isPrivatoInputLayout.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        mostraNomeCognomeNascita();

                        ruolo = "veterinaio";
                        break;

                    case 2: // associazione
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.GONE);
                        denomInputLayout.setVisibility(View.VISIBLE);
                        isPrivatoInputLayout.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.VISIBLE);
                        nascondiNomeCognomeNascita();

                        ruolo = "associazione";
                        break;

                    case 3: // ente
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.VISIBLE);
                        denomInputLayout.setVisibility(View.VISIBLE);
                        isPrivatoInputLayout.setVisibility(View.VISIBLE );
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        nascondiNomeCognomeNascita();

                        ruolo = "ente";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(RegisterActivity.this, R.string.selectUserType, Toast.LENGTH_SHORT).show();
            }
        });

        etRegNumEFNOVI = findViewById(R.id.etRegNumEFNOVI);
        etRegPartitaIva = findViewById(R.id.etRegPartitaIva);
        etRegDenominazione=findViewById(R.id.etRegDenominazione);
        etRegIsPrivato = findViewById(R.id.etRegIsPrivato);
        etRegCodiceFiscaleAssociazione = findViewById(R.id.etRegCodiceFiscaleAssociazione);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void mostraNomeCognomeNascita() {
        nomeLayout.setVisibility(View.VISIBLE);
        cognomeLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.VISIBLE);
    }

    private void nascondiNomeCognomeNascita() {
        nomeLayout.setVisibility(View.GONE);
        cognomeLayout.setVisibility(View.GONE);
        dataLayout.setVisibility(View.GONE);
    }

    private void createUser(){
        String email;
        String password;
        String confPassword;
        String telefono;
        String indirizzo;
        String efnovi;
        String partitaIva;
        String codiceFiscaleAssociazione, denominazione;

        email = etRegEmail.getText().toString();
        password = etRegPassword.getText().toString();
        confPassword= etRegConfPass.getText().toString();
        telefono= etRegTelefono.getText().toString();
        indirizzo = etRegIndirizzo.getText().toString();

        efnovi = etRegNumEFNOVI.getText().toString();
        partitaIva = etRegPartitaIva.getText().toString();

        codiceFiscaleAssociazione = etRegCodiceFiscaleAssociazione.getText().toString();
        denominazione = etRegDenominazione.getText().toString();

        Map<String,String> indirizzoMap = new HashMap<>();
        indirizzoMap.put("via", indirizzo);

        // Controllo se gli input sono corretti
        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("*Email obbligatoria");
            etRegEmail.requestFocus();
            return;
        }else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("*Password obbligatoria");
            etRegPassword.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(confPassword)){
            etRegConfPass.setError("*Password obbligatoria");
            etRegConfPass.requestFocus();
            return;
        }
        else if(!confPassword.equals(password)){
            etRegConfPass.setError("Le password non combaciano");
            etRegConfPass.requestFocus();
            return;
        }

        switch (ruolo){
            case "proprietario":
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                            Persona p =new Persona(email,telefono, indirizzoMap, ruolo, nome.getText().toString(), cognome.getText().toString(), data.getText().toString());
                            db.collection("utenti").document(email+"").set(p);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "veterinario":
                // TODO: fare controlli sui campi efnovi e partitaIva

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                            Veterinario v =new Veterinario(email, telefono, indirizzoMap, ruolo, nome.getText().toString(), cognome.getText().toString(), data.getText().toString(), efnovi, partitaIva);
                            db.collection("utenti").document(email+"").set(v);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "associazione":
                // TODO: fare controlli sui campi cf e denominazione

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                            Associazione a =new Associazione(email, telefono, indirizzoMap, ruolo, codiceFiscaleAssociazione, denominazione);
                            db.collection("utenti").document(email+"").set(a);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "ente":
                // TODO: fare controlli sui campi

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                            Ente e =new Ente(email, telefono, indirizzoMap, ruolo, partitaIva, denominazione, true);
                            db.collection("utenti").document(email+"").set(e);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

}