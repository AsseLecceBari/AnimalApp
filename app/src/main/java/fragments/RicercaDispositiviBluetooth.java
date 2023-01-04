package fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.dib.sms2223_2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RicercaDispositiviBluetooth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RicercaDispositiviBluetooth extends DialogFragment {
    private Toolbar main_action_bar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RicercaDispositiviBluetooth() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RicercaDispositiviBluetooth.
     */
    // TODO: Rename and change types and number of parameters
    public static RicercaDispositiviBluetooth newInstance(String param1, String param2) {
        RicercaDispositiviBluetooth fragment = new RicercaDispositiviBluetooth();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        main_action_bar= getActivity().findViewById(R.id.main_action_bar);
        if(main_action_bar!= null)
        {
            if(main_action_bar.getMenu()!=null) {
                main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            }
            main_action_bar.setTitle("Seleziona Dispositivo ");
            main_action_bar.setNavigationIcon(R.drawable.back);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        return inflater.inflate(R.layout.fragment_ricerca_dispositivi_bluetooth, container, false);
    }
}