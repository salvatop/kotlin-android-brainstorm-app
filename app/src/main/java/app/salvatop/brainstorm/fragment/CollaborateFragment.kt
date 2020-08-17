package app.salvatop.brainstorm.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.salvatop.brainstorm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CollaborateFragment : Fragment() {

    private lateinit var following: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_collaborate, container,false) as ViewGroup
        val database = FirebaseDatabase.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser?.displayName.toString()
        following = view.findViewById(R.id.textViewFollowing)
        var iFollowing = ""

        FirebaseDatabase.getInstance().reference.child("users").child(currentUser).child("following")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                         iFollowing += snapshot.value
                            iFollowing += " "
                            if (snapshot.value != "none") {
                                following.text = "following[$iFollowing]"
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        return view
    }
}