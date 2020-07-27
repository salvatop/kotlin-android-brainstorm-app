package app.salvatop.brainstorm.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.RegisterActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class CollaborateFragment : Fragment() {
    var firebaseAuth: FirebaseAuth? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firebaseAuth = FirebaseAuth.getInstance()

        /////TODO testing code
        //setupProfile(firebaseAuth!!,"Gigi la Trottola","https://firebasestorage.googleapis.com/v0/b/brainstorm-f3b48.appspot.com/o/eiffel-tower.jpg?alt=media&token=1c3332ec-6a44-4ef0-83bf-40a12fff86a0");
        ////TODO end of testing code
        return inflater.inflate(R.layout.fragment_collaborate, container, false)
    }

    fun setupProfile(mAuth: FirebaseAuth, displayName: String?, photoUrl: String?) {
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build()
        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }
    }

    fun removeUser(auth: FirebaseAuth) {
        val user = auth.currentUser!!
        user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show()
                        context?.startActivity(Intent(context, RegisterActivity::class.java))
                        activity?.finish()
                    } else {
                        Toast.makeText(context, "Failed to delete your account!", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    companion object {
        private const val TAG = "Fragment"
    }
}