package com.example.bondoman

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bondoman.services.JWTExpiry
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.services.ConnectivityObserver
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.example.bondoman.services.NetworkSensing
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {
    private lateinit var service: Intent
    private lateinit var networkSensing: NetworkSensing
    private lateinit var transactionButton: ImageButton
    private lateinit var graphButton: ImageButton
    private lateinit var settingButton: ImageButton
    private lateinit var scanButton: ImageButton
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        service = Intent(this, JWTExpiry::class.java)
        startService(service)
        networkSensing = NetworkSensing(this)

        EventBus.getDefault().register(TransactionFragment())
        Log.d("EventBus", "Registered fragment as subscriber")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val toolbarButton = findViewById<ImageButton>(R.id.toolbar_back_button)
        navController = navHostFragment.navController
        val fragmentManager: FragmentManager = supportFragmentManager

        val navController = navHostFragment.navController

        transactionButton = findViewById(R.id.transaction_button)
        transactionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_transaction_selector))
        transactionButton.isSelected = true

        graphButton = findViewById(R.id.graph_button)
        graphButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_graph_selector))

        settingButton = findViewById(R.id.setting_button)
        settingButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.navbar_setting_selector))

        scanButton = findViewById(R.id.scan_button)

        toolbarButton.setOnClickListener {
            navController.navigateUp()
        }
        transactionButton.setOnClickListener {
            navigateTo(R.id.transaction_fragment)
        }

        graphButton.setOnClickListener {
            navigateTo(R.id.graph_fragment)
        }

        settingButton.setOnClickListener {
            navigateTo(R.id.setting_fragment)
        }

        scanButton.setOnClickListener {
            navigateTo(R.id.scan_fragment)
        }

        val viewTreeObserver = window.decorView.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            val screenHeight = window.decorView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                val navbar = findViewById<ConstraintLayout>(R.id.navigation_bar)
                navbar.visibility = View.GONE
            } else {
                val navbar = findViewById<ConstraintLayout>(R.id.navigation_bar)
                navbar.visibility = View.VISIBLE
            }
        }
        networkSensing.observe()
            .onEach { state ->
                when (state) {
                    ConnectivityObserver.NetworkState.CONNECTED -> {
                        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                    }
                    ConnectivityObserver.NetworkState.DISCONNECTED -> {
                        runOnUiThread {
                            AlertDialog.Builder(this)
                                .setTitle("Network Status")
                                .setMessage("Disconnected")
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigateTo(id: Int) {
        transactionButton.isSelected = (id == R.id.transaction_fragment)
        graphButton.isSelected = (id == R.id.graph_fragment)
        settingButton.isSelected = (id == R.id.setting_fragment)
        navController.navigate(id)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        EventBus.getDefault().unregister(AddTransactionFragment())

    }

}
