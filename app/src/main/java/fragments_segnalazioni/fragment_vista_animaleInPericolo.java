package fragments_segnalazioni;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;


public class fragment_vista_animaleInPericolo extends Fragment implements OnMapReadyCallback {
    private TextView descrizioneAnimaleInPericolo;
    private TextView titoloReportAnimaleInPericolo;
    private ImageView immagineAnimaleInPericolo;
    private TextView dataAnimaleInPericolo;

    private FirebaseStorage storage;
    private StorageReference storageRef;


    private static final String ARG_PARAM1 = "obj";
    private Segnalazione s;

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
    private float offset1;
    private float offset2;



    public fragment_vista_animaleInPericolo() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            s= (Segnalazione) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView=inflater.inflate(R.layout.fragment_vista_animale_in_pericolo, container, false);
        Bundle mapViewBundle=null;
        if(savedInstanceState!=null){
            mapViewBundle=savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(s.getTipo());
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
            main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().onBackPressed();
                }
            });
        }


        mapViewAnimaleInPericolo=(MapView) rootView.findViewById(R.id.mapViewAnimaleInPericolo);
        mapViewAnimaleInPericolo.onCreate(mapViewBundle);
        mapViewAnimaleInPericolo.getMapAsync(this);//imposta un oggeto di callback che verrà attivato quando l'istanza di google map è pronta per essere utilizzata
        descrizioneAnimaleInPericolo=rootView.findViewById(R.id.DescrizioneAnimaleInPericolo);
        titoloReportAnimaleInPericolo=rootView.findViewById(R.id.titoloReportAnimaleInPericolo);
        descrizioneAnimaleInPericolo.setText(s.getDescrizione());
        titoloReportAnimaleInPericolo.setText(s.getTitolo());

        dataAnimaleInPericolo=rootView.findViewById(R.id.dataAnimaleInPericolo);
        dataAnimaleInPericolo.setText("pubblicata il:"+s.getData());

        immagineAnimaleInPericolo=rootView.findViewById(R.id.ImmagineAnimaleInPericolo);

        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab =  rootView.findViewById(R.id.fab);
        fabAction1 = rootView.findViewById(R.id.fab_action_1);
        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Aggiungi ai preferiti", Toast.LENGTH_SHORT).show();
            }
        });

        fabAction2 = rootView.findViewById(R.id.fab_action_2);
        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Contatta", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewAnimaleInPericolo.onLowMemory();
    }

    public Fragment newInstance(Segnalazione param1) {
        fragment_vista_animaleInPericolo fragment = new fragment_vista_animaleInPericolo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.imgProfiloItem);
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.setNavigationIcon(null);
        }
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
                createCollapseAnimator(fabAction2, offset2));
        animatorSet.start();
        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2));
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

}