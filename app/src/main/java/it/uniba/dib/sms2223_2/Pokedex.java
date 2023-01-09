package it.uniba.dib.sms2223_2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.PokedexAdapter;
import fragments.RecyclerItemClickListener;
import fragments.pokedexListFragment;
import model.Animale;
import model.Carico;

public class Pokedex extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset= new ArrayList<>();
    private PokedexAdapter mAdapter=new PokedexAdapter(mDataset);
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Toolbar main_action_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Pokedex");
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePokedex);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference pokedexReference = db.collection("pokedex");

        CollectionReference animaliReference = db.collection("animali");
        pokedexReference.whereEqualTo("emailProprietarioPokedex",auth.getCurrentUser().getEmail()+"").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Query queryAnimaliInCarico = animaliReference.whereEqualTo("idAnimale",document.toObject(model.Pokedex.class).getIdAnimale());
                        Log.e("pokedex",document.toObject(model.Pokedex.class).getIdAnimale());
                        queryAnimaliInCarico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mDataset.add(document.toObject(Animale.class));
                                    Log.e("pokedex",document.toObject(Animale.class).getIdAnimale());
                                }
                                mAdapter= new PokedexAdapter(mDataset);
                                mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                                mRecyclerView.setAdapter(mAdapter);
                            }

                        });
                    }

            }
        });


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                     getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.layoutpokedex,pokedexListFragment.newInstance(mDataset.get(position))).commit();
                    }


                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );



    }
}