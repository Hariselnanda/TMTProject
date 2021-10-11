package com.example.homeapplication.ui.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.homeapplication.R

class CreateTaskPreventif : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task_preventif)


        val btnLanjut : Button = findViewById(R.id.btn_lanjut_preventif)
        val llNoTicket : LinearLayout = findViewById(R.id.ll_no_ticket)
        val tvTitle : TextView = findViewById(R.id.tv_tittle_task)
        btnLanjut.setOnClickListener {

            val intent = Intent(this, CrosscheckTaskActivity::class.java)
            startActivity(intent)
        }

        val ss:Int = intent.getIntExtra("spinnerID",0)
        Log.e("ss",ss.toString())

        if (ss == 1){
            llNoTicket.visibility = View.VISIBLE
            tvTitle.text = "Create Task Trouble Ticket"
        }else{
            llNoTicket.visibility = View.GONE
            tvTitle.text = "Create Task Preventif"
        }
    }
}