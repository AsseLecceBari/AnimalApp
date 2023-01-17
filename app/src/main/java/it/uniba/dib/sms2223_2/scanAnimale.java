package it.uniba.dib.sms2223_2;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import DB.AnimaleDB;
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
    private String idAdozione;
    private Toolbar main_action_bar;

    public scanAnimale(int n, Animale a, String idAdozione){
        this.a = a;
        controllo = 1;
        this.idAdozione = idAdozione;
    }

    public scanAnimale(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_animale, container, false);
        final Activity activity = getActivity();

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
        // todo richiedere permessi -----------------------------------------------------------------------------------
        // dialog conferma (se è emailProprietario esiste)

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        DocumentReference docIdRef = rootRef.collection("utenti").document(emailProprietario);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        a.setEmailProprietario(emailProprietario);

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Cedi animale")
                                .setMessage("Sei DAVVERO sicuro di voler cedere la proprità?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(getActivity().getApplicationContext(), idAdozione+"L'animale ora ha un nuovo proprietario!", Toast.LENGTH_SHORT).show();
                                        db.collection("animali").document(a.getIdAnimale()).set(a);

                                        // elimino l'annuncio di adozione
                                        db.collection("adozioni").document(idAdozione).delete();

                                        // indietro
                                        getActivity().onBackPressed();
                                        getActivity().onBackPressed();
                                        getActivity().recreate();
                                    }})
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Annullato!", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    }}).show();

                    } else {
                        Log.d(TAG, "Document does not exist!");
                        Toast.makeText(getActivity().getApplicationContext(), "Qr code non riconosciuto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    Toast.makeText(getActivity().getApplicationContext(), "Errore!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);
        }
    }
}