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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationBarMenu;
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
    View barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        main_action_bar = findViewById(R.id.main_action_bar);
        setSupportActionBar(main_action_bar);

        main_fragment main_fragment= (fragments.main_fragment) getSupportFragmentManager().findFragmentById(R.id.mainfragment);


            // Imposto l'actionBar di questa activity

            tabLayout = findViewById(R.id.tabLayout);
            //Salviamo la posizione del tab selezionato
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    posizione = tab.getPosition();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {


                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

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

        main_fragment main_fragment = null;
        try {
            main_fragment = (fragments.main_fragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        } catch (Exception e) {
            super.onBackPressed();
            return;

        }

        if (main_fragment != null) {


            //Qunado clicchiamo back se posizione = 0 usciamo dall'applicazione,se no torniamo in i miei animali
            switch (posizione) {
                case (0):
                    super.onBackPressed();
                    break;
                default:
                    // super.onBackPressed();

                    tabLayout.getTabAt(0).select();
                   // posizione = 0;
                    break;
            }
        }
    }
}