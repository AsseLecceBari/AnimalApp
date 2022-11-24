package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.dib.sms2223_2.R;


public class smarrimento_fragments extends Fragment {




    public smarrimento_fragments() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      View rootView=inflater.inflate(R.layout.fragment_smarrimento_fragments, container, false);

        return rootView;
    }
}