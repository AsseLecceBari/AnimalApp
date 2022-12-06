package dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.transform.Result;

import adapter.AnimalAdapter;
import model.Animale;

public class AnimaleDAO{
    public Task<Void>registraAnimale(Animale a, FirebaseFirestore db){
        return db.collection("animali").document(a.getIdAnimale()).set(a);
    }

    public Task<QuerySnapshot> getMieiAnimali(FirebaseAuth auth,FirebaseFirestore db) {
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        CollectionReference animaliReference = db.collection("animali");
        Query query = animaliReference.whereEqualTo("emailProprietario", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        return  query.get();
    }
}
