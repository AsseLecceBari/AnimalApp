package fragments_mieiAnimali;

import static it.uniba.dib.sms2223_2.R.string.aggiunto;
import static it.uniba.dib.sms2223_2.R.string.errore;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Carico;

public class aggiungiCarico extends Fragment {
    private CodeScanner mCodeScanner;
    private FloatingActionButton aggiungi;
    private TextView dati;
    private Carico carico;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ColorStateList coloreDati;
    private CodeScannerView scannerView;
    private Toast toast;
    private Toolbar main_action_bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_aggiungi_carico, container, false);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Scan QrCode");
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
            main_action_bar.getMenu().clear();
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().onBackPressed();
                }
            });
        }
        dati = root.findViewById(R.id.datiAnimale);
        aggiungi = root.findViewById(R.id.aggiungi);
        coloreDati = dati.getTextColors();

        scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // se l'idAnimale è appartenente ad un animale che non è in carico a nessuno allora passo alla fase di conferma presa in carico
                        esisteAnimale(result.getText());

                        if (toast != null) {
                            toast.cancel();
                            toast = null;
                        }else{
                            toast = Toast.makeText(getActivity().getApplicationContext(), R.string.rilevazione, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        });

        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aggiungi il carico al db
                if(carico != null){
                    db.collection("carichi").document(carico.getId()).set(carico).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity().getApplicationContext(), aggiunto, Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), errore, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private void esisteAnimale(String idAnimale) {
        // mi riempio la field dati
        CollectionReference docRef = db.collection("animali");
        Query query = docRef.whereEqualTo("idAnimale", idAnimale);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Animale a = document.toObject(Animale.class);
                        controllo(idAnimale, a);
                        break;
                    }
                    if(task.getResult().isEmpty()){
                        dati.setText(R.string.impossibile_aggiungere_nessun_risultato);
                        aggiungi.setVisibility(View.GONE);
                        dati.setAllCaps(false);
                        dati.setTextColor(Color.RED);
                        carico = null;

                        mCodeScanner.startPreview();
                    }
                }else{
                    dati.setText(R.string.impossibile_aggiungere_non_successo);
                    aggiungi.setVisibility(View.GONE);
                    dati.setAllCaps(false);
                    dati.setTextColor(Color.RED);
                    carico = null;
                    mCodeScanner.startPreview();
                }
            }
        });
    }

    private void controllo(String idAnimale, Animale a) {
        // controlli per la checkbox in carico
        CollectionReference reference=db.collection("carichi");
        if(auth.getCurrentUser()!=null) {
            Query query = reference.whereEqualTo("idAnimale", idAnimale)
                                   .whereEqualTo("inCorso", true);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()){
                            // non ce nessuno in corso
                            aggiungi.setVisibility(View.VISIBLE);
                            carico = new Carico(new Random().nextInt(999999999)+"", new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString()+"", "datafine", idAnimale, auth.getCurrentUser().getEmail(), "nota prova", true);

                            // mi riempio la field dati
                            dati.setText(a.getNome() + ",  " + a.getGenere() + getString(R.string.puo_essere_preso_in_carico_premere_pulsante_verde));
                            dati.setAllCaps(true);
                            dati.setTextColor(coloreDati);


                            mCodeScanner.startPreview();
                        }else{
                            // ce gia qualcuno in corso
                            aggiungi.setVisibility(View.GONE);
                            dati.setText(R.string.attualmente_gia_in_carico);
                            dati.setAllCaps(false);
                            dati.setTextColor(Color.RED);
                            carico = null;

                            mCodeScanner.startPreview();
                        }
                    }else {
                        aggiungi.setVisibility(View.GONE);
                        dati.setText(R.string.impossibile_aggiungere_tocca_il_qr_Code);
                        dati.setAllCaps(false);
                        dati.setTextColor(Color.RED);
                        carico = null;

                        mCodeScanner.startPreview();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);
            MainActivity mainActivity= (MainActivity) getActivity();
            mainActivity.searchFilterListener();
        }
    }
}