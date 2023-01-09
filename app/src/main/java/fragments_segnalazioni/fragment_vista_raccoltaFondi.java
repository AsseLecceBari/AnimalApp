package fragments_segnalazioni;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.dib.sms2223_2.R;
import model.Segnalazione;

public class fragment_vista_raccoltaFondi extends Fragment {
    private TextView titolo, descrizione, data;
   // private FloatingActionButton vai;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CircleImageView imgAnimale;
    private FirebaseFirestore db;
    private Segnalazione segnalazione;
    private FloatingActionButton vai;


    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";
    private FloatingActionButton fab;
    private boolean expanded = false;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private float offset1;
    private float offset2;



    public fragment_vista_raccoltaFondi(Segnalazione segnalazione) {
        this.segnalazione = segnalazione;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vista_raccolta_fondi, container, false);

        titolo = rootView.findViewById(R.id.titolo);
        descrizione = rootView.findViewById(R.id.descrizione);
        data = rootView.findViewById(R.id.data);
       // vai = rootView.findViewById(R.id.btnVaiAlLink);
        imgAnimale = rootView.findViewById(R.id.immagine);
        vai = rootView.findViewById(R.id.btnVaiAlLink);


        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         *
        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab =  rootView.findViewById(R.id.fab);
        fabAction1 = rootView.findViewById(R.id.fab_preferiti);
        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Aggiungi ai preferiti", Toast.LENGTH_SHORT).show();
            }
        });

        fabAction2 = rootView.findViewById(R.id.fab_modifica);
        fabAction2.setImageResource(R.drawable.search_96701);
        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://" + segnalazione.getLink()));
                startActivity(i);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                if (expanded) {
                    expandFab();
                } else {
                    collapseFab();
                }
            }
        });

        /*
        Restituisce ViewTreeObserver per la gerarchia di questa vista.
         L'osservatore dell'albero di visualizzazione può essere utilizzato per ricevere notifiche
         quando si verificano eventi globali, come il cambio del layout.

        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - fabAction1.getY();
                fabAction1.setTranslationY(offset1);
                offset2 = fab.getY() - fabAction2.getY();
                fabAction2.setTranslationY(offset2);

                return true;
            }
        });
        */


        if (segnalazione != null){
            titolo.setText(segnalazione.getTitolo());
            descrizione.setText(segnalazione.getDescrizione());
            data.setText(segnalazione.getData());
        }

        // setto l'immagine dell'animale
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        if(segnalazione.getUrlFoto() != null){
            storageRef.child(segnalazione.getUrlFoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(imgAnimale.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAnimale);
                }
            });
        }else{
            imgAnimale.setVisibility(View.GONE);
        }


        //non va il fab, quindi metto il pulsante
        vai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://" + segnalazione.getLink()));
                startActivity(i);
            }
        });

        return rootView;
    }



    //metodi per le animazioni del FAB SPEED DIAL
    /*
    AnimatoreSet serve per impostare le animazioni sugli oggetti

    Abbiamo due possibilità:
        playTogheter() ( si trova nel collapse) che li fa muovere contemporaneamente
        playSequentially (si trova nell'expand) che li fa muovere sequenzialmente
     */

    //TODO: DA SCEGLIERE SE PLAYTOGHETER O PLAY SEQUENTIALLY
    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2));
        animatorSet.start();
        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2));
        animatorSet.start();
        //animateFab();
    }


    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    /*
    SERVONO PER CREARE I MOVIMENTI DELLE ANIMAZIONI CHE VENGONO POI CHIAMATI DAI VARI AnimatorSet
     */
    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    //SERVE PER IMPOSTARE L'ANIMAZIONE AL FAB GENERALE
   /*private void animateFab() {
        Drawable drawable = fab.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }*/
}