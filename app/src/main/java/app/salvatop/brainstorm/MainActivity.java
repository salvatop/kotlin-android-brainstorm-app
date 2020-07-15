package app.salvatop.brainstorm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import app.salvatop.brainstorm.model.Idea;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireAuthListener;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///initialization
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycleView);
        ///end of initialization

        ////Checking user session
        fireAuthListener = firebaseAuth1 -> {
            FirebaseUser user1 = firebaseAuth1.getCurrentUser();
            if (user1 == null) {
                //user not login
                MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
            }
        };

        /// set the avatar image if exists in the user profile or set the default from drawable
        ImageView avatar = findViewById(R.id.imageViewAvatar);
        try {
            if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl() != null) {
                Glide.with(getApplicationContext())
                        .load(firebaseAuth.getCurrentUser().getPhotoUrl())
                        .into(avatar);
            } else {
                avatar.setImageDrawable(getDrawable(R.drawable.avatar));
            }
        } catch (Exception e) {
           Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }

        ///set user profile info in the UI
        TextView email = findViewById(R.id.textViewEmail);
        email.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        TextView username = findViewById(R.id.textViewDisplayName);
        username.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName());

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /////TODO testing code
        //test search feature
        //getUser("username1");
        /////TODO end of testing code
    }

    // Handle toolbar item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmarks:
                System.out.println("bookmarks");
                return true;
            case R.id.menu_settings:
                Intent registration = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(registration);
                return true;
            case R.id.menu_logoff:
                signOut(firebaseAuth);
                return true;
            case R.id.app_bar_search:
                // Get the intent, verify the action and get the query
                Intent intent = getIntent();
                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    System.out.println(query);
                    getUser(query);
                }
                System.out.println("query");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Menu icons are inflated just as they were with actionbar
    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(fireAuthListener != null){
            firebaseAuth.removeAuthStateListener(fireAuthListener);
        }
    }

    //feature to get a users by username from the database
    public void getUser(String username){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(username);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        ////....get the actual records
        ////....add the new one
        ////....set the child value .setValue()
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return idea;
    }

    private void signOut(FirebaseAuth auth) {
        auth.signOut();
    }

    public void removeUser(FirebaseAuth auth){
        FirebaseUser user =  auth.getCurrentUser();
        assert user != null;
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                            MainActivity.this.finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}