package app.salvatop.brainstorm.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.model.Idea
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber


class BookmarksFragment : Fragment() {

    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false) as ViewGroup

        val bookmarksArrayList: ArrayList<Idea> = ArrayList()
        val currentUser = arguments!!.getString("user") as String
        val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewBookmarks)
        recyclerView.layoutManager = LinearLayoutManager(fragmentContext)

        val adapter = CardIdeaAdapter(context!!, bookmarksArrayList)

        recyclerView.adapter = adapter

        //create local list of my bookmarks
        FirebaseDatabase.getInstance().reference.child("users").child(currentUser).child("bookmarks")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val idea: Idea? = snapshot.getValue(Idea::class.java)
                            if (idea!!.title != "none") {
                                bookmarksArrayList.add(idea)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

        Timber.d(bookmarksArrayList.size.toString())
        return view
    }
}