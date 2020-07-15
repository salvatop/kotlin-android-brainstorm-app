package app.salvatop.brainstorm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "FIREBASE";
    private FirebaseAuth firebaseAuth;

    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.buttonLogin);
        Button register = findViewById(R.id.buttonRegister);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmailPasswordFields()) {
                    login(email.getText().toString(),password.getText().toString(),firebaseAuth);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registration = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registration);
            }
        });
    }

    public void login(String username, String password, FirebaseAuth mAuth){
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            if(user.isEmailVerified()) {
                                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                            } else {
                                Toast.makeText(LoginActivity.this, "Verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateEmailPasswordFields() {
        boolean valid = true;

        String emailToVerify = email.getText().toString();
        if (TextUtils.isEmpty(emailToVerify)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordToVerify = password.getText().toString();
        if (TextUtils.isEmpty(passwordToVerify)) {
            password.setError("Required.");
            valid = false;
        } else {
           password.setError(null);
        }

        return valid;
    }

    private void passwordChange(FirebaseAuth auth, String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

