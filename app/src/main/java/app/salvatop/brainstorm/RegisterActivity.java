package app.salvatop.brainstorm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.salvatop.brainstorm.model.Idea;
import app.salvatop.brainstorm.model.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FIREBASE";
    private FirebaseAuth firebaseAuth;

    private EditText rEmail,rPassword,rUsername;
    private Button register, done;
    private TextView emailLbl, passLbl, usernameLbl, messageLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ///setup test profile
        //String imageUrl = "https://firebasestorage.googleapis.com/v0/b/brainstorm-f3b48.appspot.com/o/royalty-free-transparent-images-9.png?alt=media&token=2a8913ab-4506-4f11-9a14-df560951b1f4";
        //setupProfile(firebaseAuth,"I'm Mike the user number 1",imageUrl);
        firebaseAuth = FirebaseAuth.getInstance();

        rEmail = findViewById(R.id.editTextTextRegisterEmailAddress);
        rEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        rPassword = findViewById(R.id.editTextTextRegisterPassword);
        rUsername = findViewById(R.id.editTextTextUsername);
        emailLbl = findViewById(R.id.emailLabelRegister);
        passLbl = findViewById(R.id.passwordLabelRegister);
        usernameLbl = findViewById(R.id.usernameLabelRegister);
        messageLbl = findViewById(R.id.messageLabelRegister);

        register = findViewById(R.id.buttonRegister);
        done = findViewById(R.id.buttonRegisterDone);
        done.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    public String setupProfile(FirebaseAuth mAuth, String displayName, String photoUrl){
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

    public void register(FirebaseAuth mAuth, String password, String email){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Log.d(TAG, Objects.requireNonNull(user.getEmail()));
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        System.out.println(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonRegister:
                System.out.println(rEmail.getText().toString().trim());
                register(firebaseAuth, rEmail.getText().toString().trim(),rPassword.getText().toString().trim());
                    done.setEnabled(false);
                    rEmail.setAlpha(0);
                    rEmail.setEnabled(false);
                    rEmail.setFocusable(false);
                    emailLbl.setAlpha(0);
                    passLbl.setAlpha(0);
                    rPassword.setAlpha(0);
                    rPassword.setEnabled(false);
                    rPassword.setFocusable(false);
                    usernameLbl.setAlpha(1);
                    rUsername.setAlpha(1);
                    messageLbl.setAlpha(1);
                    register.setAlpha(0);
                    done.setEnabled(true);
                    done.setAlpha(1);
                    System.out.println(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
                    break;
            case R.id.buttonRegisterDone:
                register.setEnabled(false);
                if(!setupProfile(firebaseAuth,rUsername.getText().toString(),"").isEmpty()) {
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
                            followed,following,teams,ideas,bookmarks);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String id = String.valueOf(getNbOfRecords() + 1);
                    DatabaseReference myRef = database.getReference("users");
                    myRef.child(id).setValue(profile);
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                    break;
                } else {System.out.println("error");}
        }
    }

    public int getNbOfRecords() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        return Integer.parseInt(Objects.requireNonNull(myRef.getKey()));
    }
}