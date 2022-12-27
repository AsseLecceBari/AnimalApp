package DB;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CaricoDB {
    public Task<QuerySnapshot> getVetCarichi(FirebaseAuth auth, FirebaseFirestore db) {

        CollectionReference carichiReference = db.collection("carichi");
        Query queryCarichi = carichiReference.whereEqualTo("idProfessionista", auth.getCurrentUser().getEmail()).whereEqualTo("inCorso",true);
        return  queryCarichi.get();
    }

    public Task<QuerySnapshot> getVetRichiesteCarichi(FirebaseAuth auth, FirebaseFirestore db) {

        CollectionReference carichiReference = db.collection("richiestaCarico");
        Query queryCarichi = carichiReference.whereEqualTo("idVeterinario", auth.getCurrentUser().getEmail()).whereEqualTo("stato","in sospeso");
        return  queryCarichi.get();
    }
}
