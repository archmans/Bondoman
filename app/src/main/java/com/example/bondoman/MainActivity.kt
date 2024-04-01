package com.example.bondoman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bondoman.services.JWTExpiry

class MainActivity : AppCompatActivity() {
    private lateinit var service: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        service = Intent(this, JWTExpiry::class.java)
        startService(service)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LogoutFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }
}
