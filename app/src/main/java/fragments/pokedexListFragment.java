package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false);
    }
}