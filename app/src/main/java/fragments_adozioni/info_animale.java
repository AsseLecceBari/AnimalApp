package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import it.uniba.dib.sms2223_2.R;
import model.Adozione;
import model.Animale;
import model.Preferenze;

public class info_animale extends Fragment {
    private Animale animale;
    private Adozione adozione;
    private ImageView immagineAnimale ;
    private TextView descrizioneAnimale;
    private TextView dettagliAnimale;
    private View btnaggiungiPreferiti;
    private View btineliminaPreferiti;
    private SharedPreferences share;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseStore;
    private Preferenze preferenze;

    private static String Email  ;
    private Set<String> set = new HashSet<>() ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");
        adozione=(Adozione) getActivity().getIntent().getSerializableExtra("adozione");

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View root=inflater.inflate(R.layout.fragment_info_animale, container, false);

        immagineAnimale=root.findViewById(R.id.imageAnimal);
        descrizioneAnimale=root.findViewById(R.id.DescrizioneAnimale);
        dettagliAnimale=root.findViewById(R.id.dettagliAnimale);
        btnaggiungiPreferiti=root.findViewById(R.id.BtnaggiungiPreferiti);
        btineliminaPreferiti=root.findViewById(R.id.btneliminaPreferiti);

        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
       caricaInfoAnimale();
       initDataAnnunci();

       String d=differenzaDataPubblicazione(adozione.getDataPubblicazione());
       Log.d("ciao17",d);


       eliminadaPreferiti();
       aggiungiPreferiti();




    }



    public void caricaInfoAnimale()
    {
        // setto l'immagine dell'animale
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(immagineAnimale.getContext())
                        .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(immagineAnimale);
            }
        });

        dettagliAnimale.setText("Dettagli"+"\n\n\n"+"Nome"+"       "+"Data Di Nascita"+"       "+"Specie"+"\n"+animale.getNome()+"            "+animale.getDataDiNascita()+"            "+animale.getSpecie() );
        descrizioneAnimale.setText("Descrizione");
    }


    public void aggiungiPreferiti()
    {
        btnaggiungiPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth=FirebaseAuth.getInstance();
                Log.d("ciao10","ciao");

                if(preferenze!= null) {


                        int ultimaposiz= preferenze.getAdozioni().size();

                        if( preferenze.getAdozioni().get(ultimaposiz-1)== null) {

                            preferenze.getAdozioni().remove(ultimaposiz - 1);

                        }


                            preferenze.getAdozioni().add(adozione.getIdAdozione());

                        btineliminaPreferiti.setVisibility(View.VISIBLE);
                    btnaggiungiPreferiti.setVisibility(View.GONE);



                    writeData(preferenze);
                }
                else
                {
                    Log.d("ciao10","ciao1");
                    ArrayList <String> adozionipreferite= new ArrayList<>();
                    adozionipreferite.add(adozione.getIdAdozione());
                    ArrayList <String> segnalazioni= new ArrayList<>();
                  segnalazioni.add(null);

                    preferenze= new Preferenze(auth.getCurrentUser().getEmail(),adozionipreferite,segnalazioni);
                    btnaggiungiPreferiti.setVisibility(View.GONE);

                    btineliminaPreferiti.setVisibility(View.VISIBLE);
                    writeData(preferenze);
                }










            }
        });
    }



    public void initDataAnnunci() {
        //Prendere gli oggetti(documenti)animali da fireBase e aggiungerli al dataset
        firebaseStore = FirebaseFirestore.getInstance();

        auth= FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference preferenzeRef = firebaseStore.collection("preferenze");
        //CollectionReference animali = firebaseStore.collection("animali");

        Query query= preferenzeRef.whereEqualTo("emailUtente",auth.getCurrentUser().getEmail());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        preferenze=document.toObject(Preferenze.class);

                    }

                    if(preferenze!= null) // controllo se l'annuncio sta gia nella lista preferiti
                    {
                        int cont=0;
                        for(int a=0; a<preferenze.getAdozioni().size(); a++) {


                            if(Objects.equals(preferenze.getAdozioni().get(a), adozione.getIdAdozione()))

                            {
                                cont++;

                            }

                        }

                        if (cont==0)
                        {
                            btnaggiungiPreferiti.setVisibility(View.VISIBLE);
                        }
                        else {
                            btineliminaPreferiti.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        btnaggiungiPreferiti.setVisibility(View.VISIBLE);

                    }

                }
            }
        });

    }


    public void writeData(Preferenze preferenze) {

        firebaseStore = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        CollectionReference preferenzeRef = firebaseStore.collection("preferenze");
        //CollectionReference animali = firebaseStore.collection("animali");


        firebaseStore.collection("preferenze").document(auth.getCurrentUser().getEmail()).set(preferenze).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    Toast.makeText(getActivity(), R.string.AggiuntoListaPreferiti, Toast.LENGTH_LONG).show();
                }
            }
        });






    }



    public void eliminadaPreferiti()

    {
        btineliminaPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int a=0; a<preferenze.getAdozioni().size(); a++)
                {

                    if(Objects.equals(preferenze.getAdozioni().get(a), adozione.getIdAdozione()))
                    {
                        preferenze.getAdozioni().remove(a);
                        if(preferenze.getAdozioni().size()==0)
                        {
                            preferenze.getAdozioni().add(null);
                        }
                        // Log.d("ciao10", String.valueOf(preferenze.getAdozioni().size()));
                    }
                }
                btineliminaPreferiti.setVisibility(View.GONE);
                btnaggiungiPreferiti.setVisibility(View.VISIBLE);
                writeData(preferenze);

            }
        });

    }

    public String differenzaDataPubblicazione(String datadiPublicazione)
    {
        String datafinale= "";
        SimpleDateFormat dataFor= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");


        String data= dataFor.format(new Date());

        int giornopub;
        int mesepub;
        int annopub;
        int orapub;
        int minutipub;


        int giornoOd ;
        int meseOd;
        int annoOd;
        int oraOd;
        int minutiOd;


        int annotot;
        int meseTot;
        int giornotot;
        int orariotot;
        int minutiTot;

        String[] result = datadiPublicazione.split(" ");
       String[] datapubblicazione = result[0].split("-");
        String[] orariopubblicazione = result[1].split(":");



        result= data.split(" ");
        String[] dataOdierna = result[0].split("-");
        String[] orarioOdierno = result[1].split(":");



        annopub= Integer.parseInt(datapubblicazione[0]);
        mesepub=Integer.parseInt(datapubblicazione[1]);
        giornopub=Integer.parseInt(datapubblicazione[2]);

        orapub=Integer.parseInt(orariopubblicazione[0]);
        minutipub= Integer.parseInt(orariopubblicazione[1]);


        annoOd=Integer.parseInt(dataOdierna[0]);
        meseOd=Integer.parseInt(dataOdierna[1]);
        giornoOd=Integer.parseInt(dataOdierna[2]);


        oraOd=Integer.parseInt(orarioOdierno[0]);
        minutiOd=Integer.parseInt(orarioOdierno[1]);

   

        if(annopub!= annoOd)
        {

            annotot=annoOd-annopub;
            if(annotot>1) {

                datafinale = datafinale + " " + annotot + " " + "anni fa ";
            }


            if(mesepub!= meseOd)
            {
                meseTot= meseOd-mesepub;


                if(meseTot<0)
                {
                    meseTot= 12+(meseTot);
                    if(annotot<2)// se negativo e minore di 2 inserisci solo i mesi vuol dire che non è passato un anno
                    {
                        datafinale= datafinale  + meseTot+ " mesi fa ";
                    }
                    else{
                        datafinale= datafinale +" e " + meseTot+ " mesi fa ";
                    }

                }
                else{
                    if(annotot<2)//se è maggiore ed è un anno allora inserisci un anno e mesi
                    {
                        datafinale= datafinale  + annotot +"anno e " + meseTot+ " mesi fa ";
                    }
                    else{
                        datafinale= datafinale +" e " + meseTot+ " mesi fa ";
                    }
                }
            }
            else{// se è uguale il mese allora è passato solo un anno
                if(annotot<2) {
                    datafinale = datafinale + " " + annotot + " " + "anno fa ";
                }

            }
        }
        else if(mesepub!= meseOd)
        {
            meseTot= meseOd-mesepub;
            if(meseTot>1) {
                datafinale = datafinale + " " + meseTot + " " + "mesi";
            }
            if(giornopub!= giornoOd)
            {
                giornotot= giornoOd-giornopub;
                if(giornotot<0)
                {
                    giornotot= 30+(giornotot);

                    if(meseTot<2)
                    {datafinale= datafinale  + giornotot + " giorni fa ";}
                    else{datafinale= datafinale +" e " + giornotot + " giorni ";}
                }
                else{
                    if(meseTot<2)
                    {datafinale= datafinale  + meseTot+ " mese e "+ giornotot + " giorni fa ";}
                    else{datafinale= datafinale +" e " + giornotot + " giorni ";}
                }

            }else if(meseTot<2) //se i giorni sono uguali allora è passato un mese
            {datafinale= datafinale +meseTot + " mese fa ";

            }
        }

        else if(giornopub!= giornoOd)

        {
            giornotot=giornoOd-giornopub;
            if (giornotot>1) {

                datafinale = datafinale + " " + giornotot + " " + " giorni ";
            }
        }
        else if(orapub!= oraOd)
        {


            orariotot=oraOd-orapub;

            datafinale= datafinale +" " + orariotot +" " +" ore ";

            if(minutipub!= minutiOd)
            {
                minutiTot= minutiOd-minutipub;

                if(minutiTot<0)
                {
                   minutiTot= 60+(minutiTot);
                }

                datafinale= datafinale +" e " + minutiTot + " minuti ";
            }

        }

        else if(minutipub!= minutiOd)
        {
            minutiTot= minutiOd-minutipub;
            datafinale= datafinale +" " + minutiTot +" " +" minuti ";

        }

        else{
            datafinale="Pubblicato meno di un minuto fa";
        }

        return datafinale;


    }









}