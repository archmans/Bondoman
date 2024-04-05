package com.example.bondoman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.bondoman.models.LoginRequest
import com.example.bondoman.services.ConnectivityObserver
import com.example.bondoman.services.NetworkSensing
import com.example.bondoman.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var networkSensing: NetworkSensing
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val editTextEmail = findViewById<EditText>(R.id.emailAddress)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val connectionLost = findViewById<RelativeLayout>(R.id.conn_lost)
        networkSensing = NetworkSensing(this)
        networkSensing.observe()
            .onEach { state ->
                when (state) {
                    ConnectivityObserver.NetworkState.CONNECTED -> {
                        editTextEmail.visibility = View.VISIBLE
                        editTextPassword.visibility = View.VISIBLE
                        buttonLogin.visibility = View.VISIBLE
                        connectionLost.visibility = View.GONE
                    }
                    ConnectivityObserver.NetworkState.DISCONNECTED -> {
                        editTextEmail.visibility = View.GONE
                        editTextPassword.visibility = View.GONE
                        buttonLogin.visibility = View.GONE
                        connectionLost.visibility = View.VISIBLE
                    }
                }
            }
            .launchIn(lifecycleScope)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
//            val email = "13521003@std.stei.itb.ac.id"
//            val password = "password_13521003"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.apply {
                            putString("TOKEN", token)
                            putString("EMAIL", email)
                        }.apply()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val responseError = response.errorBody()?.string()
                        responseError?.let {
                            try {
                                val jsonObject = JSONObject(it)
                                val issuesArray = jsonObject.getJSONArray("issues")
                                if (issuesArray.length() > 0) {
                                    val errorMessage = issuesArray.getJSONObject(0).getString("message")
                                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }   catch (e: Exception) {
                    Log.e("LoginActivity", "Error: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}