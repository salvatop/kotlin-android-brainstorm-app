package app.salvatop.brainstorm.adapter

import android.content.Context
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
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter.IdeaHolder
import app.salvatop.brainstorm.model.Idea
import com.bumptech.glide.Glide
import java.util.*

internal class CardIdeaAdapter(private val context: Context, private val ideas: ArrayList<Idea>) : RecyclerView.Adapter<IdeaHolder>() {
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

    internal inner class IdeaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val author: TextView = itemView.findViewById(R.id.ideaAuthor)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)
        private val ideaContext: TextView = itemView.findViewById(R.id.ideaContext)
        private val content: TextView = itemView.findViewById(R.id.ideaContents)
        private val forks: TextView = itemView.findViewById(R.id.ideaForks)
        private val cover: ImageView = itemView.findViewById(R.id.ideaCover)

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
            //idea.forks?.size?.let { forks.setText(it) }
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