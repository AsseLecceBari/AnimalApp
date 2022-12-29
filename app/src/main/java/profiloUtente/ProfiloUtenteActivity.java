package profiloUtente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
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
    private QRGEncoder qrgEncoder;
    private ImageView qrCodeIV;
    private Bitmap bitmap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        qr();

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

    private void qr() {
        qrCodeIV = findViewById(R.id.idIVQrcode);
        // setto il generatore
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.

        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(Objects.requireNonNull(auth.getCurrentUser()).getEmail(), null, QRGContents.Type.TEXT, dimen);
        // getting our qrcode in the form of bitmap.
        bitmap = qrgEncoder.getBitmap();
        // the bitmap is set inside our image
        // view using .setimagebitmap method.
        qrCodeIV.setImageBitmap(bitmap);
        if(qrgEncoder.getBitmap() != null)
            qrCodeIV.setVisibility(View.VISIBLE);
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
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        String ruolo = null;

                        for(QueryDocumentSnapshot document : task.getResult()){
                            ruolo = document.get("ruolo").toString();
                            switch(ruolo){
                                case "proprietario":
                                    Persona p = document.toObject(Persona.class);

                                    tipoUtente.setText(p.getRuolo());
                                    nome.setText(p.getNome());
                                    cognome.setText(p.getCognome());
                                    data.setText(p.getDataDiNascita());
                                    email.setText(p.getEmail());
                                    telefono.setText(p.getTelefono());
                                    indirizzo.setText(p.getIndirizzo().get("via") +", " +p.getIndirizzo().get("civico")+" "+p.getIndirizzo().get("città")+"("+p.getIndirizzo().get("provincia")+")");

                                            indirizzo.setVisibility(View.VISIBLE);
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
                                    email.setText(e.getEmail());
                                    telefono.setText(e.getTelefono());
                                    denominazione.setText(e.getDenominazione());
                                    partitaIva.setText(e.getPartitaIva());
                                    indirizzo.setText(e.getIndirizzo().get("via") +", " +e.getIndirizzo().get("civico")+" "+e.getIndirizzo().get("città")+"("+e.getIndirizzo().get("provincia")+")");


                                    tipoUtente.setVisibility(View.VISIBLE);
                                    indirizzo.setVisibility(View.VISIBLE);
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

                                    tipoUtente.setText(a.getRuolo());
                                    email.setText(a.getEmail());
                                    telefono.setText(a.getTelefono());
                                    cf.setText(a.getCodiceFiscaleAssociazione());
                                    denominazione.setText((a.getDenominazione()));
                                    indirizzo.setText(a.getIndirizzo().get("via") +", " +a.getIndirizzo().get("civico")+" "+a.getIndirizzo().get("città")+"("+a.getIndirizzo().get("provincia")+")");


                                    indirizzo.setVisibility(View.VISIBLE);
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

                                    tipoUtente.setText(v.getRuolo());
                                    nome.setText(v.getNome());
                                    cognome.setText(v.getCognome());
                                    data.setText(v.getDataDiNascita());
                                    email.setText(v.getEmail());
                                    telefono.setText(v.getTelefono());
                                    efnovi.setText(v.getNumEFNOVI());
                                    partitaIva.setText(v.getPartitaIva());
                                    indirizzo.setText(v.getIndirizzo().get("via") +", " +v.getIndirizzo().get("civico")+" "+v.getIndirizzo().get("città")+"("+v.getIndirizzo().get("provincia")+")");


                                    indirizzo.setVisibility(View.VISIBLE);
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