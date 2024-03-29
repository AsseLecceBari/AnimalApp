package it.uniba.dib.sms2223_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import class_general.PdfService;
import fragments.main_fragment;
import fragments.main_fragment_animale;
import fragments_adozioni.mainFragmentVistaAdozioni;
import model.Animale;
import profiloUtente.ProfiloUtenteActivity;

public class AdozioneActivity extends AppCompatActivity {
    private fragments_adozioni.mainFragmentVistaAdozioni main_fragment_animale;
    private TabLayout tabLayout;
    private int posizione;
    Animale animale;
    private Toolbar main_action_bar;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale = (Animale) getIntent().getSerializableExtra("animale");
        setContentView(R.layout.activity_adozione);
        main_action_bar = findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(animale.getNome());
        if (main_action_bar.getMenu() != null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            main_action_bar.inflateMenu(R.menu.menu_bar_img_profilo);
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
        main_fragment_animale = (mainFragmentVistaAdozioni) getSupportFragmentManager().findFragmentById(R.id.mainFragAdozioni);
        // Imposto l'actionBar di questa activity

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_profilo_animale, menu);
        return true;
    }

    public void mostraProfilo(MenuItem item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Parte l'activity mostraProfilo solo se si è loggati
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ProfiloUtenteActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            main_fragment_animale = (mainFragmentVistaAdozioni) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        } catch (Exception e) {
            super.onBackPressed();
        }
        if (main_fragment_animale != null) {
            tabLayout = findViewById(R.id.tabLayout);
            //posizione=main_fragment_animale.getPosition();
            switch (posizione) {
                case (0):
                    super.onBackPressed();
                    break;
                default:
                    tabLayout.getTabAt(0).select();
                    posizione = 0;
                    break;
            }
        }
    }
}
