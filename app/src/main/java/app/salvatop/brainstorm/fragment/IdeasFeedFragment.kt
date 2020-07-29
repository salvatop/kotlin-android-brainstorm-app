package app.salvatop.brainstorm.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter
import app.salvatop.brainstorm.model.Idea


class IdeasFeedFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ideas_feed, container,false) as ViewGroup

        var allUsersIdeasArrayList: ArrayList<Idea>
        allUsersIdeasArrayList = arguments!!.getSerializable("ideas") as ArrayList<Idea>

        Log.d("DATA ENCAPSULATION", allUsersIdeasArrayList.size.toString())

        recyclerView  = view.findViewById(R.id.recycleViewIdeasFeed)
        recyclerView!!.layoutManager = LinearLayoutManager(fragmentContext)

        val adapter = CardIdeaAdapter(context!!,allUsersIdeasArrayList)

        recyclerView!!.adapter = adapter

        adapter.notifyDataSetChanged()
        return view
    }
}