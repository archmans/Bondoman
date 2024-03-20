package com.example.bondoman.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.bondoman.LoginActivity
import com.example.bondoman.utils.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
class JWTExpiry : Service() {
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startJWTExpirationCheck()
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun startJWTExpirationCheck() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val token = getTokenFromSharedPreferences()
                try {
                    val response = RetrofitInstance.api.jwt("Bearer $token")
                    val timeToExpiry = response.exp - (System.currentTimeMillis() / 1000)
                    Log.d("JWTExpiry", "Hit API, time to expiry: $timeToExpiry")
                    delay( timeToExpiry * 1000)
                } catch (e: HttpException) {
                    Log.e("JWTExpiry", "HTTP Exception: ${e.message()}")
                    redirectToLogin()
                    break
                } catch (e: IOException) {
                    Log.e("JWTExpiry", "IO Exception: ${e.message}")
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