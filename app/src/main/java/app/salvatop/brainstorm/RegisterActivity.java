package app.salvatop.brainstorm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
    private String username;

    private TextInputLayout rEmail, rPassword, rUsername;
    private Button register, done, goTologin;
    private TextView messageLbl;

    private boolean validateEmailPassword() {
        boolean valid = true;

        String email = rEmail.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            rEmail.setError("Required.");
            valid = false;
        } else {
            rEmail.setError(null);
        }

        String password = rPassword.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            rPassword.setError("Required.");
            valid = false;
        } else {
            rPassword.setError(null);
        }

        return valid;
    }

    private boolean validateUsername() {
        boolean valid = true;

        String username = rUsername.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            rUsername.setError("Required.");
            valid = false;
        } else {
            rUsername.setError(null);
        }
        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        rEmail = findViewById(R.id.editTextTextRegisterEmailAddress);
        rPassword = findViewById(R.id.editTextTextRegisterPassword);
        rUsername = findViewById(R.id.editTextTextUsername);
        messageLbl = findViewById(R.id.messageLabelRegister);

        register = findViewById(R.id.buttonRegister);
        register.setOnClickListener(view -> {
            String email = rEmail.getEditText().getText().toString().trim();
            String pass = rPassword.getEditText().getText().toString().trim();
            if (validateEmailPassword()) {
                createAccount(firebaseAuth, email, pass);
            }
        });
         done = findViewById(R.id.buttonRegisterDone);
         done.setOnClickListener(view -> {
             if (validateUsername()) {
                 setupProfile(firebaseAuth, rUsername.getEditText().getText().toString().trim(), "");
                 addProfile();
             }
         });

        goTologin = findViewById(R.id.buttonGoToLogin);
        goTologin.setOnClickListener(view -> {
            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            RegisterActivity.this.finish();
        });
    }

    public void setupProfile(FirebaseAuth mAuth, String displayName, String photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        username = user.getDisplayName(); // TODO promise and future
                    }
                });
    }

    private void createAccount(FirebaseAuth auth, String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        //register user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    Log.d(TAG, "New user registration: " + task.isSuccessful());
                    hideAndDisplayUIElements();
                    if (!task.isSuccessful()) {
                        System.out.println("Authentication failed. " + task.getException());
                    }
                });
    }

    public void addProfile() {
        ArrayList<String> followed = new ArrayList<>();
        followed.add("");
        ArrayList<String> following = new ArrayList<>();
        following.add("");
        ArrayList<String> teams = new ArrayList<>();
        teams.add("");
        ArrayList<String> bookmarks = new ArrayList<>();
        bookmarks.add("");
        ArrayList<Idea> ideas = new ArrayList<>();
        ArrayList<String> forks = new ArrayList<>();
        forks.add("");
        ideas.add(new Idea("author","context", "content","title", true, forks));

        Profile profile = new Profile(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName(),
                followed, following, teams, ideas, bookmarks);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child("gino2").setValue(profile); //TODO replace with username

        sendEmailVerification();
    }

    public void hideAndDisplayUIElements() {
        goTologin.setAlpha(0);
        goTologin.setEnabled(false);
        rEmail.setAlpha(0);
        rEmail.setEnabled(false);
        rEmail.setFocusable(false);
        rPassword.setAlpha(0);
        rPassword.setEnabled(false);
        rPassword.setFocusable(false);
        done.setAlpha(1);
        rUsername.setAlpha(1);
        messageLbl.setAlpha(1);
        register.setAlpha(0);
        register.setEnabled(false);
    }

    private void sendEmailVerification() {
        // Disable buttons
        done.setAlpha(0);
        rUsername.setAlpha(0);
        messageLbl.setAlpha(0);
        rUsername.setEnabled(false);
        done.setEnabled(false);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    goTologin.setAlpha(1);
                    goTologin.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(RegisterActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
