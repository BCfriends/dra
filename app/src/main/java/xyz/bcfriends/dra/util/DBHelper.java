package xyz.bcfriends.dra.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.*;

import java.util.Map;

public interface DBHelper {
    String DEFAULT = "";

    interface QueryCallback {
        default void onCallback(@Nullable QuerySnapshot data) {
            throw new UnsupportedOperationException();

        }
        default void onCallback(@Nullable DocumentSnapshot data) {
            throw new UnsupportedOperationException();
        }
    }

    void readDataAll(@NonNull CollectionReference colRef, @NonNull QueryCallback callback);
    void readData(@NonNull DocumentReference docRef, @NonNull QueryCallback callback);
    void readData(@NonNull String key, @NonNull QueryCallback callback);
    void writeData(@NonNull DocumentReference docRef, @NonNull Map<String, Object> data);
    void writeData(@NonNull String key, @NonNull Map<String, Object> data);
}
