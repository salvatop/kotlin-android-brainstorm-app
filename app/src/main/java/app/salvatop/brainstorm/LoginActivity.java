package app.salvatop.brainstorm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "FIREBASE";
    private FirebaseAuth firebaseAuth;
    private int loginAttempts = 0;

    private TextInputLayout email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        /////TODO testing code
        login("salvatop78@gmail.com", "123456", firebaseAuth);
        /////TODO end of testing code

        MaterialButton login = findViewById(R.id.buttonLogin);
        MaterialButton register = findViewById(R.id.buttonRegister);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        login.setOnClickListener(view -> {
            //if failed three attempt to login, load the alert to reset password
            loginAttempts++;
            if (loginAttempts > 3) {
                loginAttempts = 0;
                // Build an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                // Set the custom layout as alert dialog view
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.reset_password_dialog_layout,null);
                builder.setView(dialogView);

                builder.setTitle("Reset Password");
                builder.setNegativeButton("close", (dialog, arg1) -> dialog.cancel());
                final AlertDialog dialog = builder.create();

                // Get the custom alert dialog view widgets reference
                Button send = dialogView.findViewById(R.id.resetPasswordButton);
                EditText emailResetPassword = dialogView.findViewById(R.id.resetPasswordTextField);
                send.setOnClickListener(view1 -> {
                    String emailToReset = emailResetPassword.getText().toString().trim();
                    if (validateEmailPasswordResetFields(emailResetPassword)) {
                        passwordChange(firebaseAuth, emailToReset);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else if (validateEmailPasswordFields()) {
                login(Objects.requireNonNull(email.getEditText()).getText().toString().trim(),
                        Objects.requireNonNull(password.getEditText()).getText().toString().trim(),firebaseAuth);
            }
        });
        register.setOnClickListener(view -> {
            Intent registration = new Intent(LoginActivity.this, RegisterActivity.class);
            // set an exit transition
            getWindow().setExitTransition(new Explode());
            startActivity(registration,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    public void login(String username, String password, FirebaseAuth mAuth){
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
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
                });
    }

    private boolean validateEmailPasswordFields() {
        boolean valid = true;

        String emailToVerify = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        if (TextUtils.isEmpty(emailToVerify)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordToVerify = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        if (TextUtils.isEmpty(passwordToVerify)) {
            password.setError("Required.");
            valid = false;
        } else {
           password.setError(null);
        }

        return valid;
    }

    private boolean validateEmailPasswordResetFields(EditText email) {
        boolean valid = true;

        String emailToVerify = Objects.requireNonNull(email.getText().toString().trim());
        if (TextUtils.isEmpty(emailToVerify)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }
    private void passwordChange(FirebaseAuth auth, String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "sent instructions to " + email + " if exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

