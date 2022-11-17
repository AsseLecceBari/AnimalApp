package it.uniba.dib.sms2223_2;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.Sleeper;

import java.net.InetAddress;
import java.net.URI;

public class ServiceUploadImage extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri file;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);


        }


    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        file= Uri.parse(intent.getStringExtra("file"));

        if(isInternetAvailable()){
            Toast.makeText(this, "Caricamento in corso", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Ci vorrà più tempo del previsto: Problemi di connesione!", Toast.LENGTH_SHORT).show();

            // TODO non prosegue se starto la mainActivity
            //startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

        }


        while(!isInternetAvailable()){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        uploadImage();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(isInternetAvailable()){
            Toast.makeText(this, "Animale aggiunto!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Impossibile aggiungere! Errore di connessione!", Toast.LENGTH_SHORT).show();
        }

        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    public boolean uploadImage() {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        try{
           storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}