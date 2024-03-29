package fragments_mieiAnimali;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import adapter.AnimalAdapter;
import it.uniba.dib.sms2223_2.MainActivity;
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
    private Toolbar main_action_bar;

    // TODO: Rename and change types of parameters
    private ArrayList<Animale> mParam1;
    private ArrayList<Veterinario> mDataset=new ArrayList<>();
    private RecyclerView mRecyclerView;


    private AnimalAdapter mAdapter;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_richiedi_carico, container, false);
       // lvRichiestaCarichi = (ListView) view.findViewById(R.id.lvRichiestaCarichi);
       btnInviaRichiesta=view.findViewById(R.id.btnInviaRichiesta);
        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleMyAnimals);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
     //   ArrayAdapter<Animale> arrayAdapter = new ArrayAdapter<Animale>(getContext(),android.R.layout.simple_list_item_1,mParam1);
        //Passo i dati presi dal database all'adapter
        mAdapter = new AnimalAdapter(mParam1);
        mRecyclerView.setAdapter(mAdapter);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(R.string.invia_richieste_di_carico);
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,false);
            main_action_bar.getMenu().clear();
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().onBackPressed();
                }
            });
        }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.setNavigationIcon(null);
            main_action_bar.setTitle("AnimalApp");
            main_action_bar.getMenu().setGroupVisible(R.id.groupItemMain,true);
            MainActivity mainActivity= (MainActivity) getActivity();
            mainActivity.searchFilterListener();
        }
    }
}