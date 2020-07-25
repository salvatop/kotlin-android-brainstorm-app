package app.salvatop.brainstorm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        private val author: TextView
        private val title: TextView
        private val ideaContext: TextView
        private val content: TextView
        private val forks: TextView
        private val cover: ImageView
        fun setDetails(idea: Idea) {
            author.text = idea.author
            title.text = idea.title
            content.text = idea.content
            ideaContext.text = idea.ideaContext
            Glide.with(context.applicationContext)
                    .load(R.drawable.idea)
                    .into(cover)
            //forks.setText(idea.getForks().size());
        }

        init {
            author = itemView.findViewById(R.id.ideaAuthor)
            ideaContext = itemView.findViewById(R.id.ideaContext)
            content = itemView.findViewById(R.id.ideaContents)
            title = itemView.findViewById(R.id.ideaTitle)
            forks = itemView.findViewById(R.id.ideaForks)
            cover = itemView.findViewById(R.id.ideaCover)
        }
    }

}