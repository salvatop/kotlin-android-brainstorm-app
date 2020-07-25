package app.salvatop.brainstorm

import android.app.SearchManager
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.fragment.AddIdeaFragment
import app.salvatop.brainstorm.fragment.TeamFragment
import app.salvatop.brainstorm.model.Idea
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnLongClickListener {
    private var firebaseAuth: FirebaseAuth? = null
    private var fireAuthListener: AuthStateListener? = null
    private var adapter: CardIdeaAdapter? = null
    private var ideaArrayList: ArrayList<Idea>? = null
    private var label: TextView? = null

            private fun initializeButtonsTextEditAndView() {
        val database = FirebaseDatabase.getInstance()

        val user: String = firebaseAuth?.currentUser?.displayName.toString()

        val mottoEdit: EditText = findViewById(R.id.EditTextMotto)
        val occupationEdit: EditText = findViewById(R.id.EditTextOccupation)
        val cityEdit: EditText = findViewById(R.id.EditTextCity)
        val city: TextView = findViewById(R.id.textViewCity)
        val motto: TextView = findViewById(R.id.textViewMotto)
        val occupation: TextView = findViewById(R.id.textViewOccupation)

        label = findViewById(R.id.textViewDisplayLabel)

        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        ideaArrayList = ArrayList()
        adapter = CardIdeaAdapter(this, ideaArrayList!!)
        recyclerView.adapter = adapter
        adapter!!.notifyDataSetChanged()

        //button to add idea to the profile
        val addIdea: Button = findViewById(R.id.buttonAddIdea)
        addIdea.setOnClickListener(this)
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

        motto.setOnLongClickListener(this)
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
                occupation.text = occupationEdit.text
                val myRef = database.getReference("users").child(user).child("occupation")
                myRef.setValue(occupationEdit.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        occupation.setOnLongClickListener(this)
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
        city.setOnLongClickListener(this)
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
        val avatar = findViewById<ImageView>(R.id.imageViewAvatar)
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

        /////TODO testing code
        //test search feature
        //getUser("username1");
        createData()
        /////TODO end of testing code
    }

    private fun createData() {
        val forks = ArrayList<String>()
        forks.add("2")
        val idea = Idea("Gino Malli", "android app", "un app per brainstoirming", "brainstorm", false, forks)
        ideaArrayList!!.add(idea)
        val idea2 = Idea("Gino Malli", "android app", "un app per brainstoirming", "brainstorm2", false, forks)
        ideaArrayList!!.add(idea2)
        val idea3 = Idea("Gino Malli", "android app", "un app per brainstoirming", "brainstorm2", false, forks)
        ideaArrayList!!.add(idea3)
        adapter!!.notifyDataSetChanged()
    }

    // Handle bottom menu item selection
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView)
        val settingsFragment = TeamFragment()
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
                label?.text = "Social"
                supportFragmentManager.popBackStack()
                recyclerView.alpha = 0f
                recyclerView.isEnabled = false
                val teamFragment = TeamFragment()
                supportFragmentManager.beginTransaction().add(R.id.frameLayout, teamFragment).addToBackStack(null).commit()
                true
            }
            R.id.idea_feeds -> {
                label?.text = "Ideas Feed"
                true
            }
            else -> false
        }
    }

    // Handle toolbar item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logoff -> {
                signOut(firebaseAuth)
                true
            }
            R.id.app_bar_search -> {
                // Get the intent, verify the action and get the query
                val intent = intent
                if (Intent.ACTION_SEARCH == intent.action) {
                    val query = intent.getStringExtra(SearchManager.QUERY)

                    getUser(query)
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Menu icons are inflated just as they were with actionbar
    // Inflate the menu; this adds items to the action bar if it is present.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
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

    //feature to get a users by username from the database
    private fun getUser(username: String?) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child(username!!)
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


    private fun signOut(auth: FirebaseAuth?) {
        auth!!.signOut()
    }

    override fun onClick(p0: View?) {}

    override fun onLongClick(p0: View?): Boolean {
        return false
    }
}