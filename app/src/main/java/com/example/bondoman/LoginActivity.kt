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
import org.json.JSONException
import org.json.JSONObject

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
//            val email = "13521010@std.stei.itb.ac.id"
//            val password = "password_13521010"

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    Log.d("LoginActivity", "Email: $email")
                    Log.d("LoginActivity", "Password: $password")
                    val response = RetrofitInstance.api.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.apply {
                            putString("TOKEN", token)
                        }.apply()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
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