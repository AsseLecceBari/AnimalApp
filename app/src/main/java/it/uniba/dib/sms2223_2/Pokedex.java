package it.uniba.dib.sms2223_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import adapter.AnimalAdapter;
import adapter.PokedexAdapter;
import fragments.RecyclerItemClickListener;
import fragments.pokedexListFragment;
import model.Animale;

public class Pokedex extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Animale> mDataset= new ArrayList<>();
    private PokedexAdapter mAdapter=new PokedexAdapter(mDataset);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        //Prendo il riferimento al RecycleView in myAnimals_fragment.xml

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclePokedex);
        //Dico alla recycle View di usare un linear layout,mettendo quindi le varie card degli animali,una sotto l'altra
        Animale a = new Animale("Charmander","Fuoco","Drago","io@gmail.com","29/10/89","ciao","83938",false,"maschio");
        Animale a2 = new Animale("Charmaleon","Fuoco","Drago","io@gmail.com","29/10/89","ciao","83938",false,"maschio");
        Animale a3 = new Animale("Charizard","Fuoco","Drago","io@gmail.com","29/10/89","ciao","83938",false,"maschio");
        mDataset.add(a);
        mDataset.add(a2);
        mDataset.add(a3);
        mAdapter= new PokedexAdapter(mDataset);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                     getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.layoutpokedex,pokedexListFragment.newInstance(a)).commit();
                    }


                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: menu rapido
                    }
                })
        );



    }
}