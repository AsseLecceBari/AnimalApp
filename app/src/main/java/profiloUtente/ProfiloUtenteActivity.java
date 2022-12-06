package profiloUtente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Associazione;
import model.Ente;
import model.Persona;
import model.Veterinario;

public class ProfiloUtenteActivity extends AppCompatActivity {
    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    private TextView tipoUtente, denominazione, cf, nome, cognome, data, email, telefono, indirizzo, citta, efnovi, partitaIva;
    private FloatingActionButton modificaProfilo;

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
        data = findViewById(R.id.motivoConsultazione);
        email = findViewById(R.id.email);
        telefono = findViewById(R.id.telefono);
        indirizzo = findViewById(R.id.indirizzo);
        citta = findViewById(R.id.citta);
        efnovi = findViewById(R.id.numeroEFNOVI);
        partitaIva = findViewById(R.id.partitaIva);
        modificaProfilo = findViewById(R.id.modificaProfilo);

        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Modifica il tuo profilo", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Imposto l'actionBar di questa activity
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Profilo");
        setSupportActionBar(main_action_bar);
        main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
        main_action_bar.setNavigationIcon(R.drawable.back);
        main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });

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
                                    Persona p = document.toObject(Persona.class);

                                    indirizzo.setText(p.getIndirizzo().get("viaCivico"));
                                    citta.setText(p.getIndirizzo().get("citta"));
                                    tipoUtente.setText(p.getRuolo());
                                    nome.setText(p.getNome());
                                    cognome.setText(p.getCognome());
                                    data.setText(p.getDataDiNascita());
                                    email.setText(p.getEmail());
                                    telefono.setText(p.getTelefono());

                                    indirizzo.setVisibility(View.VISIBLE);
                                    citta.setVisibility(View.VISIBLE);
                                    tipoUtente.setVisibility(View.VISIBLE);
                                    nome.setVisibility(View.VISIBLE);
                                    cognome.setVisibility(View.VISIBLE);
                                    data.setVisibility(View.VISIBLE);
                                    email.setVisibility(View.VISIBLE);
                                    telefono.setVisibility(View.VISIBLE);

                                    denominazione.setVisibility(View.GONE);
                                    cf.setVisibility(View.GONE);
                                    efnovi.setVisibility(View.GONE);
                                    partitaIva.setVisibility(View.GONE);
                                    break;

                                case "ente":
                                    Ente e = document.toObject(Ente.class);

                                    if(e.isPrivato())
                                        tipoUtente.setText(e.getRuolo() + " privato");
                                    else
                                        tipoUtente.setText(e.getRuolo() + " pubblico");
                                    indirizzo.setText(e.getIndirizzo().get("viaCivico"));
                                    citta.setText(e.getIndirizzo().get("citta"));
                                    email.setText(e.getEmail());
                                    telefono.setText(e.getTelefono());
                                    denominazione.setText(e.getDenominazione());
                                    partitaIva.setText(e.getPartitaIva());

                                    tipoUtente.setVisibility(View.VISIBLE);
                                    indirizzo.setVisibility(View.VISIBLE);
                                    citta.setVisibility(View.VISIBLE);
                                    email.setVisibility(View.VISIBLE);
                                    telefono.setVisibility(View.VISIBLE);
                                    denominazione.setVisibility(View.VISIBLE);
                                    partitaIva.setVisibility(View.VISIBLE);

                                    cf.setVisibility(View.GONE);
                                    efnovi.setVisibility(View.GONE);
                                    nome.setVisibility(View.GONE);
                                    cognome.setVisibility(View.GONE);
                                    data.setVisibility(View.GONE);
                                    break;

                                case "associazione":
                                    Associazione a = document.toObject(Associazione.class);

                                    indirizzo.setText(a.getIndirizzo().get("viaCivico"));
                                    citta.setText(a.getIndirizzo().get("citta"));
                                    tipoUtente.setText(a.getRuolo());
                                    email.setText(a.getEmail());
                                    telefono.setText(a.getTelefono());
                                    cf.setText(a.getCodiceFiscaleAssociazione());
                                    denominazione.setText((a.getDenominazione()));


                                    indirizzo.setVisibility(View.VISIBLE);
                                    citta.setVisibility(View.VISIBLE);
                                    tipoUtente.setVisibility(View.VISIBLE);
                                    email.setVisibility(View.VISIBLE);
                                    telefono.setVisibility(View.VISIBLE);
                                    denominazione.setVisibility(View.VISIBLE);
                                    cf.setVisibility(View.VISIBLE);

                                    nome.setVisibility(View.GONE);
                                    cognome.setVisibility(View.GONE);
                                    data.setVisibility(View.GONE);
                                    efnovi.setVisibility(View.GONE);
                                    partitaIva.setVisibility(View.GONE);
                                    break;

                                case "veterinario":
                                    Veterinario v = document.toObject(Veterinario.class);

                                    indirizzo.setText(v.getIndirizzo().get("viaCivico"));
                                    citta.setText(v.getIndirizzo().get("citta"));
                                    tipoUtente.setText(v.getRuolo());
                                    nome.setText(v.getNome());
                                    cognome.setText(v.getCognome());
                                    data.setText(v.getDataDiNascita());
                                    email.setText(v.getEmail());
                                    telefono.setText(v.getTelefono());
                                    efnovi.setText(v.getNumEFNOVI());
                                    partitaIva.setText(v.getPartitaIva());

                                    indirizzo.setVisibility(View.VISIBLE);
                                    citta.setVisibility(View.VISIBLE);
                                    tipoUtente.setVisibility(View.VISIBLE);
                                    nome.setVisibility(View.VISIBLE);
                                    cognome.setVisibility(View.VISIBLE);
                                    data.setVisibility(View.VISIBLE);
                                    email.setVisibility(View.VISIBLE);
                                    telefono.setVisibility(View.VISIBLE);
                                    efnovi.setVisibility(View.VISIBLE);
                                    partitaIva.setVisibility(View.VISIBLE);

                                    denominazione.setVisibility(View.GONE);
                                    cf.setVisibility(View.GONE);
                                    break;
                            }
                            tipoUtente.setAllCaps(true);
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