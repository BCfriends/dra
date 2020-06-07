package xyz.bcfriends.dra;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";

    private final FirebaseAuth mAuth;
    private final FirebaseUser mUser;
    private final FirebaseFirestore mDatabase;

    public FirestoreHelper() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    public interface DBQueryCallback {
        void onCallback(boolean exists, @Nullable Map<String, Object> data);
    }

    void readData(@Nullable String date, @NonNull DBQueryCallback callback) {
        if (date == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            date = format.format(new Date());
        }

        DocumentReference docRef = mDatabase.collection("users")
                .document(mUser.getUid())
                .collection("Records")
                .document(date);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = null;
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        data = document.getData();
                    }

                    callback.onCallback(document != null && document.exists(), data);
                }
            }
        });
    }


    void writeData(@Nullable String date, @NonNull Map<String, Object> data) {
        if (date == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            date = format.format(new Date());
        }

        mDatabase.collection("users")
                .document(mUser.getUid())
                .collection("Records")
                .document(date)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    boolean IsUserExist() {
        return mAuth.getCurrentUser() != null;
    }

}
