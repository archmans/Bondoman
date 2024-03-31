package com.example.bondoman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.bondoman.services.JWTExpiry
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var service: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        service = Intent(this, JWTExpiry::class.java)
        startService(service)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, LogoutFragment())
//            .commit()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        val transactionButton = findViewById<ImageButton>(R.id.transaction_button)
        transactionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_transaction_selector))
        transactionButton.isSelected = true

        val graphButton = findViewById<ImageButton>(R.id.graph_button)
        graphButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_graph_selector))

        val settingButton = findViewById<ImageButton>(R.id.setting_button)
        settingButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_setting_selector))

        val scanButton = findViewById<ImageButton>(R.id.scan_button)

        transactionButton.setOnClickListener {
            transactionButton.isSelected = true
            graphButton.isSelected = false
            settingButton.isSelected = false
            navController.navigate(R.id.transaction_fragment)
        }

        graphButton.setOnClickListener {
            transactionButton.isSelected = false
            graphButton.isSelected = true
            settingButton.isSelected = false
            navController.navigate(R.id.graph_fragment)
        }

        settingButton.setOnClickListener {
            transactionButton.isSelected = false
            graphButton.isSelected = false
            settingButton.isSelected = true
            navController.navigate(R.id.setting_fragment)
        }

        scanButton.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }
}
