package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;


import adapter.VPAdapterAdozione;
import model.Animale;
import profiloUtente.ProfiloUtenteActivity;

public class VistaAdozioneActivity extends AppCompatActivity {

    Animale animale;
    private Toolbar main_action_bar;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private VPAdapterAdozione vpAdapteradozione;
    private int posizione=0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_adozione);




      //assegno l'animale passato tramite l'intent
      animale= (Animale) getIntent().getSerializableExtra("animale");
        main_action_bar=findViewById(R.id.main_action_bar);
        setSupportActionBar(main_action_bar);




    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        main_action_bar.setTitle(animale.getNome());


        tabLayout= findViewById(R.id.tabLayout2);
        viewPager2=findViewById(R.id.viewPager2);

        //Se l'adapter è stato già creato,viene catturato l'errore e non ne viene creato uno nuovo

           vpAdapteradozione= new VPAdapterAdozione(this);
            viewPager2.setAdapter(vpAdapteradozione);

        tabLayout.getTabAt(posizione).select();
        viewPager2.setCurrentItem(posizione);
        Log.d("ciao", String.valueOf(posizione));




         //serve per cambiare view pager quando selezioni il tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                posizione = tab.getPosition();
                Log.d("ciao","press");

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
       // serve cambiare il tab in base al pagerview selezionata
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Salvo in che tab mi trovo quando esco
        outState.putInt("posizione", posizione);
    }

    public int getPosition() {
        return posizione;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_img_profilo,menu);

        return true;
    }
    public void mostraProfilo(MenuItem item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Parte l'activity mostraProfilo solo se si è loggati
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), ProfiloUtenteActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }
}