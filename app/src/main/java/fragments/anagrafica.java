package fragments;

import static android.content.Context.WINDOW_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import it.uniba.dib.sms2223_2.R;
import model.Animale;



public class anagrafica extends Fragment {
    private TextView nome, genere, specie,sesso, nascita, assistito;
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
    private Button generateQrBtn, cambiaImg;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;


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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);
        db=FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");

        imgAnimaleReg = rootView.findViewById(R.id.imgAnimaleReg);
        nome = rootView.findViewById(R.id.nome);
        genere = rootView.findViewById(R.id.genere);
        specie = rootView.findViewById(R.id.specie);
        sesso=rootView.findViewById(R.id.sessoAnimale);
        nascita = rootView.findViewById(R.id.nascita);
        assistito = rootView.findViewById(R.id.assistito);
        qrCodeIV = rootView.findViewById(R.id.idIVQrcode);
        cambiaImg = rootView.findViewById(R.id.selectImgButton);
        modificaAnimaliBtn = rootView.findViewById(R.id.modificaAnimaliBtn);

        if(animale!= null){
            nome.setText(animale.getNome());
            genere.setText(animale.getGenere());
            specie.setText(animale.getSpecie());
            nascita.setText(animale.getDataDiNascita());
            sesso.setText(animale.getSesso());

            if(animale.getIsAssistito()){
                assistito.setText(R.string.eassistito);
            }else{
                assistito.setText(R.string.noneassistito);
            }

            fab(rootView);

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
            //fabAction3.setVisibility(View.GONE);
        }else{
            cambiaImg.setVisibility(View.GONE);
            modificaAnimaliBtn.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            fabAction1.setVisibility(View.VISIBLE);
            fabAction2.setVisibility(View.VISIBLE);
            //fabAction3.setVisibility(View.VISIBLE);
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

        return rootView;
    }

    private void fab(View rootView) {
        /**
         * FAB INIZIALIZZAZIONI
         * ViewGroup serve per prendere il riferimento al layout dei FAB
         */
        final ViewGroup fabContainer =  rootView.findViewById(R.id.fab_container);
        fab =  rootView.findViewById(R.id.fab);
        fabAction1 = rootView.findViewById(R.id.fab_action_1);


        fabAction1.setImageResource(R.drawable.back);
        fabAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "fine carico", Toast.LENGTH_SHORT).show();

                showCustomDialog();

            }
        });



        fabAction2 = rootView.findViewById(R.id.fab_action_2);
        fabAction2.setImageResource(android.R.drawable.ic_menu_edit);
        fabAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "modifica microchip", Toast.LENGTH_SHORT).show();
            }
        });


        fabAction3 = rootView.findViewById(R.id.fab_action_3);
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
        final EditText ageEt = dialog.findViewById(R.id.costo);
        Button submitButton = dialog.findViewById(R.id.submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // se l'id del loggato è = a quella del professionista del carico in corso

                // inserisco la spesa se il costo del carico è maggiore di 0

                //aggiorno il carico mettendolo a completato
            }
        });

        dialog.show();
    }

}