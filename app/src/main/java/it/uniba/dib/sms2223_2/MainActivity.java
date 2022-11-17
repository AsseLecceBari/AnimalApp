package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import adapter.VPAdapter;
import fragments.main_fragment;
import fragments.myanimals_fragment;
import profiloUtente.ProfiloUtenteActivity;

public class MainActivity extends AppCompatActivity {

    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    int posizione;
    TabLayout tabLayout;
    private main_fragment main_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Imposto l'actionBar di questa activity
        main_action_bar=findViewById(R.id.main_action_bar);
        setSupportActionBar(main_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_bar_main, menu);
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
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onBackPressed() {
        try {
            main_fragment= (fragments.main_fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        }catch (Exception e){
            super.onBackPressed();
        }

        if(main_fragment!=null){
            tabLayout= findViewById(R.id.tabLayout);
            posizione=main_fragment.getPosition();
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
        //Qunado clicchiamo back se posizione = 0 usciamo dall'applicazione,se no torniamo in i miei animali
    }
}