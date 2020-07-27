package app.salvatop.brainstorm.repository

import android.util.Log
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository  {

    var firebaseAuth: FirebaseAuth
    var database: FirebaseDatabase

    init {
        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }


     fun getUser(username: String?) : Profile? {
        var profile: Profile? = null
        val myRef = database.getReference("users").child(username!!)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                val userProfile: Profile? = dataSnapshot.getValue(Profile::class.java)
                if (userProfile != null) {
                    profile = userProfile
                } else {
                    profile = null
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })
        return profile
    }

    fun loadAllUserFromDB() : ArrayList<Profile> {
        val listOUsers: ArrayList<Profile> = ArrayList()
        database.reference.child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val profile: Profile? = snapshot.getValue(Profile::class.java)
                            if (profile != null) {
                                listOUsers.add(profile)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        return listOUsers
    }

    fun loadIdeasFromDB(listOdIdeas: ArrayList<Idea>,  username: String) : ArrayList<Idea> {
        database.reference.child("users").child(username).child("ideas")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val idea: Idea? = snapshot.getValue(Idea::class.java)
                            if (idea != null) {
                                listOdIdeas.add(idea)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        return listOdIdeas
    }

}
