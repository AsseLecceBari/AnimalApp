package it.uniba.dib.sms2223_2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import class_general.PdfService;
import fragments.main_fragment_animale;
import model.Animale;
import model.SegnalazioneSanitaria;
import profiloUtente.ProfiloUtenteActivity;

public class ProfiloAnimale extends AppCompatActivity {
    private SegnalazioneSanitaria s;
    private Animale animale;
    private Toolbar main_action_bar;
    private FirebaseAuth auth;
    private TabLayout tabLayout;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;
    public main_fragment_animale getMain_fragment_animale() {
        return main_fragment_animale;
    }

    private fragments.main_fragment_animale main_fragment_animale;
    private int posizione;


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    PdfService pdfService= new PdfService();
                    try {
                        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

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
                        pdfService.createUserTable(animale,bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Dire all'utente di andare nelle impostazioni e dare i permessi dello storage all'app

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animale= (Animale) getIntent().getSerializableExtra("animale");
        setContentView(R.layout.activity_profilo_animale);
        main_action_bar=findViewById(R.id.main_action_bar);
        main_action_bar.setTitle(animale.getNome());
        if(main_action_bar.getMenu()!=null) {
            main_action_bar.getMenu().removeGroup(R.id.groupItemMain);
            main_action_bar.inflateMenu(R.menu.menu_bar_profilo_animale);
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
        main_fragment_animale= (main_fragment_animale) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        // Imposto l'actionBar di questa activity

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_profilo_animale, menu);
        return true;
    }

    public void mostraProfilo(MenuItem item) {
        auth= FirebaseAuth.getInstance();

        // Parte l'activity mostraProfilo solo se si è loggati
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), ProfiloUtenteActivity.class));
        }
        else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            main_fragment_animale= (main_fragment_animale) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        }catch (Exception e){
            super.onBackPressed();
        }
        if(main_fragment_animale!=null){
            tabLayout= findViewById(R.id.tabLayout);
            //posizione=main_fragment_animale.getPosition();
            switch (posizione) {
                case (0):
                    super.onBackPressed();
                    break;
                default:
                    tabLayout.getTabAt(0).select();
                    posizione=0;
                    break;
            }
        }
    }

    public SegnalazioneSanitaria getS() {
        return s;
    }

    public void setS(SegnalazioneSanitaria s) {
        this.s = s;
    }

    public void scaricaAnimale(MenuItem item) throws DocumentException, IOException {

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            PdfService pdfService = new PdfService();
            try {
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

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
                pdfService.createUserTable(animale,bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        Toast.makeText(getApplicationContext(), "scaricato", Toast.LENGTH_SHORT).show();
}



    public void condividiAnimale(MenuItem item) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = animale.toString();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Condividi via..."));
    }

}