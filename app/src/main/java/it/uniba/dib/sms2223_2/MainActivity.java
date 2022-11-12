package it.uniba.dib.sms2223_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TableLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import adapter.VPAdapter;
import fragments.main_fragment;
import profiloUtente.ProfiloUtenteActivity;

public class MainActivity extends AppCompatActivity {

    private Toolbar main_action_bar;
    private FirebaseAuth auth;

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
}