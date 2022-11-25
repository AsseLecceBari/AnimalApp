package it.uniba.dib.sms2223_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import adapter.VPAdapterAnimale;
import fragments.anagrafica;
import model.Animale;
import profiloUtente.ProfiloUtenteActivity;

public class ProfiloAnimale extends AppCompatActivity {
    private Animale animale;
    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    private TabLayout tabLayout;
    private fragments.main_fragment main_fragment_animale;
    private int posizione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getIntent().getSerializableExtra("animale");
        setContentView(R.layout.activity_profilo_animale);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Imposto l'actionBar di questa activity
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(animale.getNome());
        setSupportActionBar(main_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_profilo_animale, menu);
        return true;
    }

    public void mostraProfilo(MenuItem item) {
        auth= FirebaseAuth.getInstance();

        // Parte l'activity mostraProfilo solo se si è loggati
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), ProfiloUtenteActivity.class));
        }
        else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            main_fragment_animale= (fragments.main_fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        }catch (Exception e){
            super.onBackPressed();
        }
        if(main_fragment_animale!=null){
            tabLayout= findViewById(R.id.tabLayout);
            //posizione=main_fragment_animale.getPosition();
            switch (posizione) {
                case (0):
                    super.onBackPressed();
                    break;
                default:
                    tabLayout.getTabAt(0).select();
                    posizione=0;
                    break;
            }
        }
    }

    public void modificaAnimale(View view) {
        Toast.makeText(this, "qui si apre il fragment modificare", Toast.LENGTH_SHORT).show();
        // Apertura del fragment per modificare l'animale
    }
}