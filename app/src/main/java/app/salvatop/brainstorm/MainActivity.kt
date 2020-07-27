package app.salvatop.brainstorm

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.fragment.AddIdeaFragment
import app.salvatop.brainstorm.fragment.CollaborateFragment
import app.salvatop.brainstorm.fragment.PublicProfileFragment
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import app.salvatop.brainstorm.repository.FirebaseRepository
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var firebaseAuth: FirebaseAuth? = null
    private var fireAuthListener: AuthStateListener? = null
    private var adapter: CardIdeaAdapter? = null
    private var ideaArrayList: ArrayList<Idea>? = null
    private var usersArrayList: ArrayList<Profile>? = null
    private var label: TextView? = null


    @SuppressLint("SetTextI18n")
    private fun initializeButtonsTextEditAndView() {
        val database = FirebaseDatabase.getInstance()
        val user: String = firebaseAuth?.currentUser?.displayName.toString()

        val mottoEdit: EditText = findViewById(R.id.EditTextMotto)
        val occupationEdit: EditText = findViewById(R.id.EditTextOccupation)
        val cityEdit: EditText = findViewById(R.id.EditTextCity)
        val city: TextView = findViewById(R.id.textViewPublicProfileCity)
        val motto: TextView = findViewById(R.id.textViewPublicProfileMotto)
        val occupation: TextView = findViewById(R.id.textViewPublicProfileOccupation)

        label = findViewById(R.id.textViewDisplayLabel)

        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        ///fetch ideas for the profile from db TODO write a coroutine

        ideaArrayList = ArrayList()
        adapter = CardIdeaAdapter(this, ideaArrayList!!)
        recyclerView.adapter = adapter
        ideaArrayList = loadIdeasFromDB(ideaArrayList!!, adapter!!, user)
        adapter!!.notifyDataSetChanged()


        usersArrayList = ArrayList()
        usersArrayList = loadAllUserFromDB()
        print(getUser("Augusto I"))

        //button to add idea to the profile
        val addIdea: Button = findViewById(R.id.buttonAddIdea)
        addIdea.setOnClickListener {
            val addIdeaFragment = AddIdeaFragment()
            supportFragmentManager.popBackStack()
            recyclerView.alpha = 0f
            recyclerView.isEnabled = false
            label?.text = "Add an Idea"
            supportFragmentManager.beginTransaction().add(R.id.frameLayout, addIdeaFragment).addToBackStack(null).commit()
        }

        mottoEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                motto.text = mottoEdit.text
                val myRef = database.getReference("users").child(user).child("motto")
                myRef.setValue(mottoEdit.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val myRef = database.getReference("users").child(user).child("motto")
                motto.text = myRef.toString()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        motto.setOnLongClickListener {
            mottoEdit.visibility = VISIBLE
            mottoEdit.isEnabled = true
            occupationEdit.visibility = INVISIBLE
            occupationEdit.isEnabled = false
            cityEdit.visibility = INVISIBLE
            cityEdit.isEnabled = false

            //mottoEdit.requestFocus()
            motto.visibility = INVISIBLE
            city.visibility = VISIBLE
            occupation.visibility = VISIBLE
            true
        }
        mottoEdit.setOnClickListener {
            mottoEdit.visibility = INVISIBLE
            mottoEdit.isEnabled = false
            motto.visibility = VISIBLE
        }

        occupationEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                occupation.text = occupationEdit.text
                val myRef = database.getReference("users").child(user).child("occupation")
                myRef.setValue(occupationEdit.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        occupation.setOnLongClickListener {
            occupationEdit.visibility = VISIBLE
            occupationEdit.isEnabled = true
            cityEdit.visibility = INVISIBLE
            cityEdit.isEnabled = false
            mottoEdit.visibility = INVISIBLE
            mottoEdit.isEnabled = false

            occupationEdit.requestFocus()
            occupation.visibility = INVISIBLE
            motto.visibility = VISIBLE
            city.visibility = VISIBLE
            true

        }
        cityEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                city.text = cityEdit.text
                val myRef = database.getReference("users").child(user).child("city")
                myRef.setValue(cityEdit.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        city.setOnLongClickListener {
            cityEdit.visibility = VISIBLE
            cityEdit.isEnabled = true
            mottoEdit.visibility = INVISIBLE
            mottoEdit.isEnabled = false
            occupationEdit.visibility = INVISIBLE
            occupationEdit.isEnabled = false

            cityEdit.requestFocus()
            city.visibility = INVISIBLE
            motto.visibility = VISIBLE
            occupation.visibility = VISIBLE
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        initializeButtonsTextEditAndView()

        ////Checking user session
        fireAuthListener = AuthStateListener { firebaseAuth1: FirebaseAuth ->
            val user1 = firebaseAuth1.currentUser
            if (user1 == null) {
                //user not logged in
                val mainActivity = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(mainActivity)
            }
        }

        /// set the avatar image if exists in the user profile or set the default from drawable
        val avatar = findViewById<ImageView>(R.id.imagePublicProfileViewAvatar)
        try {
            if (firebaseAuth!!.currentUser?.photoUrl != null) {
                Glide.with(applicationContext)
                        .load(firebaseAuth!!.currentUser!!.photoUrl)
                        .into(avatar)
            } else {
                avatar.setImageDrawable(getDrawable(R.drawable.avatar))
            }
        } catch (e: Exception) {
            Log.d("FIREBASE", e.message.toString())
        }

        ///set user profile info in the UI
        val username = findViewById<TextView>(R.id.textViewDisplayName)
        username.text = firebaseAuth!!.currentUser?.displayName


        // Sets the Toolbar to act as the ActionBar for this Activity window.
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        ///Tune the menu bottom bar
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.elevation = 1f
        bottomNavigationView.itemIconSize = 70
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun loadIdeasFromDB(listOdIdeas: ArrayList<Idea>, dataAdapter: CardIdeaAdapter, username: String) : ArrayList<Idea> {
        FirebaseDatabase.getInstance().reference.child("users").child(username).child("ideas")
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

        dataAdapter.notifyDataSetChanged()
        return listOdIdeas
    }

    private fun loadAllUserFromDB() : ArrayList<Profile> {
        val listOUsers: ArrayList<Profile> = ArrayList()
        FirebaseDatabase.getInstance().reference.child("users")
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

    // Handle bottom bar menu item selection
    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        val collaborateFragment = CollaborateFragment()
        return when (item.itemId) {
            R.id.home -> {
                recyclerView.alpha = 1f
                recyclerView.isEnabled = true
                adapter!!.notifyDataSetChanged()
                supportFragmentManager.popBackStack()
                label?.text = "My Ideas"
                true
            }
            R.id.bookmarks -> {
                label?.text = "Bookmark"

                true
            }
            R.id.settings -> {
                label?.text = "Collaborate"
                supportFragmentManager.popBackStack()
                recyclerView.alpha = 0f
                recyclerView.isEnabled = false
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, collaborateFragment).addToBackStack(null).commit()
                true
            }
            R.id.idea_feeds -> {
                label?.text = "Ideas Feed"
                true
            }
            else -> false
        }
    }
    // Handle toolbar menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logoff -> {
                signOut(firebaseAuth)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Handle  toolbar menu search
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object:  SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    searchView.setQuery("",  false)
                    searchItem.collapseActionView()
                    val userSearch = "$query"
                    Log.d("QUERY", "looking for $query")
                    usersArrayList?.forEach { user ->
                        //Log.d("USERS LIST", user.displayName)
                        if (user.displayName == userSearch) {
                            val publicProfileFragment = PublicProfileFragment()
                            val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
                            recyclerView.alpha = 0f
                            recyclerView.isEnabled = false
                            label?.text = user.displayName

                            val bundle = Bundle()
                            bundle.putSerializable("profile",user)
                            publicProfileFragment.arguments = bundle

                            supportFragmentManager.popBackStack()
                            supportFragmentManager.beginTransaction().add(R.id.frameLayout, publicProfileFragment).addToBackStack("publicProfileFragment").commit()
                            return true
                        }
                    }
                    Toast.makeText(this@MainActivity, "This user: $query does not exist", Toast.LENGTH_SHORT).show()
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        return false
    }

    //feature to get a users by username from the database
    private fun getUser(username: String?) : Profile? {
        var profile: Profile? = null
        val database = FirebaseDatabase.getInstance()
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

    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(fireAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (fireAuthListener != null) {
            firebaseAuth!!.removeAuthStateListener(fireAuthListener!!)
        }
    }

    private fun signOut(auth: FirebaseAuth?) {
        auth!!.signOut()
    }
}