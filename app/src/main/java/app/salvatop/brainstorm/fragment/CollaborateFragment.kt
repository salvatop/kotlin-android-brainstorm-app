package app.salvatop.brainstorm.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import app.salvatop.brainstorm.R
import app.salvatop.brainstorm.RegisterActivity
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class CollaborateFragment : Fragment() {

    var firebaseAuth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_collaborate, container,false) as ViewGroup

        firebaseAuth = FirebaseAuth.getInstance()

        return view
    }

}