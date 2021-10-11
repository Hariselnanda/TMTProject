package com.example.homeapplication.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.homeapplication.MainActivity
import com.example.homeapplication.R
import com.example.homeapplication.ui.login.LoginActivity
import com.example.homeapplication.utils.AppPreferences

class ProfileFragment : Fragment() {

    private lateinit var notificationsViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnLogout : Button = root.findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            (activity as MainActivity).showDialog()
        }

        return root
    }


}