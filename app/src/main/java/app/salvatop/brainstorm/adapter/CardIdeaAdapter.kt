package app.salvatop.brainstorm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.adapter.CardIdeaAdapter.IdeaHolder
import app.salvatop.brainstorm.model.Idea
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.set

class CardIdeaAdapter(private val context: Context, private val data: ArrayList<Idea>) : RecyclerView.Adapter<IdeaHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): IdeaHolder {
        return ideaHolder(this, parent)
    }

    override fun onBindViewHolder(holder: IdeaHolder, position: Int) {
        val idea = data[position]
        holder.setContents(idea)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    //region *** VIEW HOLDER ***
    inner class IdeaHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
        private val database = FirebaseDatabase.getInstance()
        private val author: TextView = itemView.findViewById(R.id.ideaAuthor)
        private val title: TextView = itemView.findViewById(R.id.textViewTitle)
        private val ideaContext: EditText = itemView.findViewById(R.id.ideaContext)
        private val content: EditText = itemView.findViewById(R.id.ideaContents)
        private val forks: TextView = itemView.findViewById(R.id.ideaForks)
        private val cover: ImageView = itemView.findViewById(R.id.ideaCover)
        private val forksButton: Button = itemView.findViewById(R.id.buttonFork)
        private val bookmarkButton: Button = itemView.findViewById(R.id.buttonBookmark)

        private val expandBtn: Button = itemView.findViewById(R.id.buttonShowMore)
        private val expandableLayout: ConstraintLayout = itemView.findViewById(R.id.expandable)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {
            expandBtn.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.VISIBLE
                    val collapse: String = it.resources.getString(R.string.collapse)
                    expandBtn.text = collapse
                } else {
                    cardView.let { it1 -> TransitionManager.beginDelayedTransition(it1, AutoTransition()) }
                    expandableLayout.visibility = View.GONE
                    val expand: String = it.resources.getString(R.string.expand)
                    expandBtn.text = expand
                }
            }

        }

        @SuppressLint("ResourceAsColor")
        fun setContents(idea: Idea) {
            val currentUser = firebaseAuth?.currentUser?.displayName.toString()
            author.text = idea.author
            title.text = idea.title
            content.setText(idea.content)
            ideaContext.setText(idea.ideaContext)
            Glide.with(context.applicationContext)
                    .load(R.drawable.idea)
                    .into(cover)

            val fromOrBy: String
            val getFork = getForks(idea)
            fromOrBy = if (getFork.startsWith(currentUser)) {
                "by"
            } else {
                "from"
            }
            val forked = "forked " + fromOrBy + ": [ " + getForks(idea) + " ]"
            forks.text = forked

            content.setTextColor(R.color.disabled_edit_text)
            ideaContext.setTextColor(R.color.disabled_edit_text)

            if (currentUser != idea.author) {
                ideaContext.isEnabled = false
                content.isEnabled = false
            } else {
                cardView.setOnLongClickListener {
                    Timber.d("long click")
                    if (currentUser == idea.author) {
                        val alertBox = AlertDialog.Builder(it.rootView.context)
                        alertBox.setMessage("Do you want remove for this idea?")
                        alertBox.setTitle("Delete Idea")
                        alertBox.setPositiveButton("DELETE") { _, _ ->
                            val ideaToDelete = database.getReference("users").child(currentUser).child("ideas")
                            ideaToDelete.child(idea.title).removeValue()
                        }
                        alertBox.setNegativeButton("CANCEL") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                        alertBox.show()
                    }
                    return@setOnLongClickListener true
                }
            }

            //region *** FORK ACTIONS ***
            forksButton.setOnClickListener {
                val alertBox = AlertDialog.Builder(it.rootView.context)
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.hint = "add a new title"

                alertBox.setView(input)
                alertBox.setMessage("Do you want fork this idea?")
                alertBox.setTitle("Fork Idea")
                alertBox.setPositiveButton("FORK") { _ , _ ->
                    val forksToAdd: HashMap<String, String> = HashMap()
                    forksToAdd[idea.author] = idea.title

                    val newTitle = input.text.toString()
                    Timber.i(newTitle)
                    val ideaToFork = Idea(currentUser, idea.ideaContext, idea.content, newTitle, "true", forksToAdd)

                    val currentUserForkIdea = database.getReference("users").child(currentUser).child("ideas").child(newTitle)

                    currentUserForkIdea.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            currentUserForkIdea.setValue(ideaToFork)
                            Timber.d("Value is: %s", dataSnapshot.value)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Timber.tag("FORK IDEA").w(error.toException(), "Failed to read value.")
                        }
                    })
                    //add a reference to the to idea of the new fork
                    val originalAuthor = database.getReference("users").child(idea.author).child("ideas").child(idea.title).child("forks")
                    originalAuthor.child(currentUser).setValue(newTitle)
                }
                alertBox.setNegativeButton("CANCEL") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                alertBox.show()
            }
            //endregion

            //region *** BOOKMARK ACTIONS ***
            bookmarkButton.setOnClickListener {
                val currentUserBookmark = firebaseAuth?.currentUser?.displayName.toString()
                val exist = isBookmarked(idea)
                // Start a coroutine in the UI (Main thread)
                GlobalScope.launch {
                    val handler = Handler(Looper.getMainLooper())
                    delay(1000)
                    handler.post {
                       Timber.d(exist.toString())
                        if (!exist) {
                            val myRef = database.getReference("users").child(currentUserBookmark).child("bookmarks").child(idea.title)
                            myRef.setValue(idea)
                            Toast.makeText(context, "The idea was added to your bookmarks", Toast.LENGTH_SHORT).show()
                        } else {
                            val myRef = database.getReference("users").child(currentUserBookmark).child("bookmarks")
                            myRef.child(idea.title).removeValue()
                            Toast.makeText(context, "The was removed from to your bookmarks", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            //endregion
        }

        private fun isBookmarked(idea: Idea): Boolean {
            var exist = false
            val currentUser = firebaseAuth!!.currentUser?.displayName.toString()
            val myRef = database.getReference("users").child(currentUser).child("bookmarks")

            myRef.addListenerForSingleValueEvent(object :ValueEventListener {
                @Override
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(idea.title)) {
                        Timber.d(snapshot.hasChild(idea.title).toString())
                        exist = true
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Timber.tag("BOOKMARK").w(error.toException(), "Failed to read value.")
                }
            })
            return exist
        }

        private fun getForks(idea: Idea) : String {
            var forksToReturn = ""

            for (fork in idea.forks) {
                if (fork.value != "none"){
                    forksToReturn = fork.key + "-" + fork.value
                }
            }
            return forksToReturn
        }
    }
        //inflate the layout for ideaHolder
        companion object {
            private fun ideaHolder(cardIdeaAdapter: CardIdeaAdapter, parent: ViewGroup): IdeaHolder {
                val view = LayoutInflater.from(cardIdeaAdapter.context).inflate(R.layout.idea_card_layout, parent, false)
                return cardIdeaAdapter.IdeaHolder(view)
        }
    }
    //endregion
}