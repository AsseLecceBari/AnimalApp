package profiloUtente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms2223_2.LoginActivity;
import it.uniba.dib.sms2223_2.MainActivity;
import it.uniba.dib.sms2223_2.R;

public class ProfiloUtenteActivity extends AppCompatActivity {
    Toolbar main_action_bar;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente);
        auth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle("Profilo");
        setSupportActionBar(main_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_profile, menu);
        return true;

    }

    public void logout(MenuItem item) {
        auth.signOut();
        Toast.makeText(getApplicationContext(), R.string.logoutSuccess, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


}