package fragments_mieiAnimali;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.time.temporal.Temporal;
import java.util.Random;

import it.uniba.dib.sms2223_2.R;
import model.Carico;

public class aggiungiCarico extends Fragment {
    private CodeScanner mCodeScanner;
    private FloatingActionButton aggiungi;
    private TextView dati;
    private Carico carico;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private boolean res;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_aggiungi_carico, container, false);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        dati = root.findViewById(R.id.datiAnimale);
        aggiungi = root.findViewById(R.id.aggiungi);


        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                        // se l'idAnimale è appartenente ad un animale che non è in carico a nessuno
                        // allora passo alla fase di conferma presa in carico
                        if(isAllow("-105106619")){ // todo inserire come parametro result.getText()
                            aggiungi.setVisibility(View.VISIBLE);
                            // todo mettere dati reali del carico
                            carico = new Carico(new Random().nextInt(999999999)+"", "datainizio", "datafine", "idAnimale", "idProfessionista", "nota", true);
                        }else{
                            aggiungi.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aggiungi il carico al db
                db.collection("carichi").document(carico.getId()).set(carico);
                // todo fare la intent
            }
        });
        return root;
    }

    private boolean isAllow(String idAnimale) {
        // controlli per la checkbox in carico
        CollectionReference reference=db.collection("carichi");
        if(auth.getCurrentUser()!=null) {
            Query query = reference.whereEqualTo("idProfessionista", auth.getCurrentUser().getEmail())
                                   .whereEqualTo("idAnimale", idAnimale)
                                   .whereEqualTo("isInCorso", true);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()){
                            // non ce nessuno in corso
                            res = true;
                            Log.e("test", "1");
                        }else{
                            res = false;
                            Log.e("test", "2");
                        }
                    }else{
                        res = true;
                        Log.e("test", "3");
                    }
                }
            });
            Log.e("test", res+" - -");
            return res;  // todo res sembra asincrono e rimane false quando viene restituito
        }

        return false;
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
}