package app.salvatop.brainstorm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.databinding.ActivityMainBinding
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
import timber.log.Timber

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireAuthListener: AuthStateListener
    private lateinit var currentUser: String

    private lateinit var adapter: CardIdeaAdapter

    private lateinit var ideaArrayList: ArrayList<Idea>
    private lateinit var allUsersIdeasArrayList: ArrayList<Idea>
    private lateinit var usersArrayList: ArrayList<Profile>

    private lateinit var binding: ActivityMainBinding

    //region *** APPLICATION STATE ***
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
        setSupportActionBar(binding.toolbar)

        ///Tune the menu bottom bar
        binding.bottomNavigation.elevation = 1f
        binding.bottomNavigation.itemIconSize = 70
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
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

        allUsersIdeasArrayList = ArrayList()

        binding.CoordinatorLayout.setOnClickListener {
            binding.EditTextMotto.visibility = INVISIBLE
            binding.EditTextMotto.isEnabled = false
            binding.textViewMotto.visibility = VISIBLE
            binding.EditTextOccupation.visibility = INVISIBLE
            binding.EditTextOccupation.isEnabled = false
            binding.textViewOccupation.visibility = VISIBLE
            binding.textViewMotto.visibility = VISIBLE
            binding.editTextCity.visibility = INVISIBLE
            binding.editTextCity.isEnabled = false
            binding.textViewCity.visibility = VISIBLE
        }
        //endregion

        //region *** SETUP USER DETAILS ***
        // set the avatar image if exists in the user profile or set the default from drawable
        try {
            if (firebaseAuth.currentUser?.photoUrl != null) {
                Glide.with(applicationContext)
                        .load(firebaseAuth.currentUser!!.photoUrl)
                        .into(binding.imagePublicProfileViewAvatar)
            } else {
                binding.imagePublicProfileViewAvatar.setImageDrawable(getDrawable(R.drawable.avatar))
            }
        } catch (e: Exception) {
           Timber.d(e.message.toString())
        }

        ///set username
        val username = findViewById<TextView>(R.id.textViewDisplayName)
        username.text = currentUser

        val mottoDB = database.getReference("users").child(currentUser).child("motto")
        val cityDB = database.getReference("users").child(currentUser).child("city")
        val occupationDB = database.getReference("users").child(currentUser).child("occupation")

        mottoDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.i("Value is: %s", dataSnapshot.value)
                binding.textViewMotto.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Timber.tag("FIREBASE").w(error.toException(), "Failed to read value.")
            }
        })

        occupationDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.i("Value is: %s", dataSnapshot.value)
                binding.textViewOccupation.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Timber.tag("FIREBASE").w(error.toException(), "Failed to read value.")
            }
        })

        cityDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Timber.d("Value is: %s", dataSnapshot.value)
                binding.textViewCity.text = (dataSnapshot.value as String?).toString()
            } override fun onCancelled(error: DatabaseError) {
                Timber.tag("FIREBASE").w(error.toException(), "Failed to read value.")
            }
        })
        //endregion

        //region *** SETUP THE RECYCLE VIEW WITH THE CARD VIEW ADAPTER ***
        binding.recycleView.layoutManager = LinearLayoutManager(this)

        ideaArrayList = ArrayList()
        adapter = CardIdeaAdapter(this, ideaArrayList)

        binding.recycleView.adapter = adapter

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
        binding.EditTextMotto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val myRef = database.getReference("users").child(currentUser).child("motto")
                myRef.setValue(binding.EditTextMotto.text.toString())
                binding.textViewMotto.text = binding.EditTextMotto.text
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.textViewMotto.setOnLongClickListener {
            binding.EditTextMotto.visibility = VISIBLE
            binding.EditTextMotto.isEnabled = true
            binding.EditTextOccupation.visibility = INVISIBLE
            binding.EditTextOccupation.isEnabled = false
            binding.editTextCity.visibility = INVISIBLE
            binding.editTextCity.isEnabled = false

            binding.EditTextMotto.requestFocus()
            binding.textViewMotto.visibility = INVISIBLE
            binding.textViewCity.visibility = VISIBLE
            binding.textViewOccupation.visibility = VISIBLE
            true
        }
        binding.EditTextOccupation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val myRef = database.getReference("users").child(currentUser).child("occupation")
                myRef.setValue(binding.EditTextOccupation.text.toString())
                binding.textViewOccupation.text = binding.EditTextOccupation.text
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.textViewOccupation.setOnLongClickListener {
            binding.EditTextOccupation.visibility = VISIBLE
            binding.EditTextOccupation.isEnabled = true
            binding.editTextCity.visibility = INVISIBLE
            binding.editTextCity.isEnabled = false
            binding.EditTextMotto.visibility = INVISIBLE
            binding.EditTextMotto.isEnabled = false

            binding.EditTextOccupation.requestFocus()
            binding.textViewOccupation.visibility = INVISIBLE
            binding.textViewMotto.visibility = VISIBLE
            binding.textViewCity.visibility = VISIBLE
            true

        }
        binding.editTextCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val myRef = database.getReference("users").child(currentUser).child("city")
                myRef.setValue(binding.editTextCity.text.toString())
                binding.textViewCity.text = binding.editTextCity.text
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.textViewCity.setOnLongClickListener {
            binding.editTextCity.visibility = VISIBLE
            binding.editTextCity.isEnabled = true
            binding.EditTextMotto.visibility = INVISIBLE
            binding.EditTextMotto.isEnabled = false
            binding.EditTextOccupation.visibility = INVISIBLE
            binding.EditTextOccupation.isEnabled = false

            binding.editTextCity.requestFocus()
            binding.textViewCity.visibility = INVISIBLE
            binding.textViewMotto.visibility = VISIBLE
            binding.textViewOccupation.visibility = VISIBLE
            true
        }
        //endregion

        //region *** ADD IDEA BUTTON ACTION ***
        binding.buttonAddIdea.setOnClickListener {
            val addIdeaFragment = AddIdeaFragment()
            supportFragmentManager.popBackStack()
            binding.recycleView.alpha = 0f
            binding.recycleView.isEnabled = false
            binding.textViewDisplayLabel.text = it.resources.getString(R.string.add_an_idea)
            supportFragmentManager.beginTransaction().add(R.id.frameLayout, addIdeaFragment).addToBackStack(null).commit()
        }
        //endregion
    }
    //endregion

    //region *** MENU HANDLER ***
    // Handle bottom bar menu item selection
    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                supportFragmentManager.popBackStack()
                binding.textViewDisplayLabel.text = "My Ideas"
                val mainActivity = Intent(this, MainActivity::class.java)
                mainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                mainActivity.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                mainActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                overridePendingTransition(0, 0)
                startActivity(mainActivity)
                overridePendingTransition(0, 0)
                binding.recycleView.alpha = 1f
                binding.recycleView.isEnabled = true
                true
            }
            R.id.bookmarks -> {
                val bookmarksFragment = BookmarksFragment()
                binding.textViewDisplayLabel.text = "Bookmark"
                supportFragmentManager.popBackStack()
                binding.recycleView.alpha = 0f
                binding.recycleView.isEnabled = false
                val bundle = Bundle()
                bundle.putString("user", currentUser)
                bookmarksFragment.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, bookmarksFragment).addToBackStack(null).commit()
                true
            }
            R.id.collaborate -> {
                val collaborateFragment = CollaborateFragment()
                binding.textViewDisplayLabel.text = "Collaborate"
                supportFragmentManager.popBackStack()
                binding.recycleView.alpha = 0f
                binding.recycleView.isEnabled = false
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, collaborateFragment).addToBackStack(null).commit()
                true
            }
            R.id.idea_feeds -> {
                val ideasFeed = IdeasFeedFragment()
                binding.textViewDisplayLabel.text = "Ideas Feed"
                supportFragmentManager.popBackStack()
                binding.recycleView.alpha = 0f
                binding.recycleView.isEnabled = false
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
            @SuppressLint("LogNotTimber")
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",  false)
                searchItem.collapseActionView()
                val userSearch = "$query"
                Log.i("QUERY", "looking for $query")
                usersArrayList.forEach { user ->
                    //Log.d("USERS LIST", user.displayName)
                    if (user.displayName == userSearch) {
                        val publicProfileFragment = PublicProfileFragment()
                        binding.recycleView.alpha = 0f
                        binding.recycleView.isEnabled = false
                        binding.textViewDisplayLabel.text = user.displayName

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
                Timber.i(idea.value.author + "-" + idea.value.title)
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