package app.salvatop.brainstorm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.salvatop.brainstorm.model.Idea;
import app.salvatop.brainstorm.model.Profile;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "FIREBASE";
    FirebaseAuth firebaseAuth;

    private EditText rEmail, rPassword, rUsername;
    private Button register, done;
    private TextView emailLbl, passLbl, messageLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        rEmail = findViewById(R.id.editTextTextRegisterEmailAddress);
        rPassword = findViewById(R.id.editTextTextRegisterPassword);
        rUsername = findViewById(R.id.editTextTextUsername);

        register = findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = rEmail.getText().toString().trim();
                String pass = rPassword.getText().toString().trim();
                createAccount(firebaseAuth, email, pass);
            }
        });

         done = findViewById(R.id.buttonRegisterDone);
         done.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

             }
         });

        emailLbl = findViewById(R.id.emailLabelRegister);
        passLbl = findViewById(R.id.passwordLabelRegister);
        messageLbl = findViewById(R.id.messageLabelRegister);
    }

    public String setupProfile(FirebaseAuth mAuth, String displayName, String photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Log.d(TAG, Objects.requireNonNull(user.getDisplayName()));
                        }
                    }
                });

        return Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
    }

    private void createAccount(FirebaseAuth auth, String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        //register user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "New user registration: " + task.isSuccessful());
                        hideAndDisplayUIElements();
                        if (!task.isSuccessful()) {
                            System.out.println("Authentication failed. " + task.getException());
                        }
                    }
                });
    }


    public void prepareProfile() {
        setupProfile(firebaseAuth, rUsername.getText().toString().trim(), "");

        ArrayList<String> followed = new ArrayList<>();
        followed.add("");
        ArrayList<String> following = new ArrayList<>();
        following.add("");
        ArrayList<String> teams = new ArrayList<>();
        teams.add("");
        ArrayList<String> bookmarks = new ArrayList<>();
        bookmarks.add("");
        ArrayList<Idea> ideas = new ArrayList<>();
        ideas.add(new Idea());

        Profile profile = new Profile(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName(),
                followed, following, teams, ideas, bookmarks);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String id = String.valueOf(getNbOfRecords() + 1);
        DatabaseReference myRef = database.getReference("users");
        myRef.child(id).setValue(profile);

        RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        RegisterActivity.this.finish();
    }


    public void hideAndDisplayUIElements() {
        rEmail.setAlpha(0);
        rEmail.setEnabled(false);
        rEmail.setFocusable(false);
        emailLbl.setAlpha(0);
        passLbl.setAlpha(0);
        rPassword.setAlpha(0);
        rPassword.setEnabled(false);
        rPassword.setFocusable(false);
        done.setAlpha(1);
        rUsername.setAlpha(1);
        messageLbl.setAlpha(1);
        register.setAlpha(0);
        register.setEnabled(false);
    }


    public int getNbOfRecords() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        return Integer.parseInt(Objects.requireNonNull(myRef.getKey()));
    }
}
