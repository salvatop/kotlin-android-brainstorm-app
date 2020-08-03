package app.salvatop.brainstorm

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.transition.Explode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout

    private var loginAttempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        /////TODO testing code
        login("salvatop78@gmail.com", "123456", firebaseAuth)
        /////TODO end of testing code
        val login = findViewById<MaterialButton>(R.id.buttonLogin)
        val register = findViewById<MaterialButton>(R.id.buttonRegister)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        login.setOnClickListener {
            //if failed three attempt to login, load the alert to reset password
            loginAttempts++
            if (loginAttempts > 3) {
                loginAttempts = 0
                // Build an AlertDialog
                val builder = AlertDialog.Builder(this@LoginActivity)

                // Set the custom layout as alert dialog view
                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.reset_password_dialog_layout, null)
                builder.setView(dialogView)
                builder.setTitle("Reset Password")
                builder.setNegativeButton("close") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                val dialog = builder.create()

                // Get the custom alert dialog view widgets reference
                val send = dialogView.findViewById<Button>(R.id.resetPasswordButton)
                val emailResetPassword = dialogView.findViewById<EditText>(R.id.resetPasswordTextField)
                send.setOnClickListener {
                    val emailToReset = emailResetPassword.text.toString()
                    if (validateEmailPasswordResetFields(emailResetPassword)) {
                        passwordChange(firebaseAuth, emailToReset)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            } else if (validateEmailPasswordFields()) {
                val email = email.editText?.text.toString()
                val password = password.editText?.text.toString()
                login(email, password, firebaseAuth)
            }
        }
        register.setOnClickListener {
            val registration = Intent(this@LoginActivity, RegisterActivity::class.java)
            // set an exit transition
            window.exitTransition = Explode()
            startActivity(registration,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    private fun login(username: String?, password: String?, mAuth: FirebaseAuth) {
        mAuth.signInWithEmailAndPassword(username!!, password!!)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SIGNIN", "signInWithEmail:success")
                        val user = mAuth.currentUser!!
                        if (user.isEmailVerified) {
                            val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(mainActivity)
                        } else {
                            Toast.makeText(this@LoginActivity, "Verify your email.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.w("SIGNIN", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed." + task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateEmailPasswordFields(): Boolean {
        var valid = true
        val emailToVerify = email.editText?.text.toString()
        if (TextUtils.isEmpty(emailToVerify)) {
            email.error = "Required."
            valid = false
        } else {
            email.error = null
        }
        val passwordToVerify = password.editText?.text.toString()
        if (TextUtils.isEmpty(passwordToVerify)) {
            password.error = "Required."
            valid = false
        } else {
            password.error = null
        }
        return valid
    }

    private fun validateEmailPasswordResetFields(email: EditText): Boolean {
        var valid = true
        val emailToVerify = email.text.toString()
        if (TextUtils.isEmpty(emailToVerify)) {
            email.error = "Required."
            valid = false
        } else {
            email.error = null
        }
        return valid
    }

    private fun passwordChange(auth: FirebaseAuth, email: String) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "sent instructions to $email if exist.", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}