package com.example.homeapplication.ui.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.homeapplication.R

class CreateTaskActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var languages = arrayOf("Preventif", "Non Preventif")
    var SpinnerID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val btnLanjut : Button = findViewById(R.id.btn_lanjut)
        val spTypeTask : Spinner = findViewById(R.id.sp_type_task)
        btnLanjut.setOnClickListener {

            val intent = Intent(this, CreateTaskPreventif::class.java)
            intent.putExtra("spinnerID",SpinnerID)
            startActivity(intent)
        }
        if (spTypeTask != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, languages)
            spTypeTask.adapter = adapter

            spTypeTask.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
//                    Toast.makeText(this@CreateTaskActivity,
//                        getString(R.string.selected_item) + " " +
//                                "" + languages[position], Toast.LENGTH_SHORT).show()
                    SpinnerID = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}