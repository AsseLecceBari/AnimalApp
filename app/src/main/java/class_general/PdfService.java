package class_general;

import static com.google.common.io.Resources.getResource;

import android.animation.FloatArrayEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms2223_2.R;
import model.Animale;

public  class PdfService {
    com.itextpdf.text.Font TITLE_FONT= new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16f, com.itextpdf.text.Font.BOLD);
    com.itextpdf.text.Font BODY_FONT= new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16f, com.itextpdf.text.Font.BOLD);
    private PdfWriter pdf;

    private File createFile(Animale animale) throws IOException {
        //Prepare file
        String title = "Dati anagrafici di "+animale.getNome()+".pdf";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(String.valueOf(path), title);
        if (!file.exists()){ file.createNewFile();}
        return file;
    }

    private com.itextpdf.text.Document createDocument() {
        //Create Document
        com.itextpdf.text.Document document =new com.itextpdf.text.Document();
        document.setMargins(24f, 24f, 32f, 32f);
        document.setPageSize(PageSize.A4);
        return document;
    }

    private void setupPdfWriter(com.itextpdf.text.Document document, File file) throws FileNotFoundException, DocumentException {
        pdf = PdfWriter.getInstance((com.itextpdf.text.Document) document, new FileOutputStream(file));
        pdf.setFullCompression();
        //Open the document
        (document).open();
    }

    public void createUserTable(Animale data, Bitmap bitmap, Context context) throws IOException, DocumentException {
        //Define the document
        File file = createFile(data);
        com.itextpdf.text.Document document = createDocument();
        FirebaseStorage storage;
        StorageReference storageRef;
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        //Setup PDF Writer
        setupPdfWriter( document, file);
        document.newPage();

      storageRef.child(data.getFotoProfilo()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
          @Override
          public void onComplete(@NonNull Task<Uri> task) {
/*
              URL url= null;
              try {
                  url = new URL(task.getResult().toString());
              } catch (MalformedURLException e) {
                  e.printStackTrace();
              }

              Bitmap image = null;
              try {
                  image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
              } catch (IOException e) {
                  e.printStackTrace();
              }

               */

             // ByteArrayOutputStream stream = new ByteArrayOutputStream();
              ByteArrayOutputStream streamQr = new ByteArrayOutputStream();
              //image.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , streamQr);
             // Image myImg = null;
              Image myImgQr = null;
              try {
               //   myImg = Image.getInstance(stream.toByteArray());
                  myImgQr = Image.getInstance(streamQr.toByteArray());
              } catch (BadElementException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              }
             // myImg.scaleToFit(300,300);
           //   myImg.setAlignment(Image.MIDDLE);
            //  myImg.setRotationDegrees(-90);


               //   document.add(myImg);

              try {
                  document.add(new Paragraph(  "Nome "+":"+data.getNome()));
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              try {
                  document.add(new Paragraph(  "Genere "+":"+data.getGenere()));
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              try {
                  document.add(new Paragraph(  "Specie "+":"+data.getSpecie()));
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              try {
                  document.add(new Paragraph(  "Nascita "+":"+data.getDataDiNascita()));
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              try {
                  document.add(new Paragraph(  "Sesso "+":"+data.getSesso()));
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              myImgQr.scaleToFit(300,300);
              myImgQr.setAlignment(Image.MIDDLE);
              try {
                  document.add(myImgQr);
              } catch (DocumentException e) {
                  e.printStackTrace();
              }
              document.addTitle(data.getNome());
              document.close();
              pdf.close();
              Toast.makeText(context, "scaricato", Toast.LENGTH_SHORT).show();
          }

      });





    }

}