package app.salvatop.brainstorm;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest extends Application {

    private static FirebaseApp firebaseApp;
    private static FirebaseDatabase database;

    @BeforeClass
    public void setup(){
        firebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Test
    public void testFirebase(FirebaseDatabase database) {

        final DatabaseReference myRef = database.getReference("users");

        // Read from the database
        final ValueEventListener valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
                Object object = dataSnapshot.getValue();
                assertNotNull(object);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
}