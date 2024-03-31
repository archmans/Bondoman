package com.example.bondoman.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.bondoman.LoginActivity
import com.example.bondoman.TransactionActivity
import com.example.bondoman.utils.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
class JWTExpiry : Service() {
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val source = intent?.getStringExtra("source") ?: "main"
        startJWTExpirationCheck(source)
        return START_STICKY
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun startJWTExpirationCheck(source: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val token = getTokenFromSharedPreferences()
                try {
                    val response = RetrofitInstance.api.jwt("Bearer $token")
                    if (response.isSuccessful) {
                        val exp = response.body()?.exp
                        val currTime = System.currentTimeMillis() / 1000
                        val timeToExpiry = exp?.minus(currTime) ?: 0
                        Log.d("JWTExpiry", "Hit API, time to expiry: $timeToExpiry")
                        val delay = timeToExpiry * 1000
                        if (source == "splash") {
                            redirectToTransaction()
                            break
                        } else if (source == "main") {
                            delay(delay)
                        }
                    } else {
                        Log.e("JWTExpiry", "JWT API call failed")
                        if (response.code() == 401) {
                            val errorMessage = response.errorBody()?.string()
                            Log.e("JWTExpiry", "Error Message: $errorMessage")
                        }
                        redirectToLogin()
                        break
                    }
                } catch (e: HttpException) {
                    Log.e("JWTExpiry", "HTTP Exception: ${e.message()}")
                    redirectToLogin()
                    break
                } catch (e: Throwable) {
                    Log.e("JWTExpiry", "Error: ${e.message}")
                    redirectToLogin()
                    break
                }
            }
        }
    }

//    private fun redirectToMain() {
//        val mainIntent = Intent(this, MainActivity::class.java)
//        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(mainIntent)
//    }

    private fun redirectToTransaction() {
        val transactionIntent = Intent(this, TransactionActivity::class.java)
        transactionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(transactionIntent)
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", "") ?: ""
    }

    private fun redirectToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }
}