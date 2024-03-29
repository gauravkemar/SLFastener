package com.example.slfastener.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.demorfidapp.helper.Constants.KEY_ISLOGGEDIN
import com.example.demorfidapp.helper.Utils
import com.example.slfastener.R


class SplashActivity : AppCompatActivity() {
    private val SPLASH_SCREEN_TIME_OUT = 1000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        Handler().postDelayed({
            if (Utils.getSharedPrefsBoolean(this@SplashActivity, KEY_ISLOGGEDIN, false)) {
                if (Utils.getSharedPrefsBoolean(this@SplashActivity, KEY_ISLOGGEDIN, true)) {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, SPLASH_SCREEN_TIME_OUT)

    }

}
