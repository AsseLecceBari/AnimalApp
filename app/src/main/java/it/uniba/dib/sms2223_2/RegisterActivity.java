package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import model.Utente;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout numEFNInputLayout;
    TextInputLayout piVAInputLayout;
    TextInputLayout denomInputLayout;
    TextInputLayout isPrivatoInputLayout;
    TextInputLayout codFiscAssInputLayout;


    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextInputEditText etRegConfPass;
    TextInputEditText etRegTelefono;
    TextInputEditText etRegIndirizzo;
    Spinner etRegRuolo;
    TextInputEditText etRegNumEFNOVI;
    TextInputEditText etRegPartitaIva;
    TextInputEditText etRegDenominazione;
    TextInputEditText etRegIsPrivato;
    TextInputEditText etRegCodiceFiscaleAssociazione;
    TextView tvLoginHere;
    Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        etRegConfPass=findViewById(R.id.etRegConfPass);
        etRegTelefono = findViewById(R.id.etRegTelefono);
        etRegIndirizzo = findViewById(R.id.etRegIndirizzo);
        etRegRuolo=findViewById(R.id.etRegRuolo);

        numEFNInputLayout=findViewById(R.id.numEFNInputLayout);
        piVAInputLayout=findViewById(R.id.piVAInputLayout);
        denomInputLayout=findViewById(R.id.denomInputLayout);
        isPrivatoInputLayout=findViewById(R.id.isPrivatoInputLayout);
        codFiscAssInputLayout=findViewById(R.id.codFiscAssInputLayout);
        etRegRuolo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    numEFNInputLayout.setVisibility(View.GONE);
                    piVAInputLayout.setVisibility(View.GONE);
                    denomInputLayout.setVisibility(View.GONE);
                    isPrivatoInputLayout.setVisibility(View.GONE);
                    codFiscAssInputLayout.setVisibility(View.GONE);
                }else if(i==1){
                    numEFNInputLayout.setVisibility(View.VISIBLE);
                    piVAInputLayout.setVisibility(View.VISIBLE);
                    denomInputLayout.setVisibility(View.GONE);
                    isPrivatoInputLayout.setVisibility(View.GONE);
                    codFiscAssInputLayout.setVisibility(View.GONE);
                }else if(i==2){
                    numEFNInputLayout.setVisibility(View.GONE);
                    piVAInputLayout.setVisibility(View.GONE);
                    denomInputLayout.setVisibility(View.VISIBLE);
                    isPrivatoInputLayout.setVisibility(View.GONE);
                    codFiscAssInputLayout.setVisibility(View.VISIBLE);
                }else if(i==3){
                    numEFNInputLayout.setVisibility(View.GONE);
                    piVAInputLayout.setVisibility(View.VISIBLE);
                    denomInputLayout.setVisibility(View.VISIBLE);
                    isPrivatoInputLayout.setVisibility(View.VISIBLE );
                    codFiscAssInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    private void createUser(){
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();
        String confPassword= etRegConfPass.getText().toString();
        String telefono="";
        Map<String,String> indirizzo = new HashMap<>();
        int ruolo=0;

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("*Email obbligatoria");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("*Password obbligatoria");
            etRegPassword.requestFocus();
        }
        else if (TextUtils.isEmpty(confPassword)){
            etRegConfPass.setError("*Password obbligatoria");
            etRegConfPass.requestFocus();
        }
        else if(!confPassword.equals(password)){
            etRegConfPass.setError("Le password non combaciano");
            etRegConfPass.requestFocus();
        }else{
           create(email,password);

        }
    }
    private void  create(String email,String password){
        String telefono="2131";
        Map<String,String> indirizzo = new HashMap<>();
        indirizzo.put("via","g.verdi");
        String ruolo="";
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                    Utente u =new Utente(email+"",telefono+"",indirizzo,ruolo);
                    db.collection("utenti").document(email+"").set(u);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}