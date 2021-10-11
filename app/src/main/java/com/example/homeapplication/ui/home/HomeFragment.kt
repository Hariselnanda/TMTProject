package com.example.homeapplication.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.homeapplication.ui.atendence.AbsensiActivity
import com.example.homeapplication.ui.task.ListTaskActivity
import com.example.homeapplication.R
import com.example.homeapplication.ui.atendence.AbsenLokasiActivity
import com.example.homeapplication.ui.task.CreateTaskActivity
import com.example.homeapplication.utils.AppPreferences

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val REQUEST_LOCATION = 1
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
        val absensi : LinearLayout = root.findViewById(R.id.ll_absensi)
        val task : LinearLayout = root.findViewById(R.id.ll_task)
        val buatTask : LinearLayout = root.findViewById(R.id.ll_buat_task)
        val tvFullNama :TextView = root.findViewById(R.id.tv_full_nama)

        absensi.setOnClickListener {
            if (AppPreferences.absenStepTwo) {
                val i = Intent(root.context, AbsenLokasiActivity::class.java)
                startActivity(i)
            }else{
                val intent = Intent(root.context, AbsensiActivity::class.java)
                startActivity(intent)
            }

        }
        task.setOnClickListener {
            val intent = Intent(root.context, ListTaskActivity::class.java)
            startActivity(intent)
        }
        buatTask.setOnClickListener {
            val intent = Intent(root.context, CreateTaskActivity::class.java)
            startActivity(intent)
        }
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })
        tvFullNama.text = AppPreferences.namaLengkap

        if (ActivityCompat.checkSelfPermission(
                        root.context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        root.context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    root.context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION
            )
        }
        return root
    }

}