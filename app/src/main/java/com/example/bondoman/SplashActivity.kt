package com.example.bondoman

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.bondoman.services.JWTExpiry

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkJWTExpiration()
        }, 3000L)
    }

    private fun checkJWTExpiration() {
        val jwtExpiryIntent = Intent(this, JWTExpiry::class.java)
        jwtExpiryIntent.putExtra("source", "splash")
        startService(jwtExpiryIntent)
    }
}