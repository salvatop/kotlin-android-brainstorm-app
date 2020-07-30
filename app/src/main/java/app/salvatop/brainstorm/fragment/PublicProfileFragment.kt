package app.salvatop.brainstorm.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PublicProfileFragment : Fragment() {
    private var profile = Profile()
    private var ideaArrayList: ArrayList<Idea>? = null

    private fun getIdeasFromSerializable(profile: Profile): ArrayList<Idea> {
        val ideas: ArrayList<Idea> = ArrayList()
        for (idea in profile.ideas) {
            ideas.add(idea.value)
        }
        return ideas
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_public_profile, container,false) as ViewGroup

        profile = arguments!!.getSerializable("profile") as Profile
        Log.d("PROFILE", profile.displayName)
        ideaArrayList = getIdeasFromSerializable(profile)

        val firebaseAuth = FirebaseAuth.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleViewPublicProfile)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        val adapter = CardIdeaAdapter(context!!, ideaArrayList!!)
        recyclerView.adapter = adapter

        val follow: Button = view.findViewById(R.id.buttonFollow)
        follow.setOnClickListener {

            val userToFollow = profile.displayName
            val whoIsFollow = firebaseAuth.currentUser?.displayName.toString()
            Log.d("USERS", "$whoIsFollow $userToFollow")
            // Start a coroutine
            GlobalScope.launch {
                delay(1500)
                followUnfollowUsers(userToFollow, whoIsFollow, "follow")
            }
            Log.d("BUTTON", "user followed")
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d("IDEAS", "${ideaArrayList?.size}")
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