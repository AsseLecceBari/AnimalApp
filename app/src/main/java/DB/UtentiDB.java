package DB;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class UtentiDB {
    public Task<QuerySnapshot> getUtenti(FirebaseAuth auth, FirebaseFirestore db) {

        CollectionReference utentiReference=db.collection("utenti");
        Query query = utentiReference.whereEqualTo("email", auth.getCurrentUser().getEmail());
        return  query.get();
    }
}
