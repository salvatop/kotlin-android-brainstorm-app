package app.salvatop.brainstorm.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class PublicProfileFragment : Fragment() {

    private lateinit var mottoDB: DatabaseReference
    private lateinit var cityDB: DatabaseReference
    private lateinit var occupationDB: DatabaseReference

    @SuppressLint("LogNotTimber")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_public_profile, container, false) as ViewGroup
        val database = FirebaseDatabase.getInstance()
        val motto: TextView = view.findViewById(R.id.textViewPublicProfileMotto)
        val occupation: TextView = view.findViewById(R.id.textViewPublicProfileOccupation)
        val city: TextView = view.findViewById(R.id.textViewPublicProfileCity)

        val profile: Profile = arguments!!.getSerializable("profile") as Profile
        Timber.d(profile.displayName)
        val ideaArrayList: ArrayList<Idea> = getIdeasFromSerializable(profile)


        mottoDB = database.getReference("users").child(profile.displayName).child("motto")
        cityDB = database.getReference("users").child(profile.displayName).child("city")
        occupationDB = database.getReference("users").child(profile.displayName).child("occupation")

        val firebaseAuth = FirebaseAuth.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleViewPublicProfile)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = CardIdeaAdapter(context!!, ideaArrayList)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        val follow: Button = view.findViewById(R.id.buttonFollow)
        follow.setOnClickListener {

            val userToFollow = profile.displayName
            val whoIsFollow = firebaseAuth.currentUser?.displayName.toString()
            Log.d("USERS", "$whoIsFollow $userToFollow")

            // Start a coroutine
            GlobalScope.launch {
                //TODO if already followed un-follow else
                delay(1000)
                followUnfollowUsers(userToFollow, whoIsFollow, "follow")
            }
            Log.d("BUTTON", "user followed")
        }
        // Start a coroutine in the UI (Main thread)
        GlobalScope.launch {
            delay(1000)
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                mottoDB.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                        motto.text = (dataSnapshot.value as String?).toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("FIREBASE", "Failed to read value.", error.toException())
                    }
                })

                occupationDB.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                        occupation.text = (dataSnapshot.value as String?).toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("FIREBASE", "Failed to read value.", error.toException())
                    }
                })

                cityDB.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                        city.text = (dataSnapshot.value as String?).toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("FIREBASE", "Failed to read value.", error.toException())
                    }
                })
            }
        }
        return view
    }

    private fun getIdeasFromSerializable(profile: Profile): ArrayList<Idea> {
        val ideas: ArrayList<Idea> = ArrayList()
        for (idea in profile.ideas) {
            ideas.add(idea.value)
        }
        return ideas
    }
    private fun followUnfollowUsers(userToFollow: String?, whoIsFollow: String?, action: String?) {

        val database = FirebaseDatabase.getInstance()

        //add current user display name into followed record in the DB of the user ti follow
        val myRef = database.getReference("users").child(userToFollow!!).child("followed")

        //add the user to follow display name into the current user following record in the DB
        val myRef2 = database.getReference("users").child(whoIsFollow!!).child("following")

        if(action == "follow") {
            myRef.child(whoIsFollow).setValue(whoIsFollow)
            myRef2.child(userToFollow).setValue(userToFollow)
        } else {
            myRef.child(whoIsFollow).removeValue()
            myRef2.child(userToFollow).removeValue()
        }
    }
}