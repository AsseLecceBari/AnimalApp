package fragments_segnalazioni;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class fragment_vista_news extends Fragment implements OnMapReadyCallback {
    private TextView descrizioneNews;
    private TextView titoloReportNews;
    private ImageView immagineNews;
    private TextView dataNews;

    private FirebaseStorage storage;
    private StorageReference storageRef;


    private static final String ARG_PARAM1 = "obj";
    private Segnalazione s;

    private MapView mapViewNews;
    private static final String MAPVIEW_BUNDLE_KEY="MapViewBundleKey";



    private UiSettings mUiSettings;
    private Toolbar main_action_bar;
    public fragment_vista_news() {
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
        View rootView = inflater.inflate(R.layout.fragment_vista_news, container, false);
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


        mapViewNews=(MapView) rootView.findViewById(R.id.mapViewNews);
        mapViewNews.onCreate(mapViewBundle);
        mapViewNews.getMapAsync(this);//imposta un oggeto di callback che verrà attivato quando l'istanza di google map è pronta per essere utilizzata
        descrizioneNews=rootView.findViewById(R.id.descrizioneNews);
        titoloReportNews=rootView.findViewById(R.id.titoloReportNews);
        descrizioneNews.setText(s.getDescrizione());
        titoloReportNews.setText(s.getTitolo());

        dataNews=rootView.findViewById(R.id.dataNews);
        dataNews.setText("pubblicata il:"+s.getData());

        immagineNews=rootView.findViewById(R.id.immagineNews);

        //setto immagine
        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        storageRef.child(s.getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(immagineNews.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineNews);
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
        mapViewNews.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapViewNews.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapViewNews.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapViewNews.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewNews.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewNews.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewNews.onLowMemory();
    }

    public Fragment newInstance(Segnalazione param1) {
        fragment_vista_news fragment = new fragment_vista_news();
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
}