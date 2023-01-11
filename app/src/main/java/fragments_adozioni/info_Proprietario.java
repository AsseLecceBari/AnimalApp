package fragments_adozioni;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Associazione;
import model.Ente;
import model.Persona;
import model.Veterinario;

public class info_Proprietario extends Fragment {
    private Animale animale;
    private Adozione adozione;
    private Persona proprietario;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView tipoUtente, denominazione, cf, nome, cognome, data, email, telefono, indirizzo, citta, efnovi, partitaIva;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");
        proprietario=(Persona) getActivity().getIntent().getSerializableExtra("proprietario");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_info__proprietario, container, false);

        //link
        tipoUtente = rootView.findViewById(R.id.tipoUtente);
        denominazione = rootView.findViewById(R.id.denominazione);
        cf = rootView.findViewById(R.id.codiceFiscale);
        nome = rootView.findViewById(R.id.nome);
        cognome = rootView.findViewById(R.id.cognome);
        data = rootView.findViewById(R.id.motivoConsultazione);
        email = rootView.findViewById(R.id.email);
        telefono = rootView.findViewById(R.id.telefono);
        indirizzo = rootView.findViewById(R.id.indirizzo);
        efnovi = rootView.findViewById(R.id.numeroEFNOVI);
        partitaIva = rootView.findViewById(R.id.partitaIva);

        auth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        String NOME = getString(R.string.nomeduepunti);
        String COGNOME = getString(R.string.cogn);
        String EMAIL = getString(R.string.mail);
        String TELEFONO = getString(R.string.tel);
        String INDIRIZZO = getString(R.string.indiri);
        String DENOMINAZIONE = getString(R.string.denom);
        String PARTITA_IVA = getString(R.string.partiva);
        String DATA_DI_NASCITA = getString(R.string.datanascita);
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
                                    nome.setText(NOME +p.getNome());
                                    cognome.setText(COGNOME +p.getCognome());
                                    data.setText(DATA_DI_NASCITA +p.getDataDiNascita());
                                    email.setText(EMAIL +p.getEmail());
                                    telefono.setText(TELEFONO +p.getTelefono());
                                    indirizzo.setText(INDIRIZZO +p.getIndirizzo().get("via") +", " +p.getIndirizzo().get("civico")+" "+p.getIndirizzo().get("città")+"("+p.getIndirizzo().get("provincia")+")");

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
                                        tipoUtente.setText(e.getRuolo() + getString(R.string.priv));
                                    else
                                        tipoUtente.setText(e.getRuolo() + getString(R.string.pubbl));
                                    email.setText(EMAIL +e.getEmail());
                                    telefono.setText(TELEFONO +e.getTelefono());
                                    denominazione.setText(DENOMINAZIONE +e.getDenominazione());
                                    partitaIva.setText(PARTITA_IVA +e.getPartitaIva());
                                    indirizzo.setText(INDIRIZZO +e.getIndirizzo().get("via") +", " +e.getIndirizzo().get("civico")+" "+e.getIndirizzo().get("città")+"("+e.getIndirizzo().get("provincia")+")");


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
                                    email.setText(EMAIL +a.getEmail());
                                    telefono.setText(TELEFONO +a.getTelefono());
                                    cf.setText(getString(R.string.cf)+a.getCodiceFiscaleAssociazione());
                                    denominazione.setText(DENOMINAZIONE +(a.getDenominazione()));
                                    indirizzo.setText(INDIRIZZO +a.getIndirizzo().get("via") +", " +a.getIndirizzo().get("civico")+" "+a.getIndirizzo().get("città")+"("+a.getIndirizzo().get("provincia")+")");


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
                                    nome.setText(NOME +v.getNome());
                                    cognome.setText(COGNOME +v.getCognome());
                                    data.setText(DATA_DI_NASCITA +v.getDataDiNascita());
                                    email.setText(EMAIL +v.getEmail());
                                    telefono.setText(TELEFONO +v.getTelefono());
                                    efnovi.setText(getString(R.string.efnovi)+v.getNumEFNOVI());
                                    partitaIva.setText(PARTITA_IVA +v.getPartitaIva());
                                    indirizzo.setText(INDIRIZZO +v.getIndirizzo().get("via") +", " +v.getIndirizzo().get("civico")+" "+v.getIndirizzo().get("città")+"("+v.getIndirizzo().get("provincia")+")");


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


        return rootView;
    }
}