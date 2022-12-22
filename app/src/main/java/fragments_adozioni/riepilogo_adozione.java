package fragments_adozioni;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import class_general.fab;
import it.uniba.dib.sms2223_2.R;


public class riepilogo_adozione extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public riepilogo_adozione() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View root =inflater.inflate(R.layout.fragment_riepilogo_adozione, container, false);


        fab fab= new fab();

        fab.iniziallizazioneFab(root);

        fab.inizializzazioneFab2(root,1,getContext(),"ciao");

        fab.FabContainerListner();
         return root;
    }
}