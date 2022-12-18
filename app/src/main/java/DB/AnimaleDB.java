package DB;

import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import model.Animale;

public class AnimaleDB {
    public Task<Void>registraAnimale(Animale a, FirebaseFirestore db){
        return db.collection("animali").document(a.getIdAnimale()).set(a);
    }
    public Task<QuerySnapshot> getMieiAnimali(FirebaseAuth auth,FirebaseFirestore db) {

        CollectionReference animaliReference = db.collection("animali");
        Query query = animaliReference.whereEqualTo("emailProprietario", auth.getCurrentUser().getEmail());
        return  query.get();
    }
    public StorageTask<UploadTask.TaskSnapshot> uploadImageAnimale(FirebaseStorage storage, StorageReference storageRef, Uri file) {
        storage= FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        StorageTask < UploadTask.TaskSnapshot > storageTask = null;
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        try {
            storageTask = storageRef.child("images/" + file.getLastPathSegment()).putFile(file, metadata);
        }catch (Exception e){
        }
        return  storageTask;
    }
}
