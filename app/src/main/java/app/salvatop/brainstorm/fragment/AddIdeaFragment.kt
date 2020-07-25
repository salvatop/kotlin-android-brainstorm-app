package app.salvatop.brainstorm.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import app.salvatop.brainstorm.MainActivity
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.model.Idea
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddIdeaFragment : Fragment(), View.OnClickListener {
    private var authorName: String = ""
    private var title: EditText? = null
    private var ideaContext: EditText? = null
    private var contents: EditText? = null
    private var forks: ArrayList<String>? = null
    private var save: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        authorName = firebaseAuth!!.currentUser?.displayName.toString()

        val view = inflater.inflate(R.layout.fragment_add_idea, container,false) as ViewGroup

        title = view.findViewById(R.id.editTextIdeaTitle)
        ideaContext = view.findViewById(R.id.editTextContext)
        contents = view.findViewById(R.id.editTextContents)
        forks?.add("")

        save = view.findViewById(R.id.buttonSaveIdea)
        save?.setOnClickListener(this)
        save?.setOnClickListener {
            val newIdea = Idea(authorName,ideaContext?.text.toString(),contents?.text.toString(),title?.text.toString(),true,forks)
            val titleText = title!!.text.toString()
            val myRef = database.getReference("users").child(authorName).child("ideas").child(titleText)

            myRef.setValue(newIdea)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again whenever data at this location is updated.
                    Log.d("ADD IDEA", "Value is: " + dataSnapshot.value)
                    val mainActivity = Intent(context, MainActivity::class.java)
                    startActivity(mainActivity)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("ADD IDEA", "Failed to read value.", error.toException())
                }
            }) }


        return view
    }

    override fun onClick(p0: View?) {}
}

