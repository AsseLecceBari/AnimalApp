package fragments;

import static android.content.Context.WINDOW_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import fragments_mieiAnimali.modificaAnimale;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
import it.uniba.dib.sms2223_2.R;
import model.Animale;
import model.Carico;
import model.Pokedex;
import model.SpesaAnimale;


public class anagrafica extends Fragment {
    private TextView nome, genere, specie,sesso, nascita, assistito, microchip_, box, dataRitrovamento;
    private Animale animale;

    private ImageView imgAnimaleReg;

    private Uri file;
    private ActivityResultLauncher<String> mGetContent;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Button selectImgButton;
    private ImageView qrCodeIV;

    private FloatingActionButton modifica;

    //FAB SPEED DIAL DECLARATION
    private static final String TRANSLATION_Y = "translationY";
    private FloatingActionButton fab, modificaAnimaliBtn;
    private boolean expanded = false;
    private FloatingActionButton fabAction1;
    private FloatingActionButton fabAction2;
    private FloatingActionButton fabAction3;
    private float offset1;
    private float offset2;
    private float offset3;
    private Toolbar main_action_bar;
    
    private ImageView pokeball;
    private Button generateQrBtn;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private ProfiloAnimale profiloAnimale;
    private float x,y;
    private float dx,dy;
    private float xCoOrdinate, yCoOrdinate;
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private main_fragment_animale main_fragment_animale;
    private Button cambiaImg;

    private Dialog dialog;
    private  int countPokedex = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        profiloAnimale= new ProfiloAnimale();
        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        pokeball=rootView.findViewById(R.id.pokeball);
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.pokeballsoundeffect);
        main_action_bar=getActivity().findViewById(R.id.main_action_bar);
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.setNavigationIcon(R.drawable.back);
            main_action_bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();

                }
            });
        }


        modifica = rootView.findViewById(R.id.modificaAnimaliBtn);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificaAnimale();
            }
        });
        ProfiloAnimale profiloAnimale=(ProfiloAnimale)  anagrafica.super.getActivity();
        main_fragment_animale=profiloAnimale.getMain_fragment_animale();
        viewPager2=main_fragment_animale.getViewPager2();
        tabLayout= main_fragment_animale.getTabLayout();
        imgAnimaleReg = rootView.findViewById(R.id.imgAnimaleReg);
        pokeball.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {  switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    viewPager2.setUserInputEnabled(false);
                    Log.e("motion","down");
                    xCoOrdinate = v.getX() - event.getRawX();
                    yCoOrdinate = v.getY() - event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:
                    //tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    Rect rc_img1 = new Rect();
                    pokeball.getHitRect(rc_img1);
                    Rect rc_img2 = new Rect();
                    imgAnimaleReg.getHitRect(rc_img2);
                    if (Rect.intersects(rc_img1, rc_img2)) {

                        RotateAnimation anim =new RotateAnimation(-1.0f, 1.0f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 1f);
                        anim.setRepeatCount(2);
                        anim.setDuration(1500);
                        pokeball.startAnimation(anim);
                        imgAnimaleReg.setVisibility(View.INVISIBLE);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                mediaPlayer.start();

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                CollectionReference pokedexReference = db.collection("pokedex");
                                Pokedex pokedex=new Pokedex(animale.getIdAnimale(),auth.getCurrentUser().getEmail());
                                pokedexReference.document().set(pokedex).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), R.string.animaleAggiuntoalPokedex,Toast.LENGTH_LONG).show();
                                    }
                                });

                                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                        .replace(R.id.fragmentContainerView,pokedexListFragment.newInstance(animale)).commit();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                mediaPlayer.start();
                            }
                        });


                }
                    break;
                default:
                    viewPager2.setUserInputEnabled(true);
                    Log.e("motion","default");


                    return false;
            }
                return true;
            }
        });
        nome = rootView.findViewById(R.id.nome);
        genere = rootView.findViewById(R.id.genere);
        specie = rootView.findViewById(R.id.specie);
        sesso=rootView.findViewById(R.id.sessoAnimale);
        nascita = rootView.findViewById(R.id.nascita);
        assistito = rootView.findViewById(R.id.assistito);
        qrCodeIV = rootView.findViewById(R.id.idIVQrcode);
        cambiaImg = rootView.findViewById(R.id.selectImgButton);
        modificaAnimaliBtn = rootView.findViewById(R.id.modificaAnimaliBtn);

        microchip_ = rootView.findViewById(R.id.microChip);
        box = rootView.findViewById(R.id.box);
        dataRitrovamento = rootView.findViewById(R.id.dataRitrovamento);

        fab(rootView);

        if(animale!= null){
            nome.setText(getString(R.string.nome)+": "+animale.getNome());
            genere.setText(getString(R.string.genere)+": "+animale.getGenere());
            specie.setText(getString(R.string.specie)+": "+animale.getSpecie());
            nascita.setText(getString(R.string.nascita)+": "+animale.getDataDiNascita());
            sesso.setText(getString(R.string.sesso)+": "+animale.getSesso());

            if(animale.getIsAssistito()){
                assistito.setText(R.string.eassistito);
            }else{
                assistito.setText(R.string.noneassistito);
            }

            try{
                if(!animale.getMicroChip().equals(""))
                    microchip_.setText(getString(R.string.microchip)+": "+animale.getMicroChip());
                if(!animale.getBox().equals(""))
                    box.setText(getString(R.string.box)+": "+animale.getBox());
                if(!animale.getDataRitrovamento().equals(""))
                    dataRitrovamento.setText(getString(R.string.data_ritrovamento)+": "+animale.getDataRitrovamento());
            }catch (Exception e){
                Toast.makeText(getActivity().getApplicationContext(), "Animale da cancellare", Toast.LENGTH_SHORT).show();
            }



            // box deve essere visibile solo ai professionisti
            vediBox();

            // setto l'immagine dell'animale
            FirebaseStorage storage;
            StorageReference storageRef;
            storage= FirebaseStorage.getInstance();
            storageRef=storage.getReference();

            storageRef.child(animale.getFotoProfilo()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(imgAnimaleReg.getContext())
                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAnimaleReg);
                }
            });
        }

        // tasto cambia immagine
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        file=uri;
                        if(file!=null){
                            animale.setFotoProfilo("images/"+file.getLastPathSegment());

                            // aggiornare il db
                            db.collection("animali").document(animale.getIdAnimale()).set(animale).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                            uploadImage();
                            imgAnimaleReg.setImageURI(file);
                        }
                    }
                });

        selectImgButton = rootView.findViewById(R.id.selectImgButton);
        selectImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });


        if(isProprietario()){
            cambiaImg.setVisibility(View.VISIBLE);
            modificaAnimaliBtn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            fabAction1.setVisibility(View.GONE);
            fabAction2.setVisibility(View.GONE);
        }else{
            cambiaImg.setVisibility(View.GONE);
            modificaAnimaliBtn.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            fabAction1.setVisibility(View.VISIBLE);
            fabAction2.setVisibility(View.VISIBLE);
            fabAction3.setVisibility(View.VISIBLE);
        }


        // setto il generatore
        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();
        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);
        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;
        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(animale.getIdAnimale(), null, QRGContents.Type.TEXT, dimen);
        // getting our qrcode in the form of bitmap.
        bitmap = qrgEncoder.getBitmap();
        // the bitmap is set inside our image
        // view using .setimagebitmap method.
        qrCodeIV.setImageBitmap(bitmap);
        if(qrgEncoder.getBitmap() != null)
            qrCodeIV.setVisibility(View.VISIBLE);

        // la modifica del microchip è permessa solo ai vet che hanno il carico
        visibilitaModificaMicrochip();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        CollectionReference pokedexReference = db.collection("pokedex");
        pokedexReference.whereEqualTo("idAnimale",animale.getIdAnimale()+"").whereEqualTo("emailProprietarioPokedex",auth.getCurrentUser().getEmail()+"").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult().size()>0){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        countPokedex++;
                    }


                }
                if( countPokedex==0 && !(animale.getEmailProprietario().equals(auth.getCurrentUser().getEmail())) ){
                    pokeball.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void vediBox() {
        //se non sei il proprietario dell'animale non vedi il box, altrimenti lo vedi se sei un professionista

        CollectionReference animaliReference=db.collection("utenti");

        if(auth.getCurrentUser()!=null){
            if (auth.getCurrentUser().getEmail().equals(animale.getEmailProprietario())) {

                Query query = animaliReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            String ruolo = null;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ruolo = Objects.requireNonNull(document.get("ruolo")).toString();
                                if (!ruolo.equals("proprietario")) {
                                    box.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                        }

                    }
                });
            }else{
                //sei in visualizzazione esterna
                box.setVisibility(View.GONE);
                return;
            }
        }
    }

    private void visibilitaModificaMicrochip() {
        if(isProprietario()){
        }else{
            // se non è proprietario, SE è colui che ce lha attualmente in carico  && è veterinario allora visualizza il pulsante per aggiungere la segnalazione sanitaria
            CollectionReference docRef = db.collection("carichi");
            Query query = docRef.whereEqualTo("inCorso", true).whereEqualTo("idProfessionista", Objects.requireNonNull(auth.getCurrentUser()).getEmail()).whereEqualTo("idAnimale", animale.getIdAnimale());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().size() == 0){
                            // nascondo
                            fabAction2.setVisibility(View.GONE);
                        }else{
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Carico  trovato
                                Carico c = document.toObject(Carico.class);

                                // se è veterianario vedo il pulsante
                                CollectionReference animaliReference=db.collection("utenti");
                                if(auth.getCurrentUser()!=null) {
                                    Query query = animaliReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                String ruolo = null;

                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                    ruolo = Objects.requireNonNull(document.get("ruolo")).toString();
                                                    if(ruolo.equals("veterinario")){
                                                        fabAction2.setVisibility(View.VISIBLE);

                                                    }else{
                                                        fabAction2.setVisibility(View.GONE);
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                    });
                                }

                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void fab(View rootView) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab =  rootView.findViewById(R.id.fab);
        fabAction1 = rootView.findViewById(R.id.fab_annulla_modifica);


        fabAction1.setImageResource(R.drawable.back);
        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomDialog();

            }
        });



        fabAction2 = rootView.findViewById(R.id.fab_fine_modifica);
        fabAction2.setImageResource(android.R.drawable.ic_menu_edit);
        fabAction2.setVisibility(View.VISIBLE);
        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialogMicrochip();
            }
        });


        fabAction3 = rootView.findViewById(R.id.fab_mail);
        fabAction3.setVisibility(View.GONE);/*
        fabAction3.setImageResource(android.R.drawable.sym_action_email);
        fabAction3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

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
         */
        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - fabAction1.getY();
                fabAction1.setTranslationY(offset1);
                offset2 = fab.getY() - fabAction2.getY();
                fabAction2.setTranslationY(offset2);
                offset3 = fab.getY() - fabAction3.getY();
                fabAction3.setTranslationY(offset3);

                return true;
            }
        });
    }

    private void collapseFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
                createCollapseAnimator(fabAction2, offset2),createCollapseAnimator(fabAction3, offset3));
        animatorSet.start();
        // animateFab();
    }

    private void expandFab() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
                createExpandAnimator(fabAction2, offset2),createExpandAnimator(fabAction3, offset3));
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

    private boolean isProprietario() {
        return animale.getEmailProprietario().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail());

    }

    public void setAnimale(Animale a){
        animale = a;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public void uploadImage() {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        try{
            StorageTask<UploadTask.TaskSnapshot> storageTask=storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
            if(isInternetAvailable()) {
                storageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    }
                });
            }
        }catch (Exception e){
        }
    }

    //Function to display the custom dialog.
    void showCustomDialog() {
        final Dialog dialog = new Dialog(getActivity());
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_dialog);

        //Initializing the views of the dialog.
        final EditText costo = dialog.findViewById(R.id.costo);
        Button submitButton = dialog.findViewById(R.id.submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // se l'id del loggato è = a quella del professionista del carico in corso
                String emailCarico = null;
                CollectionReference docRef = db.collection("carichi");
                Query query = docRef.whereEqualTo("inCorso", true).whereEqualTo("idProfessionista", Objects.requireNonNull(auth.getCurrentUser()).getEmail()).whereEqualTo("idAnimale", animale.getIdAnimale());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() == 0){
                                Toast.makeText(getActivity().getApplicationContext(), "Non ci sono risultati", Toast.LENGTH_SHORT).show();
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Carico daaggiornare trovato
                                    Carico c = document.toObject(Carico.class);
                                    if(c != null){
                                        //Toast.makeText(getActivity().getApplicationContext(), c.getId()+"", Toast.LENGTH_SHORT).show();

                                        aggiornaCarico(costo.getText().toString(), c);
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(), "Nessun risultato", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Operazione negata", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });

        dialog.show();
    }

    void showCustomDialogMicrochip() {
        dialog = new Dialog(getActivity());
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_dialog_microchip);

        //Initializing the views of the dialog.
        final EditText microchip = dialog.findViewById(R.id.microchip);
        Button submitButton = dialog.findViewById(R.id.submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    animale.setMicroChip(microchip.getText().toString());
                    db.collection("animali").document(animale.getIdAnimale()).set(animale).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity().getApplicationContext(), "Microchip aggiornato!", Toast.LENGTH_SHORT).show();
                            microchip_.setText("Microchip: "+animale.getMicroChip());
                            dialog.cancel();
                        }
                    });
                }catch (Exception e){
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getActivity().getApplicationContext(), "Animale da cancellare, funzione disabilitata a causa del model senza i campi", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }


    private void aggiornaCarico(String costo, Carico carico) {
        Float costoUnitario = Float.valueOf(0);
        if(!costo.isEmpty()){
            costoUnitario = Float.parseFloat(costo);
        }
        String data =  new SimpleDateFormat("dd-M-yyyy").format(new Date()).toString();

        // inserisco la spesa se il costo del carico è maggiore di 0
        if(costoUnitario>0){
            SpesaAnimale s = new SpesaAnimale("Carico", data, "Prestazione lavorativa offerta da "+auth.getCurrentUser().getEmail(), new Random().nextInt(999999999)+"", animale.getIdAnimale().toString(), costoUnitario, 1);
            db.collection("spese").document(s.getId()).set(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(getActivity().getApplicationContext(), "Spesa aggiunta!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //aggiorno il carico mettendolo a completato
        Carico c = new Carico(carico.getId(), carico.getDataInizio(), data, carico.getIdAnimale(), carico.getIdProfessionista(), carico.getNote(), false);
        db.collection("carichi").document(carico.getId()).set(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity().getApplicationContext(), "Carico Terminato!", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }


    public void modificaAnimale() {
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainerView,new modificaAnimale(animale)).commit();
    }
}