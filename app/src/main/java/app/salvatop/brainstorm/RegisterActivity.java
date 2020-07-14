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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "FIREBASE";
    FirebaseAuth firebaseAuth;

    private EditText rEmail, rPassword, rUsername;
    private Button register;
    private TextView emailLbl, passLbl, usernameLbl, messageLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        rEmail = findViewById(R.id.editTextTextRegisterEmailAddress);
        rPassword = findViewById(R.id.editTextTextRegisterPassword);
        rUsername = findViewById(R.id.editTextTextUsername);

        register = findViewById(R.id.buttonRegister);
        register.setOnClickListener(this);



        passLbl = findViewById(R.id.passwordLabelRegister);
        usernameLbl = findViewById(R.id.usernameLabelRegister);
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

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        //register user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "New user registration: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            System.out.println("Authentication failed. " + task.getException());
                        } else {
                            RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
                        }
                    }
                });
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
                String email = rEmail.getText().toString().trim();
                String pass = rPassword.getText().toString().trim();
                System.out.println(email + " " + pass);
                createAccount(email, pass);
            default:
                System.out.println("error");
        }

    }
}
