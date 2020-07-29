package app.salvatop.brainstorm.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.model.Idea


class BookmarksFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container,false) as ViewGroup

        var bookmarksArrayList: ArrayList<Idea>
        bookmarksArrayList = arguments!!.getSerializable("bookmarks") as ArrayList<Idea>

        Log.d("DATA ENCAPSULATION", bookmarksArrayList.size.toString())

        recyclerView  = view.findViewById(R.id.recycleViewBookmarks)
        recyclerView!!.layoutManager = LinearLayoutManager(fragmentContext)

        val adapter = CardIdeaAdapter(context!!,bookmarksArrayList)

        recyclerView!!.adapter = adapter

        adapter.notifyDataSetChanged()
        return view
    }

    

}