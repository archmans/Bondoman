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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.DelicateCoroutinesApi
import com.example.bondoman.models.LoginRequest
import com.example.bondoman.utils.RetrofitInstance

class LoginActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val editTextEmail = findViewById<EditText>(R.id.emailAddress)
        val editTextPassword = findViewById<EditText>(R.id.password)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    Log.d("LoginActivity", "Email: $email")
                    Log.d("LoginActivity", "Password: $password")
                    val response = RetrofitInstance.api.login(LoginRequest(email, password))
                    val token = response.token
                    val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putString("TOKEN", token)
                    }.apply()
                    // print all shared preferences
                    val all: Map<String, *> = sharedPreferences.all
                    for ((key, value) in all) {
                        Log.d("LoginActivity", "$key: $value")
                    }
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
            }   catch (e: Exception) {
                    Log.e("LoginActivity", "An error occurred: ${e.message}", e)
                    Toast.makeText(this@LoginActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}