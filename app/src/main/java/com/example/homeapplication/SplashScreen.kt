package com.example.homeapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.homeapplication.ui.login.LoginActivity
import com.example.homeapplication.utils.AppPreferences


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        AppPreferences.init(this)
        Handler().postDelayed(Runnable { //This method will be executed once the timer is over
            // Start your app main activity
            if (AppPreferences.belumLogin){
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
            }else{
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }

            // close this activity
            finish()
        }, 3000)
    }
}