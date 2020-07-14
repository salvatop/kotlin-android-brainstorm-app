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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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

        login.setOnClickListener(this);
        register.setOnClickListener(this);
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
                                redirect();
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

    public void redirect(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonLogin:
                if (validateEmailPassword()) {
                    login(email.getText().toString(),password.getText().toString(),firebaseAuth);
                    break;
                }
                break;
            case R.id.buttonRegister:
                Intent registration = new Intent(this, RegisterActivity.class);
                startActivity(registration);
                break;
            default: System.out.println("error");
        }
    }

    private boolean validateEmailPassword() {
        boolean valid = true;

        String email2 = email.getText().toString();
        if (TextUtils.isEmpty(email2)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String password2 = password.getText().toString();
        if (TextUtils.isEmpty(password2)) {
            password.setError("Required.");
            valid = false;
        } else {
           password.setError(null);
        }

        return valid;
    }
}

