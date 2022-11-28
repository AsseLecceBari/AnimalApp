package profiloUtente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import adapter.AnimalAdapter;
import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Persona;
import model.Utente;

public class ProfiloUtenteActivity extends AppCompatActivity {
    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    private TextView tipoUtente, denominazione, cf, nome, cognome, data, email, telefono, indirizzo, citta, efnovi, partitaIva;
    //private MaterialButton modificaProfilo;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        //link
        tipoUtente = findViewById(R.id.tipoUtente);
        denominazione = findViewById(R.id.denominazione);
        cf = findViewById(R.id.codiceFiscale);
        nome = findViewById(R.id.nome);
        cognome = findViewById(R.id.cognome);
        data = findViewById(R.id.data);
        email = findViewById(R.id.email);
        telefono = findViewById(R.id.telefono);
        indirizzo = findViewById(R.id.indirizzo);
        citta = findViewById(R.id.citta);
        efnovi = findViewById(R.id.numeroEFNOVI);
        partitaIva = findViewById(R.id.partitaIva);
        //modificaProfilo = findViewById(R.id.modificaProfilo);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Imposto l'actionBar di questa activity
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Profilo");
        setSupportActionBar(main_action_bar);

        CollectionReference animaliReference=db.collection("utenti");
        if(auth.getCurrentUser()!=null) {
            Query query = animaliReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        String ruolo = null;

                        for(QueryDocumentSnapshot document : task.getResult()){
                            ruolo = document.get("ruolo").toString();
                            switch(ruolo){
                                case "proprietario":
                                    tipoUtente.setText(document.get("ruolo").toString());
                                    nome.setText(document.get("nome").toString());
                                    cognome.setText(document.get("cognome").toString());
                                    data.setText(document.get("dataDiNascita").toString());
                                    email.setText(document.get("email").toString());
                                    break;
                                case "ente":

                                    break;
                                case "associazione":

                                    break;
                                case "veterinario":

                                    break;
                            }
                        }
                    }

                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_profile, menu);
        return true;
    }

    public void logout(MenuItem item) {
        auth.signOut();
        Toast.makeText(getApplicationContext(), R.string.logoutSuccess, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}