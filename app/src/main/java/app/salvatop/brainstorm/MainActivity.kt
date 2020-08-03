package app.salvatop.brainstorm

import android.annotation.SuppressLint
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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.fragment.*
import app.salvatop.brainstorm.model.Idea
import app.salvatop.brainstorm.model.Profile
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireAuthListener: AuthStateListener
    private lateinit var currentUser: String
    private lateinit var adapter: CardIdeaAdapter
    private lateinit var ideaArrayList: ArrayList<Idea>
    private lateinit var allUsersIdeasArrayList: ArrayList<Idea>
    private lateinit var usersArrayList: ArrayList<Profile>
    private lateinit var label: TextView

    //region *** APPLICATION STATE ***
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

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        ///Tune the menu bottom bar
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.elevation = 1f
        bottomNavigationView.itemIconSize = 70
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
        firebaseAuth.addAuthStateListener(fireAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(fireAuthListener)
    }
    //endregion

    //region *** INITIALIZE UI ELEMENTS ***

    private fun initializeButtonsTextEditAndView() {

        //region  *** VARIABLES ***
        val database = FirebaseDatabase.getInstance()
        currentUser = firebaseAuth.currentUser?.displayName.toString()

        val mottoEdit: EditText = findViewById(R.id.EditTextMotto)
        val occupationEdit: EditText = findViewById(R.id.EditTextOccupation)
        val cityEdit: EditText = findViewById(R.id.editTextCity)

        val city: TextView = findViewById(R.id.textViewCity)
        val motto: TextView  = findViewById(R.id.textViewMotto)
        val occupation: TextView = findViewById(R.id.textViewOccupation)

        allUsersIdeasArrayList = ArrayList()
        label = findViewById(R.id.textViewDisplayLabel)

        val layout: CoordinatorLayout = findViewById(R.id.CoordinatorLayout)
        layout.setOnClickListener {
            mottoEdit.visibility = INVISIBLE
            mottoEdit.isEnabled = false
            motto.visibility = VISIBLE
            occupationEdit.visibility = INVISIBLE
            occupationEdit.isEnabled = false
            occupation.visibility = VISIBLE
            motto.visibility = VISIBLE
            cityEdit.visibility = INVISIBLE
            cityEdit.isEnabled = false
            city.visibility = VISIBLE
        }
        //endregion

        //region *** SETUP USER DETAILS ***
        // set the avatar image if exists in the user profile or set the default from drawable
        val avatar = findViewById<ImageView>(R.id.imagePublicProfileViewAvatar)
        try {
            if (firebaseAuth.currentUser?.photoUrl != null) {
                Glide.with(applicationContext)
                        .load(firebaseAuth.currentUser!!.photoUrl)
                        .into(avatar)
            } else {
                avatar.setImageDrawable(getDrawable(R.drawable.avatar))
            }
        } catch (e: Exception) {
            Log.d("FIREBASE", e.message.toString())
        }

        ///set username
        val username = findViewById<TextView>(R.id.textViewDisplayName)
        username.text = currentUser

        val mottoDB = database.getReference("users").child(currentUser).child("motto")
        val cityDB = database.getReference("users").child(currentUser).child("city")
        val occupationDB = database.getReference("users").child(currentUser).child("occupation")

        mottoDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                motto.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })

        occupationDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                occupation.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })

        cityDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FIREBASE", "Value is: " + dataSnapshot.value)
                city.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })
        //endregion

        //region *** SETUP THE RECYCLE VIEW WITH THE CARD VIEW ADAPTER ***
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        ideaArrayList = ArrayList()
        adapter = CardIdeaAdapter(this, ideaArrayList)

        recyclerView.adapter = adapter

        usersArrayList = loadAllUserFromDB()

        //create local list of my ideas
        FirebaseDatabase.getInstance().reference.child("users").child(currentUser).child("ideas")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val idea: Idea? = snapshot.getValue(Idea::class.java)
                            if (idea!!.title != "none") {
                                ideaArrayList.add(idea)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        // Start a coroutine
        GlobalScope.launch {
            delay(1500)
            allUsersIdeasArrayList = getAllTheIdeas(usersArrayList)
        }
        //endregion

        //region *** SETUP TEXT LISTENER FOR UPDATE USER PROFILE ***
        mottoEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val myRef = database.getReference("users").child(currentUser).child("motto")
                myRef.setValue(mottoEdit.text.toString())
                motto.text = mottoEdit.text
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        motto.setOnLongClickListener {
            mottoEdit.visibility = VISIBLE
            mottoEdit.isEnabled = true
            occupationEdit.visibility = INVISIBLE
            occupationEdit.isEnabled = false
            cityEdit.visibility = INVISIBLE
            cityEdit.isEnabled = false

            mottoEdit.requestFocus()
            motto.visibility = INVISIBLE
            city.visibility = VISIBLE
            occupation.visibility = VISIBLE
            true
        }
        occupationEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val myRef = database.getReference("users").child(currentUser).child("occupation")
                myRef.setValue(occupationEdit.text.toString())
                occupation.text = occupationEdit.text
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
                val myRef = database.getReference("users").child(currentUser).child("city")
                myRef.setValue(cityEdit.text.toString())
                city.text = cityEdit.text
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
        //endregion

        //region *** ADD IDEA BUTTON ACTION ***
        val addIdea: Button = findViewById(R.id.buttonAddIdea)
        addIdea.setOnClickListener {
            val addIdeaFragment = AddIdeaFragment()
            supportFragmentManager.popBackStack()
            recyclerView.alpha = 0f
            recyclerView.isEnabled = false
            label.text = it.resources.getString(R.string.add_an_idea)
            supportFragmentManager.beginTransaction().add(R.id.frameLayout, addIdeaFragment).addToBackStack(null).commit()
        }
        //endregion
    }
    //endregion

    //region *** MENU HANDLER ***
    // Handle bottom bar menu item selection
    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        return when (item.itemId) {
            R.id.home -> {
                supportFragmentManager.popBackStack()
                label.text = "My Ideas"
                val mainActivity = Intent(this, MainActivity::class.java)
                mainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                mainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                mainActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                overridePendingTransition(0, 0)
                startActivity(mainActivity)
                overridePendingTransition(0, 0)
                recyclerView.alpha = 1f
                recyclerView.isEnabled = true
                adapter.notifyDataSetChanged()
                true
            }
            R.id.bookmarks -> {
                val bookmarksFragment = BookmarksFragment()
                label.text = "Bookmark"
                supportFragmentManager.popBackStack()
                recyclerView.alpha = 0f
                recyclerView.isEnabled = false
                val bundle = Bundle()
                bundle.putString("user", currentUser)
                bookmarksFragment.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, bookmarksFragment).addToBackStack(null).commit()
                true
            }
            R.id.collaborate -> {
                val collaborateFragment = CollaborateFragment()
                label.text = "Collaborate"
                supportFragmentManager.popBackStack()
                recyclerView.alpha = 0f
                recyclerView.isEnabled = false
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, collaborateFragment).addToBackStack(null).commit()
                true
            }
            R.id.idea_feeds -> {
                val ideasFeed = IdeasFeedFragment()
                label.text = "Ideas Feed"
                supportFragmentManager.popBackStack()
                recyclerView.alpha = 0f
                recyclerView.isEnabled = false
                val bundle = Bundle()
                bundle.putSerializable("ideas",allUsersIdeasArrayList)
                ideasFeed.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, ideasFeed).addToBackStack(null).commit()
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

        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object:  SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",  false)
                searchItem.collapseActionView()
                val userSearch = "$query"
                Log.d("QUERY", "looking for $query")
                usersArrayList.forEach { user ->
                    //Log.d("USERS LIST", user.displayName)
                    if (user.displayName == userSearch) {
                        val publicProfileFragment = PublicProfileFragment()
                        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
                        recyclerView.alpha = 0f
                        recyclerView.isEnabled = false
                        label.text = user.displayName

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
        return true
    }
    //endregion

    //region *** UTIL FUNCTIONS ***

    private fun signOut(auth: FirebaseAuth?) {
        auth!!.signOut()
    }

    private fun getAllTheIdeas(users: ArrayList<Profile>) : ArrayList<Idea> {
        val ideas: ArrayList<Idea> =  ArrayList()
        for(profile in users) {
            for (idea in profile.ideas) {
                if (idea.value.visibility != "true") { continue }
                Log.d("LOAD IDEAS", idea.value.author + "-" + idea.value.title)
                val oneIdea = idea.value
                ideas.add(oneIdea)
            }
        }
        return  ideas
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
    //endregion
}