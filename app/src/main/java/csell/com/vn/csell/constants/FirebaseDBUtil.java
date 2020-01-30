package csell.com.vn.csell.constants;

import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseDBUtil {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatebase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

//    private static FirebaseFirestore db;
//
//    public static FirebaseFirestore getFirestore() {
//        if (db == null) {
//            db = FirebaseFirestore.getInstance();
//            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                    .setPersistenceEnabled(true)
//                    .build();
//            db.setFirestoreSettings(settings);
//        }
//        return db;
//    }
}
