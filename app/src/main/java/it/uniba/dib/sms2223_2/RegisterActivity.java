package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import model.Utente;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
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
        String telefono="";
        Map<String,String> indirizzo = new HashMap<>();
        int ruolo=0;

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("*Email obbligatoria");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("*Password obbligatoria");
            etRegPassword.requestFocus();
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
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                    Utente u = new Utente(email + "", telefono + "", indirizzo, ruolo);
                    db.collection("prova").add(u);
                } else {
                    Toast.makeText(RegisterActivity.this, "Errore di registrazione: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}