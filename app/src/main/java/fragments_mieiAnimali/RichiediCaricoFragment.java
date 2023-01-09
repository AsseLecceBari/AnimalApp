package fragments_mieiAnimali;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.RichiestaCarico;
import model.Utente;
import model.Veterinario;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RichiediCaricoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RichiediCaricoFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private ListView lvRichiestaCarichi;
    private Button btnInviaRichiesta;

    // TODO: Rename and change types of parameters
    private ArrayList<Animale> mParam1;
    private ArrayList<Veterinario> mDataset=new ArrayList<>();

    public RichiediCaricoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RichiediCaricoFragment newInstance(ArrayList<Animale> param1) {
        RichiediCaricoFragment fragment = new RichiediCaricoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ArrayList<Animale>) getArguments().getSerializable(ARG_PARAM1);
            for(Animale animale:mParam1){
                Log.e("ciao123",animale.getNome());
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_richiedi_carico, container, false);
        lvRichiestaCarichi = (ListView) view.findViewById(R.id.lvRichiestaCarichi);
        btnInviaRichiesta=view.findViewById(R.id.btnInviaRichiesta);
        ArrayAdapter<Animale> arrayAdapter = new ArrayAdapter<Animale>(getContext(),android.R.layout.simple_expandable_list_item_1,mParam1);
        lvRichiestaCarichi.setAdapter(arrayAdapter);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        CollectionReference utentiReference=db.collection("utenti");
        Query query = utentiReference.whereEqualTo("ruolo", "veterinario");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mDataset.add(document.toObject(Veterinario.class));
                        }
                }

            }
        });
        final RichiestaCarico[] richiestaCarico = new RichiestaCarico[1];
        btnInviaRichiesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Animale animale: mParam1){
                    //TODO METTERE VETERINARIO SCELTO DA LISTA
                   richiestaCarico[0] =new RichiestaCarico(animale.getIdAnimale(),"vet@gmail.com",auth.getCurrentUser().getEmail(),"in sospeso");
                    db.collection("richiestaCarico").document(animale.getIdAnimale()).set(richiestaCarico[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}