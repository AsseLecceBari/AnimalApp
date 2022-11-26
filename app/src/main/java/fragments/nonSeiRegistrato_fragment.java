package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.R;
import it.uniba.dib.sms2223_2.RegisterActivity;

public class nonSeiRegistrato_fragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvLogin;
    private TextView tvRegistrati;

    public nonSeiRegistrato_fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView= inflater.inflate(R.layout.fragment_non_sei_registrato, container, false);
            tvLogin=rootView.findViewById(R.id.tvLoginMyAnimals);
            tvRegistrati=rootView.findViewById(R.id.tvRegisterHereMyAnimals);
            tvLogin.setOnClickListener(view ->{
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            });
            tvRegistrati.setOnClickListener(view ->{
                startActivity(new Intent(getActivity().getApplicationContext(), RegisterActivity.class));
            });

        return rootView;
    }
}