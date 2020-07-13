package app.salvatop.brainstorm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.salvatop.brainstorm.model.Idea;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t = findViewById(R.id.textView2);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        t.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName());

        //test search feature
        getUser("username1");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //feature to search users by username
    public void getUser(String username){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(username);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //feature to follow a new user adding your username in his list of followed by
    public void followAUser(String toFollow, String whoIsFollow, String action){
        /// call -> updateFollowingUsersList(String username)
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(toFollow).child("followed");
        //TODO implement actions to add/remove whoIsFollow
        ////....
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //feature to update you list of following user after you add a new one
    public void updateFollowingUsersList(String whoIsFollow, String toFollow, String action){
        /// call -> followAUser(String toFollow, String whoIsFollow)
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(whoIsFollow).child("following");
        //TODO implement actions to add/remove toFollow
        ////....
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    ///add a new idea to your profile
    public Idea newIdea(String author, String context, String content, String title, Boolean visibility, ArrayList<String>forks) {
        Idea idea = new Idea(author,context,content,title,true,forks);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(author).child("ideas");
        //TODO add the idea into the database
        ////....
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return idea;
    }

}