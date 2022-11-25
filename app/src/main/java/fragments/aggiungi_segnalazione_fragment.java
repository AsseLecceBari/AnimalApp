package fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.util.ArrayList;

import adapter.AggiungiAnimaleAdapter;
import adapter.AnimalAdapter;
import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class aggiungi_segnalazione_fragment extends Fragment {

    ImageButton smarrimento,animaleFerito,pericolo,news,raccoltaFondi,ritrovamento;











    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =inflater.inflate(R.layout.fragment_aggiungi_segnalazione_fragment, container, false);




        smarrimento=rootView.findViewById(R.id.smarrimento);

        animaleFerito=rootView.findViewById(R.id.animaleFerito);
        pericolo=rootView.findViewById(R.id.pericolo);
        news=rootView.findViewById(R.id.news);
        raccoltaFondi=rootView.findViewById(R.id.raccoltaFondi);
        ritrovamento=rootView.findViewById(R.id.ritrovamento);













        smarrimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new myanimals_fragment().newInstance("smarrimento")).addToBackStack(null).commit();



            }
        });

        animaleFerito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pericolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        //da capire bene come fare
        raccoltaFondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                }
        });


        ritrovamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });






        // Inflate the layout for this fragment
        return rootView;
    }




}

