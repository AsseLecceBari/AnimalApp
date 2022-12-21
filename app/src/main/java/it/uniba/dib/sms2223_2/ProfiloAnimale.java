package it.uniba.dib.sms2223_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import model.Animale;
import model.SegnalazioneSanitaria;
import profiloUtente.ProfiloUtenteActivity;

public class ProfiloAnimale extends AppCompatActivity {
    private SegnalazioneSanitaria s;
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
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(animale.getNome());
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            main_action_bar.inflateMenu(R.menu.menu_bar_profilo_animale);
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
    protected void onResume() {
        super.onResume();
        // Imposto l'actionBar di questa activity

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

    public SegnalazioneSanitaria getS() {
        return s;
    }

    public void setS(SegnalazioneSanitaria s) {
        this.s = s;
    }

    public void scaricaAnimale(MenuItem item) {
        Toast.makeText(getApplicationContext(), "scaricato", Toast.LENGTH_SHORT).show();
    }

    public void condividiAnimale(MenuItem item) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = animale.getIdAnimale();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Condividi via..."));
    }
}