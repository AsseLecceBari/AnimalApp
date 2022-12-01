package fragments_adozioni;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public class info_animale extends Fragment {
    private Animale animale;
    private ImageView immagineAnimale ;
    private TextView descrizioneAnimale;
    private TextView dettagliAnimale;
    private View btnaggiungiPreferiti;
    private SharedPreferences share;
    private FirebaseAuth auth;

   private static final String mypreference = "animalipreferiti";

    private static String Email  ;
    private Set<String> set = new HashSet<>() ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getActivity().getIntent().getSerializableExtra("animale");

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

        return  root;
    }

    @Override
    public void onResume() {
        super.onResume();
       caricaInfoAnimale();
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

                auth= FirebaseAuth.getInstance();
                share=getActivity().getSharedPreferences(mypreference,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor edit= share.edit();
                String preferenza  = animale.getIdAnimale();
                if(share.contains("preferiti")) {

                    set.add(String.valueOf(share.getStringSet("preferiti", null)));

                }
                else {
                    set.add(preferenza);
                }

                  edit.putStringSet("preferiti",set);
                  edit.apply();

                  if(set!= null)
                  {
                      String c= String.valueOf(set);
                      String a = String.valueOf(set);
                      String v= new String();


                      // usando un semplice ciclo for
                      for (int i = 0; i < c.length(); i++) {
                          for (int o = i; o < a.length(); o++) {

                              if (c.charAt(i) != ']') {

                               a= String.valueOf((c.charAt(i)));

                              }
                          }
                        // Log.d("ciao7",a);


                          // System.out.print(s.charAt(i));
                      }
                      Log.d("ciao7",a);

                  }



                Toast.makeText(getActivity(), R.string.AggiuntoListaPreferiti, Toast.LENGTH_LONG).show();


            }
        });
    }




}