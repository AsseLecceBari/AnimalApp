package it.uniba.dib.sms2223_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import model.Associazione;
import model.Ente;
import model.Persona;
import model.Veterinario;

public class RegisterActivity extends AppCompatActivity {
    private StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private TextInputLayout numEFNInputLayout;
    private TextInputLayout piVAInputLayout;
    private TextInputLayout denomInputLayout;
    private TextInputLayout codFiscAssInputLayout;
    private TextInputLayout dataLayout;
    private TextInputLayout nomeLayout;
    private TextInputLayout cognomeLayout;

    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etRegConfPass;
    private TextInputEditText etRegTelefono;
    private TextInputEditText etRegNumEFNOVI;
    private TextInputEditText etRegPartitaIva;
    private TextInputEditText etRegDenominazione;
    private TextInputEditText etRegCodiceFiscaleAssociazione;
    private TextInputEditText data;
    private TextInputEditText nome;
    private TextInputEditText cognome;
    private Spinner etRegRuolo;
    private MaterialCheckBox etRegIsPrivato;
    private Toolbar main_action_bar;
    private TextView tvLoginHere, etichettaLocalita;
    private Button btnRegister;

    private Calendar cldr;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String ruolo;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private double latitudine, longitudine;
    private String address;
    private ColorStateList oldColors;
   private  Map<String,String> indirizzo = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("PLACE", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("PLACEeRROR", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(getResources().getString(R.string.registrazione));
        main_action_bar.setNavigationIcon(R.drawable.back);
        main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);

        StrictMode.setThreadPolicy(policy);

        //Autocomplete Indirizzo

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDlX6obgKqLyk_7MU5HD6hKzZeWQo0xEaA", Locale.ITALY);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                this.getSupportFragmentManager().findFragmentById(R.id.autoComplete);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS,Place.Field.LAT_LNG));
            autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        }



        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                AddressComponents ac = place.getAddressComponents();
                List<AddressComponent> ac1=ac.asList();

                String stato=" ";
                String città= " ";
                String provincia= " ";
                String via= " ";
                String civico= " ";
                String CodicePostale= " ";

                for(int a =0; a< ac1.size(); a++)
               {
               Log.d( "ciao20", String.valueOf(ac1.get(a).getTypes()));
               Log.d("ciao20",ac1.get(a).getShortName());

               if(ac1.get(a).getTypes().get(0).equals("street_number"))
               {
                   civico=ac1.get(a).getName();

               }
                else if(ac1.get(a).getTypes().get(0).equals("route"))
                   {
                       via=ac1.get(a).getName();

                   }

                else if(ac1.get(a).getTypes().get(0).equals("administrative_area_level_3" )||ac1.get(a).getTypes().get(0).equals("locality"))
                   {
                       città=ac1.get(a).getName();
                   }

                else if(ac1.get(a).getTypes().get(0).equals("administrative_area_level_2"))
               {
                   provincia = ac1.get(a).getShortName();

               }
               else if(ac1.get(a).getTypes().get(0).equals("country"))
               {

                   stato=ac1.get(a).getName();
               }
               else if(ac1.get(a).getTypes().get(0).equals("postal_code"))
               {

                   CodicePostale=ac1.get(a).getName();
               }

               }
                indirizzo.put("via",via);
                indirizzo.put("civico", civico);
                indirizzo.put("codicePostale", CodicePostale);
                indirizzo.put("città",città);
                indirizzo.put("provincia",provincia);
                indirizzo.put("stato",stato);

                address=place.getName();
                LatLng latlng=place.getLatLng();
                indirizzo.put("latitudine", String.valueOf(latlng.latitude));
                indirizzo.put("longitudine", String.valueOf(latlng.longitude));


                address = via+ ", " +civico +" "+CodicePostale+" " + città +", " + provincia +", "+ stato ;


                // todo remove focus from other fields -----------------------------
                etRegEmail.clearFocus();
                etRegPassword.clearFocus();
                etRegConfPass.clearFocus();
                etRegTelefono.clearFocus();
                etRegNumEFNOVI.clearFocus();
                etRegPartitaIva.clearFocus();
                etRegDenominazione.clearFocus();
                etRegCodiceFiscaleAssociazione.clearFocus();
                data.clearFocus();
                nome.clearFocus();
                cognome.clearFocus();
                etichettaLocalita.setTextColor(Color.GREEN);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("placeerror", "An error occurred: " + status);
            }
        });


        cldr = Calendar.getInstance();

        // Informazioni generali
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        etRegConfPass = findViewById(R.id.etRegConfPass);
        etRegTelefono = findViewById(R.id.etRegTelefono);
        etRegRuolo = findViewById(R.id.etRegRuolo);
        nome = findViewById(R.id.nome);
        cognome = findViewById(R.id.cognome);
        data = findViewById(R.id.etRegDataNascitaUtente);

        // Informazioni specifiche
        numEFNInputLayout = findViewById(R.id.numEFNInputLayout);
        piVAInputLayout = findViewById(R.id.piVAInputLayout);
        denomInputLayout = findViewById(R.id.denomInputLayout);
        codFiscAssInputLayout = findViewById(R.id.codFiscAssInputLayout);
        nomeLayout = findViewById(R.id.nomeLayout);
        cognomeLayout = findViewById(R.id.cognomeLayout);
        dataLayout = findViewById(R.id.dataLayout);

        etichettaLocalita = findViewById(R.id.etichettaLocalita);
        oldColors =  etichettaLocalita.getTextColors();

        // Al click nel campo Nascita si apre il date picker
        data.setInputType(InputType.TYPE_NULL);
        data.setFocusable(false);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE");


        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"));
                        calendar.setTimeInMillis((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate  = format.format(calendar.getTime());
                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        data.setText(formattedDate);
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });


        // Rendo visibili alcuni campi in base al ruolo
        etRegRuolo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        ruolo = "proprietario";
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.GONE);
                        denomInputLayout.setVisibility(View.GONE);
                        etRegIsPrivato.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        mostraNomeCognomeNascita();
                        etichettaLocalita.setText("Località di residenza ");
                        break;

                    case 1:
                        ruolo = "veterinario";
                        numEFNInputLayout.setVisibility(View.VISIBLE);
                        piVAInputLayout.setVisibility(View.VISIBLE);
                        denomInputLayout.setVisibility(View.GONE);
                        etRegIsPrivato.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        mostraNomeCognomeNascita();
                        etichettaLocalita.setText("Sede studio medico ");
                        break;

                    case 2:
                        ruolo = "associazione";
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.GONE);
                        denomInputLayout.setVisibility(View.VISIBLE);
                        etRegIsPrivato.setVisibility(View.GONE);
                        codFiscAssInputLayout.setVisibility(View.VISIBLE);
                        nascondiNomeCognomeNascita();
                        etichettaLocalita.setText("Sede associazione ");
                        break;

                    case 3:
                        ruolo = "ente";
                        numEFNInputLayout.setVisibility(View.GONE);
                        piVAInputLayout.setVisibility(View.VISIBLE);
                        denomInputLayout.setVisibility(View.VISIBLE);
                        etRegIsPrivato.setVisibility(View.VISIBLE );
                        codFiscAssInputLayout.setVisibility(View.GONE);
                        nascondiNomeCognomeNascita();
                        etichettaLocalita.setText("Sede dell'ente ");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(RegisterActivity.this, R.string.selectUserType, Toast.LENGTH_SHORT).show();
            }
        });

        etRegNumEFNOVI = findViewById(R.id.etRegNumEFNOVI);
        etRegPartitaIva = findViewById(R.id.etRegPartitaIva);
        etRegDenominazione=findViewById(R.id.etRegDenominazione);
        etRegIsPrivato = findViewById(R.id.isPrivato);
        etRegCodiceFiscaleAssociazione = findViewById(R.id.etRegCodiceFiscaleAssociazione);
        tvLoginHere = findViewById(R.id.vaiSmarrimento);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvLoginHere.setOnClickListener(view ->{
            onBackPressed();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }



    private void mostraNomeCognomeNascita() {
        nomeLayout.setVisibility(View.VISIBLE);
        cognomeLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.VISIBLE);
    }

    private void nascondiNomeCognomeNascita() {
        nomeLayout.setVisibility(View.GONE);
        cognomeLayout.setVisibility(View.GONE);
        dataLayout.setVisibility(View.GONE);
    }

    private void createUser(){
        String email;
        String password;
        String confPassword;
        String telefono;
        String efnovi;
        String partitaIva;
        String codiceFiscaleAssociazione, denominazione;
        String name, surname, dataNascita;

        email = etRegEmail.getText().toString();
        password = etRegPassword.getText().toString();
        confPassword= etRegConfPass.getText().toString();
        telefono= etRegTelefono.getText().toString();
        name = nome.getText().toString();
        surname = cognome.getText().toString();
        dataNascita = data.getText().toString();
        efnovi = etRegNumEFNOVI.getText().toString();
        partitaIva = etRegPartitaIva.getText().toString();
        codiceFiscaleAssociazione = etRegCodiceFiscaleAssociazione.getText().toString();
        denominazione = etRegDenominazione.getText().toString();


        // Controllo se gli input sono corretti
        int flag = 0;
            // Generali
        if (TextUtils.isEmpty(email)){
            etRegEmail.setError(getString(R.string.emailRequired));
            flag = 1;
        }
        if (TextUtils.isEmpty(password)) {
            etRegPassword.setError(getString(R.string.passwordRequired));
            etRegConfPass.setError(getString(R.string.passwordRequired));
            flag = 1;
        }
        if(!confPassword.equals(password)){
            etRegConfPass.setError(getString(R.string.passwordRepeatRequired));
            flag = 1;
        }
        if(address == null){
            etichettaLocalita.setTextColor(Color.RED);
            flag =1;
        }else{
            etichettaLocalita.setTextColor(oldColors);
        }
        if(TextUtils.isEmpty(telefono)){
            etRegTelefono.setError(getString(R.string.teleponeRequired));
            flag = 1;
        }else if(telefono.length()<10){
            etRegTelefono.setError(getString(R.string.minimum11cifre));
            flag = 1;
        }

        switch (ruolo){
            // Controlli
            case "proprietario":
                if(flag == 1){
                    nomeCognomeNascitaRequired(name, surname, dataNascita);
                }else{
                    flag = nomeCognomeNascitaRequired(name, surname, dataNascita);
                }


                // se un controllo non è andato esco dal metodo
                if(flag == 1){
                    Toast.makeText(RegisterActivity.this, getString(R.string.completaIcampi), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Creazione account
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){





                            Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationDone), Toast.LENGTH_SHORT).show();
                            Persona p =new Persona(email,telefono, ruolo, indirizzo, nome.getText().toString(), cognome.getText().toString(), data.getText().toString());
                            db.collection("utenti").document(email+"").set(p);
                            onBackPressed();
                        }else{
                            Toast.makeText(RegisterActivity.this, getString(R.string.registrationError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "veterinario":
                // controlli
                if(flag == 1){
                    nomeCognomeNascitaRequired(name, surname, dataNascita);
                }else{
                    flag = nomeCognomeNascitaRequired(name, surname, dataNascita);
                }
                if(TextUtils.isEmpty(efnovi)){
                    etRegNumEFNOVI.setError(getString(R.string.efnoviRequired));
                    flag = 1;
                }

                if(TextUtils.isEmpty(partitaIva)){
                    etRegPartitaIva.setError(getString(R.string.partitaIvaRequired));
                    flag = 1;
                }else if(partitaIva.length()<11){
                    etRegPartitaIva.setError("Partita Iva deve essere lunga almeno 11 numeri");
                    flag = 1;
                }

                // se un controllo non è andato esco dal metodo
                if(flag == 1){
                    Toast.makeText(RegisterActivity.this, getString(R.string.completaIcampi), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationDone), Toast.LENGTH_SHORT).show();
                            Veterinario v =new Veterinario(email, telefono, ruolo, indirizzo, nome.getText().toString(), cognome.getText().toString(), data.getText().toString(), efnovi, partitaIva);
                            db.collection("utenti").document(email+"").set(v);
                            onBackPressed();
                        }else{
                            Toast.makeText(RegisterActivity.this, getString(R.string.registrationError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "associazione":
                // controlli
                if(TextUtils.isEmpty(denominazione)){
                    etRegDenominazione.setError(getString(R.string.denominazioneRequired));
                    flag = 1;
                }

                if(TextUtils.isEmpty(codiceFiscaleAssociazione)){
                    etRegCodiceFiscaleAssociazione.setError(getString(R.string.cfRequired));
                    flag = 1;
                }else if(codiceFiscaleAssociazione.length()<11){
                    etRegCodiceFiscaleAssociazione.setError(getString(R.string.mincf));
                    flag = 1;
                }

                // se un controllo non è andato esco dal metodo
                if(flag == 1){
                    Toast.makeText(RegisterActivity.this, getString(R.string.completaIcampi), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationDone), Toast.LENGTH_SHORT).show();
                            Associazione a =new Associazione(email, telefono,  ruolo,indirizzo, codiceFiscaleAssociazione, denominazione);
                            db.collection("utenti").document(email+"").set(a);
                            onBackPressed();
                        }else{
                            Toast.makeText(RegisterActivity.this, getString(R.string.registrationError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case "ente":
                // controlli
                if(TextUtils.isEmpty(denominazione)){
                    etRegDenominazione.setError(getString(R.string.denominazioneRequired));
                    flag = 1;
                }

                if(TextUtils.isEmpty(partitaIva)){
                    etRegPartitaIva.setError(getString(R.string.partitaIvaRequired));
                    flag = 1;
                }else if(partitaIva.length()<11){
                    etRegPartitaIva.setError("Partita Iva deve essere lunga almeno 11 numeri");
                    flag = 1;
                }

                // se un controllo non è andato esco dal metodo
                if(flag == 1){
                    Toast.makeText(RegisterActivity.this, getString(R.string.completaIcampi), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, getString(R.string.RegistrationDone), Toast.LENGTH_SHORT).show();
                            Ente e =new Ente(email, telefono,  ruolo,indirizzo, partitaIva, denominazione, etRegIsPrivato.isChecked());
                            db.collection("utenti").document(email+"").set(e);
                           onBackPressed();
                        }else{
                            Toast.makeText(RegisterActivity.this, getString(R.string.registrationError) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    private boolean indirizzoNonConforme(String indirizzo) {
        if(indirizzo.length()<4){
            return true;
        }
        if(indirizzo.contains(" ") || indirizzo.contains(",")){
            return false;
        }
        return true;
    }

    // Restituisce 1 se cè un errore
    private int nomeCognomeNascitaRequired(String name, String surname, String dataNascita) {
        int flag = 0;
        if(TextUtils.isEmpty(name)){
            nome.setError(getString(R.string.nameRequired));
            flag = 1;
        }

        if(TextUtils.isEmpty(surname)){
            cognome.setError(getString(R.string.surnameRequired));
            flag = 1;
        }

        if(TextUtils.isEmpty(dataNascita)){
            data.setError(getString(R.string.dateBornRequired));
            flag = 1;
        }
        return flag;
    }
}