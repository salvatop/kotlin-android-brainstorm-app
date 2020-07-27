package app.salvatop.brainstorm.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.model.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PublicProfileFragment : Fragment() {
    private var profile = Profile()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profile = arguments!!.getSerializable("profile") as Profile
        return inflater.inflate(R.layout.fragment_public_profile, container, false)
    }


    //feature to follow a new user adding your username in his list of followed by
    fun followAUser(toFollow: String?, whoIsFollow: String?, action: String?) {
        /// call -> updateFollowingUsersList(String username)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(toFollow!!).child("followed")
        //TODO implement actions to add/remove whoIsFollow
        ////....
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })
    }

    //feature to update you list of following user after you add a new one
    fun updateFollowingUsersList(whoIsFollow: String?, toFollow: String?, action: String?) {
        /// call -> followAUser(String toFollow, String whoIsFollow)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(whoIsFollow!!).child("following")
        //TODO implement actions to add/remove toFollow
        ////....
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })
    }
}