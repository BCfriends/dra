package xyz.bcfriends.dra;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import xyz.bcfriends.dra.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FirestoreHelper implements DBHelper {
    private static final String TAG = "FirestoreHelper";

    private final FirebaseAuth mAuth;
    private final FirebaseUser mUser;
    private final FirebaseFirestore mDatabase;
    private final Source source;

    public FirestoreHelper() {
        this(Source.DEFAULT);
    }

    public FirestoreHelper(Source source) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.source = source;
    }

    @Override
    public void readDataAll(@NonNull CollectionReference colRef, @NonNull QueryCallback callback) {
        colRef.get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onCallback(task.getResult());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void readData(@NonNull DocumentReference docRef, @NonNull QueryCallback callback) {
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    callback.onCallback(task.getResult());
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void readData(@NonNull String key, @NonNull QueryCallback callback) {
        if (key.equals(DBHelper.DEFAULT)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            key = format.format(new Date());
        }

        DocumentReference docRef = mDatabase.collection("users")
                .document(mUser.getUid())
                .collection("Records")
                .document(key);

        readData(docRef, callback);
    }

    @Override
    public void writeData(@NonNull DocumentReference docRef, @NonNull Map<String, Object> data) {
        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    @Override
    public void writeData(@NonNull String key, @NonNull Map<String, Object> data) {
        if (key.equals(DBHelper.DEFAULT)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            key = format.format(new Date());
        }

        DocumentReference docRef = mDatabase.collection("users")
                .document(mUser.getUid())
                .collection("Records")
                .document(key);

        writeData(docRef, data);
    }

    boolean IsUserExist() {
        return mAuth.getCurrentUser() != null;
    }
}
