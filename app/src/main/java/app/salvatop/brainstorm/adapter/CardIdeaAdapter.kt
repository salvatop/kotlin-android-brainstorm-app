package app.salvatop.brainstorm.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import app.salvatop.brainstorm.MainActivity
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter.IdeaHolder
import app.salvatop.brainstorm.model.Idea
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class CardIdeaAdapter(private val context: Context, private val ideas: ArrayList<Idea>) : RecyclerView.Adapter<IdeaHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): IdeaHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.idea_card_layout, parent, false)
        return IdeaHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaHolder, position: Int) {
        val idea = ideas[position]
        holder.setDetails(idea)
    }

    override fun getItemCount(): Int {
        return ideas.size
    }

    inner class IdeaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.ideaAuthor)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)
        private val ideaContext: TextView = itemView.findViewById(R.id.ideaContext)
        private val content: TextView = itemView.findViewById(R.id.ideaContents)
        private val forks: TextView = itemView.findViewById(R.id.ideaForks)
        private val cover: ImageView = itemView.findViewById(R.id.ideaCover)
        private val forksButton: Button = itemView.findViewById(R.id.buttonFork)
        private val bookmarkButton: Button = itemView.findViewById(R.id.buttonBookmark)


        private val expandBtn: Button = itemView.findViewById(R.id.buttonShowMore)
        private val expandableLayout: ConstraintLayout = itemView.findViewById(R.id.expandable)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun setDetails(idea: Idea) {
            author.text = idea.author
            title.text = idea.title
            content.text = idea.content
            ideaContext.text = idea.ideaContext
            Glide.with(context.applicationContext)
                    .load(R.drawable.idea)
                    .into(cover)
            var nbOfforks = idea.forks.size - 1
            forks.text = "forks[ " + nbOfforks.toString() + " ]"

            forksButton.setOnClickListener {
                val newIdea = idea
                var newName = ""
                val titleText = idea.author + idea.title + newName
                val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
                val database = FirebaseDatabase.getInstance()
                val currentUser = firebaseAuth!!.currentUser?.displayName.toString()
                val myRef = database.getReference("users").child(currentUser).child("ideas").child(titleText)

                myRef.setValue(newIdea)

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again whenever data at this location is updated.
                        Log.d("FORK IDEA", "Value is: " + dataSnapshot.value)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("ADD IDEA", "Failed to read value.", error.toException())
                    }
                })
            }

            bookmarkButton.setOnClickListener {

            }
        }




    init {
            expandBtn.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.VISIBLE
                    expandBtn.text = "COLLAPSE"
                } else {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.GONE
                    expandBtn.text = "EXPAND"
                }
            }

        }
    }

}