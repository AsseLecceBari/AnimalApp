package fragments;

import static android.content.Context.WINDOW_SERVICE;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import it.uniba.dib.sms2223_2.ProfiloAnimale;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anagrafica, container, false);
        db=FirebaseFirestore.getInstance();
        profiloAnimale= new ProfiloAnimale();
        animale = (Animale) getActivity().getIntent().getSerializableExtra("animale");
        pokeball=rootView.findViewById(R.id.pokeball);
        ProfiloAnimale profiloAnimale=(ProfiloAnimale)  anagrafica.super.getActivity();
        main_fragment_animale=profiloAnimale.getMain_fragment_animale();
        viewPager2=main_fragment_animale.getViewPager2();
        tabLayout= main_fragment_animale.getTabLayout();



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
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    Log.e("motion","move");
                    v.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    break;
                default:
                    viewPager2.setUserInputEnabled(true);
                    Log.e("motion","default");


                    return false;
            }
                return true;
            }
        });
                /*
       pokeball.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                   x= motionEvent.getX();
                   y=motionEvent.getY();
               }
               if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                   dx= motionEvent.getX()-x;
                   dy=motionEvent.getY()-y;
                   pokeball.setX(pokeball.getX()+dx);
                   pokeball.setY(pokeball.getY()+dy);
                   x=motionEvent.getX();
                   y= motionEvent.getY();
               }
               return true;
           }
       });

                 */
        imgAnimaleReg = rootView.findViewById(R.id.imgAnimaleReg);
        nome = rootView.findViewById(R.id.nome);
        genere = rootView.findViewById(R.id.genere);
        specie = rootView.findViewById(R.id.specie);
        sesso=rootView.findViewById(R.id.sessoAnimale);
        nascita = rootView.findViewById(R.id.nascita);
        assistito = rootView.findViewById(R.id.assistito);
        qrCodeIV = rootView.findViewById(R.id.idIVQrcode);
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


      /*  if(!proprietario)
        {   bottoneMar.setVisibility(View.INVISIBLE);
            inviaemail.setVisibility(View.VISIBLE);
        }
        else
        {
         //   bottoneMar.setVisibility(View.VISIBLE);
           // inviaemail.setVisibility(View.INVISIBLE);

        }*/


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
}