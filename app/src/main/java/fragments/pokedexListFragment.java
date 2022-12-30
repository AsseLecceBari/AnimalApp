package fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pokedexListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pokedexListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private Animale mParam1;
    private ImageView imageAnimalePokeView;
    private TextView nomeAnimale;
    private TextView genereAnimale;
    private TextView specieAnimale;
    private TextView sessoAnimale;
    private TextView dataNascitaAnimale;



    public pokedexListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static pokedexListFragment newInstance(Animale param1) {
        pokedexListFragment fragment = new pokedexListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Animale) getArguments().getSerializable(ARG_PARAM1);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_pokedex_list, container, false);
        imageAnimalePokeView=view.findViewById(R.id.imageAnimalePokeView);
        nomeAnimale = (TextView) view.findViewById(R.id.nomeAnimaleViewPoke);
        genereAnimale=(TextView) view.findViewById(R.id.genereAnimaleViewPoke);
        specieAnimale= (TextView) view.findViewById(R.id.specieAnimaleViewPoke);
        sessoAnimale=view.findViewById(R.id.sessoAnimaleViewPoke);
       // dataNascitaAnimale= (TextView) view.findViewById(R.id.dateNascitaAnimaleView);

        nomeAnimale.setText(mParam1.getNome());
        genereAnimale.setText(mParam1.getGenere());
        specieAnimale.setText(mParam1.getSpecie());
        sessoAnimale.setText(mParam1.getSesso());
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(mParam1.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageAnimalePokeView);
            }
        });
        return view;
    }
}