package com.example.homeapplication.ui.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.homeapplication.MainActivity
import com.example.homeapplication.R

class CrosscheckTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crosscheck_task)
        val btnSimpan : Button = findViewById(R.id.btn_crosscheck_task_simpan)
        val btnBack : ImageView = findViewById(R.id.tv_back)
        btnSimpan.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}