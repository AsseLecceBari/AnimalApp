package fragments_segnalazioni;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;
import model.Utente;


public class fragment_vista_animaleInPericolo extends Fragment implements OnMapReadyCallback {
    private TextView descrizioneAnimaleInPericolo;
    private TextView titoloReportAnimaleInPericolo;
    private ImageView immagineAnimaleInPericolo;
    private TextView dataAnimaleInPericolo,textViewMappa;
    private TextInputLayout updateTitoloLayout,updateDescrizioneLayout;
    private TextInputEditText updateTitoloText,updateDescrizioneText;
    private MaterialDivider dividerMappa1,dividerMappa2;

    private FirebaseStorage storage;
    private StorageReference storageRef;


    private static final String ARG_PARAM1 = "obj";
    private Segnalazione s;
    private static final String ARG_PARAM2 = "int";
    private int x;//0 viene dalla radio tutti, 1 dal radio mie segnalazioni

    private MapView mapViewAnimaleInPericolo;
    private static final String MAPVIEW_BUNDLE_KEY="MapViewBundleKey";
    private Toolbar main_action_bar;


    private UiSettings mUiSettings;

    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";
    private FloatingActionButton fab;
    private boolean expanded = false;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;
    private FloatingActionButton fabAction4;
    private float offset1;
    private float offset2;
    private float offset3;

    Utente utente;
    private FirebaseAuth auth;
    private FirebaseFirestore db;



    public fragment_vista_animaleInPericolo() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            s= (Segnalazione) getArguments().getSerializable(ARG_PARAM1);

            x=(int) getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView=inflater.inflate(R.layout.fragment_vista_animale_in_pericolo, container, false);
        auth= FirebaseAuth.getInstance();
        Bundle mapViewBundle=null;
        if(savedInstanceState!=null){
            mapViewBundle=savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(s.getTipo());
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
        main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);

        mapViewAnimaleInPericolo=(MapView) rootView.findViewById(R.id.mapViewAnimaleInPericolo);
        mapViewAnimaleInPericolo.onCreate(mapViewBundle);
        mapViewAnimaleInPericolo.getMapAsync(this);//imposta un oggeto di callback che verrà attivato quando l'istanza di google map è pronta per essere utilizzata
        descrizioneAnimaleInPericolo=rootView.findViewById(R.id.DescrizioneAnimaleInPericolo);
        titoloReportAnimaleInPericolo=rootView.findViewById(R.id.titoloReportAnimaleInPericolo);
        dataAnimaleInPericolo=rootView.findViewById(R.id.dataAnimaleInPericolo);
        immagineAnimaleInPericolo=rootView.findViewById(R.id.ImmagineAnimaleInPericolo);
        //editText per l'update della segnalazione
        updateDescrizioneLayout=rootView.findViewById(R.id.updateDescrizioneLayout);
        updateDescrizioneText=rootView.findViewById(R.id.updateDescrizioneText);
        updateTitoloLayout=rootView.findViewById(R.id.updateTitoloLayout);
        updateTitoloText=rootView.findViewById(R.id.updateTitoloText);
        textViewMappa=rootView.findViewById(R.id.textViewMappa);
        dividerMappa1=rootView.findViewById(R.id.dividerMappa1);
        dividerMappa2=rootView.findViewById(R.id.dividerMappa2);

        descrizioneAnimaleInPericolo.setText(s.getDescrizione());
        titoloReportAnimaleInPericolo.setText(s.getTitolo());
        dataAnimaleInPericolo.setText("pubblicata il:"+s.getData());



        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab =  rootView.findViewById(R.id.fab);
        fabAction4= rootView.findViewById(R.id.fab_fine_modifica);
        fabAction1 = rootView.findViewById(R.id.fab_preferiti);

        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina
        if (x==0){
            fabAction1.setVisibility(View.VISIBLE);
            fabAction1.setImageResource(android.R.drawable.ic_menu_share);
            fabAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    condividiSegnalazione(s);
                }
            });
        }else if(x==1){
            fabAction1.setVisibility(View.VISIBLE);
            fabAction1.setImageResource(android.R.drawable.ic_menu_share);
            fabAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    condividiSegnalazione(s);
                }
            });
        }




        fabAction2 = rootView.findViewById(R.id.fab_modifica);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il contatta, mentre da mie segnalazioni(x=1) ho il modifica
        if (x==0){
            fabAction2.setVisibility(View.VISIBLE);
            fabAction2.setImageResource(android.R.drawable.stat_sys_phone_call);
            fabAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trovaNumeroDiTelefono(s);
                }
            });
        }else if(x==1){
            fabAction2.setVisibility(View.VISIBLE);
            fabAction2.setImageResource(android.R.drawable.ic_menu_edit);
            fabAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fabAction2.setVisibility(View.GONE);

                    //Rendo a GONE le textView e a VISIBLE le editText
                    descrizioneAnimaleInPericolo.setVisibility(View.GONE);
                    titoloReportAnimaleInPericolo.setVisibility(View.GONE);
                    mapViewAnimaleInPericolo.setVisibility(View.GONE);
                    dividerMappa1.setVisibility(View.GONE);
                    dividerMappa2.setVisibility(View.GONE);
                    textViewMappa.setVisibility(View.GONE);



                    updateTitoloLayout.setVisibility(View.VISIBLE);
                    updateDescrizioneLayout.setVisibility(View.VISIBLE);
                    updateTitoloText.setText(s.getTitolo());
                    updateDescrizioneText.setText(s.getDescrizione());



                    fabAction4.setVisibility(View.VISIBLE);
                    fabAction4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            updateSegnalazione(s);

                        }
                    });


                }
            });
        }


        fabAction3 = rootView.findViewById(R.id.fab_elimina);
        //if per cambiare icona fab, se viene da radioTutti(x=0) ho il preferiti, mentre da mie segnalazioni(x=1) ho il elimina
        if (x==0){
            fabAction3.setVisibility(View.VISIBLE);
            fabAction3.setImageResource(android.R.drawable.sym_action_email);
            fabAction3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (auth.getCurrentUser()!=null){
                        composeEmail(auth.getCurrentUser().getEmail(),s.getEmailSegnalatore());
                    }else{
                        composeEmail(" ",s.getEmailSegnalatore());

                    }                }
            });
        }else if(x==1){
            //per ora non serve nella vista mieSegnalazioni
            //per ora non serve nella vista mieSegnalazioni
            fabAction3.setVisibility(View.VISIBLE);
            fabAction3.setImageResource(android.R.drawable.ic_menu_delete);
            fabAction3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "elimina", Toast.LENGTH_SHORT).show();
                    deleteReports(s);
                }
            });
        }
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
                if (x==1) {
                    fabAction4.setTranslationY(offset2);
                }
                offset3 = fab.getY() - fabAction3.getY();
                fabAction3.setTranslationY(offset3);

                return true;
            }
        });


        //setto immagine
        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        storageRef.child(s.getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(immagineAnimaleInPericolo.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineAnimaleInPericolo);
            }
        });




        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle=outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if(mapViewBundle==null){
            mapViewBundle=new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY,mapViewBundle);
        }
        mapViewAnimaleInPericolo.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewAnimaleInPericolo.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapViewAnimaleInPericolo.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapViewAnimaleInPericolo.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewAnimaleInPericolo.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewAnimaleInPericolo.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewAnimaleInPericolo.onLowMemory();
    }

    public Fragment newInstance(Segnalazione param1,int a) {
        fragment_vista_animaleInPericolo fragment = new fragment_vista_animaleInPericolo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2,a);
        fragment.setArguments(args);

        return fragment;


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mUiSettings = googleMap.getUiSettings();




        googleMap.addMarker(new MarkerOptions().position(new LatLng(s.getLatitudine(),s.getLongitudine())).title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(s.getLatitudine(),s.getLongitudine()),10));
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setMapToolbarEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        googleMap.setPadding(0,0,0,150);
    }


    //metodi per le animazioni del FAB SPEED DIAL
    /*
    AnimatoreSet serve per impostare le animazioni sugli oggetti

    Abbiamo due possibilità:
        playTogheter() ( si trova nel collapse) che li fa muovere contemporaneamente
        playSequentially (si trova nell'expand) che li fa muovere sequenzialmente
     */

    //TODO: DA SCEGLIERE SE PLAYTOGHETER O PLAY SEQUENTIALLY
    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3),createCollapseAnimator(fabAction4, offset2));
        animatorSet.start();
        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3),createExpandAnimator(fabAction4, offset2));
        animatorSet.start();
        //animateFab();
    }


    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    /*
    SERVONO PER CREARE I MOVIMENTI DELLE ANIMAZIONI CHE VENGONO POI CHIAMATI DAI VARI AnimatorSet
     */
    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    //SERVE PER IMPOSTARE L'ANIMAZIONE AL FAB GENERALE
   /*private void animateFab() {
        Drawable drawable = fab.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }*/

    //per trovare il numero di telefono del segnalatore
    private void trovaNumeroDiTelefono(Segnalazione s) {


        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        db= FirebaseFirestore.getInstance();

        // auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("utenti");


        segnalazioniRef.whereEqualTo("email",s.getEmailSegnalatore()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Salvare animale in un array con elementi oggetto animale

                        //Passo i dati presi dal database all'adapter
                        utente=document.toObject(Utente.class);
                    }
                    if (utente!=null) {
                        String telefono = utente.getTelefono();
                        dialPhoneNumber(telefono);
                    }else{
                        Toast.makeText(getContext(), "Numero di telefono non presente,impossibile contattare", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    //intent per aprire l'app del telefono con il numero del segnalatore
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void composeEmail(String addresses,String toAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+toAddress)); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        startActivity(intent);

    }

    public void condividiSegnalazione(Segnalazione s) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Puoi raggiungere il luogo di questa segnalazione in questo punto: http://www.google.com/maps/place/"+s.getLatitudine()+","+s.getLongitudine();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Condividi via..."));
    }

    public void deleteReports(Segnalazione s){
        Log.d("provaelimina","sono dentro");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.ConfermaEliminaSegnalazione)
                .setTitle("Elimina Segnalazione").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("provaelimina","sono dentro");
                        db=FirebaseFirestore.getInstance();
                        CollectionReference segnalazioniRef=db.collection("segnalazioni");
                        segnalazioniRef.document(s.getIdSegnalazione()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Segnalazione eliminata correttamente", Toast.LENGTH_SHORT).show();
                                //prova per far tornare indietro al fragment che contiene la lista dei report
                                getActivity().onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Errore nell'eliminazione", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.setCancelable(true);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }

    public void updateSegnalazione(Segnalazione s){
        db=FirebaseFirestore.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");

        String titolo,descrizione;

       /* titolo = updateTitoloText.getText().toString();
        if (titolo.equals("")){
            titolo=s.getTitolo();
        }


        descrizione = updateDescrizioneText.getText().toString();
        if (descrizione.equals("")){
             descrizione=s.getDescrizione();
          }*/


        titolo = updateTitoloText.getText().toString();
        descrizione = updateDescrizioneText.getText().toString();

        segnalazioniRef.document(s.getIdSegnalazione()).update("titolo",titolo,"descrizione",descrizione).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Modifica effettuata", Toast.LENGTH_SHORT).show();
                descrizioneAnimaleInPericolo.setVisibility(View.VISIBLE);
                titoloReportAnimaleInPericolo.setVisibility(View.VISIBLE);

                updateTitoloLayout.setVisibility(View.GONE);
                updateDescrizioneLayout.setVisibility(View.GONE);
                fabAction4.setVisibility(View.GONE);

                mapViewAnimaleInPericolo.setVisibility(View.VISIBLE);
                dividerMappa1.setVisibility(View.VISIBLE);
                dividerMappa2.setVisibility(View.VISIBLE);
                textViewMappa.setVisibility(View.VISIBLE);

                fabAction2.setVisibility(View.VISIBLE);


                updateTitoloText.getText().clear();
                updateDescrizioneText.getText().clear();

                //RECUPERO LA SEGNALAZIONE APPENA MODIFICATA PER POPOLARE LE TEXTVIEW
                DocumentReference docRef = db.collection("segnalazioni").document(s.getIdSegnalazione());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Segnalazione s1 = documentSnapshot.toObject(Segnalazione.class);
                        descrizioneAnimaleInPericolo.setText(s1.getDescrizione());
                        titoloReportAnimaleInPericolo.setText(s1.getTitolo());
                        aggiornaSegnalazione(s);



                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Errore nella modifica", Toast.LENGTH_SHORT).show();
                descrizioneAnimaleInPericolo.setVisibility(View.VISIBLE);
                titoloReportAnimaleInPericolo.setVisibility(View.VISIBLE);
                updateTitoloLayout.setVisibility(View.GONE);
                updateDescrizioneLayout.setVisibility(View.GONE);
                fabAction2.setVisibility(View.VISIBLE);
                fabAction4.setVisibility(View.GONE);
            }
        });


    }

    public void aggiornaSegnalazione(Segnalazione s1){

        db= FirebaseFirestore.getInstance();

        // auth=FirebaseAuth.getInstance();
        CollectionReference segnalazioniRef=db.collection("segnalazioni");


        segnalazioniRef.whereEqualTo("idSegnalazione",s1.getIdSegnalazione()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Salvare animale in un array con elementi oggetto animale

                        //Passo i dati presi dal database all'adapter
                        s=document.toObject(Segnalazione.class);
                    }





                } else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }
            }
        });

    }

}