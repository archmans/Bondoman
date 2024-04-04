package com.example.bondoman

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        if (savedInstanceState == null) {
            // Check if intent contains transaction ID
            val transactionId = intent.getIntExtra("id", -1)
            if (transactionId != -1) {
//                 Start the EditTransactionFragment
                val fragment = EditTransactionFragment.newInstance(transactionId)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

            } else {
                // Start the AddTransactionFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddTransactionFragment())
                    .commit()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}