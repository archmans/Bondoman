    package com.example.bondoman.services

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.os.Handler
    import android.os.Looper
    import android.util.Log
    import android.widget.EditText
    import androidx.appcompat.app.AppCompatActivity
    import com.example.bondoman.LogoutFragment
    import com.example.bondoman.R

    class RandomizeTransaction : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val randomText = intent?.getStringExtra("transactionName")

            Log.d("BroadcastRandomizeTransaction", "Random text: $randomText")

//            val activity = context as? AppCompatActivity
//            activity?.runOnUiThread {
//                val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_container) as? LogoutFragment
//                fragment?.updateEditText(randomText)
//            }
        }
    }