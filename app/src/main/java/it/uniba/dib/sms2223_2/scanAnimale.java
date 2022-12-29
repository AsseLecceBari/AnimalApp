package it.uniba.dib.sms2223_2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import model.Animale;

public class scanAnimale extends Fragment {
    private CodeScanner mCodeScanner;
    private TextView dati;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ColorStateList coloreDati;
    private CodeScannerView scannerView;
    private Toast toast;
    private int controllo = 0;
    private Animale a = null;

    public scanAnimale(int n, Animale a){
        this.a = a;
        controllo = 1;
    }

    public scanAnimale(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_animale, container, false);
        final Activity activity = getActivity();

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        scannerView = rootView.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(controllo == 0)
                            esisteAnimale(result.getText());
                        if(controllo == 1)
                            passaggioProprieta(result.getText());
                    }
                });
            }
        });

        return rootView;
    }

    private void passaggioProprieta(String emailProprietario) {
        // dialog conferma (se è emailProprietario esiste) -- l'oggetto animale da modificare è chiamato "a"


        // toast di errore altrimenti
    }

    private void esisteAnimale(String idAnimale) {
        DocumentReference docRef = db.collection("animali").document(idAnimale);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Animale a = documentSnapshot.toObject(Animale.class);
                if(a!= null){
                    // todo intent per visuallizare
                    Intent i = new Intent(getActivity().getApplicationContext(), ProfiloAnimale.class); // todo non deve andare nella vista profilo ufficiale ma in una versione lite con il pulsante aggiungi al pokedex
                    i.putExtra("animale", a);
                    startActivity(i);
                }else{
                    mCodeScanner.startPreview();

                    if (toast != null) {
                        toast.cancel();
                        toast = null;
                    }else{
                        toast = Toast.makeText(getActivity().getApplicationContext(), "Animale non trovato!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
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