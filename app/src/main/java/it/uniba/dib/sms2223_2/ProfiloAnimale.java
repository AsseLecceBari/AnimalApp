package it.uniba.dib.sms2223_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import model.Animale;

public class ProfiloAnimale extends AppCompatActivity {

    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_animale);

        t = findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //receive
        Animale a = (Animale) getIntent().getSerializableExtra("animale");
        t.setText(a.getNome());
    }
}