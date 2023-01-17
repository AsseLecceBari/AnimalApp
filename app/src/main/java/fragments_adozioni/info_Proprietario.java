package fragments_adozioni;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
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

    private String email1, telefono1;

    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";
    private FloatingActionButton fab, modificaAnimaliBtn;
    private boolean expanded = false;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;
    private float offset1;
    private float offset2;
    private float offset3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_info__proprietario, container, false);

        fab =  rootView.findViewById(R.id.fabAnimals);

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

        Query query = animaliReference.whereEqualTo("email", animale.getEmailProprietario());
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
                                email1 = p.getEmail();
                                telefono1 =  p.getTelefono();
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
                                email1 = e.getEmail();
                                telefono1 =  e.getTelefono();
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

                                email1 = a.getEmail();
                                telefono1 =  a.getTelefono();
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

                                email1 = v.getEmail();
                                telefono1 =  v.getTelefono();
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



        // fab per contattare
        fab(rootView);

        return rootView;
    }

    private void fab(View rootView) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */



        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
        CollectionReference collection = db.collection("richiestaCarico");
        if(auth.getCurrentUser()!= null) {
            Query query = collection.whereEqualTo("idVeterinario", auth.getCurrentUser().getEmail()).whereEqualTo("stato", "in sospeso");

            AggregateQuery countQuery = query.count();
            countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    task.addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                        }
                    });
                }
            });
        }
        fabAction1 = rootView.findViewById(R.id.aggiungiAnimaliBtn);
        fabAction1.setVisibility(View.VISIBLE);
        fabAction1.setImageDrawable(getResources().getDrawable(android.R.drawable.sym_action_email));
        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+ email1)); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, auth.getCurrentUser().getEmail());
                startActivity(intent);
            }
        });


        fabAction2 = rootView.findViewById(R.id.aggiungiAnimaliInCaricoBtn);
        fabAction2.setVisibility(View.VISIBLE);
        fabAction2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_call));
        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telefono1));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expandFab();

                } else {
                    collapseFab();
                }
            }
        });

        /*
        Restituisce ViewTreeObserver per la gerarchia di questa vista.
         L'osservatore dell'albero di visualizzazione può essere utilizzato per ricevere notifiche
         quando si verificano eventi globali, come il cambio del layout.
         */
        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - fabAction1.getY();
                fabAction1.setTranslationY(offset1);
                offset2 = fab.getY() - fabAction2.getY();
                fabAction2.setTranslationY(offset2);

                return true;
            }
        });
    }



    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3));
        animatorSet.start();

        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3));
        animatorSet.start();

        //animateFab();
    }

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
}