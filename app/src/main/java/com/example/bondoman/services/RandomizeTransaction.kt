    package com.example.bondoman.services

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.util.Log
    import org.greenrobot.eventbus.EventBus

    class RandomizeTransaction : BroadcastReceiver() {
        private var transactionName: String = ""
        private var price: Double = 0.0
        override fun onReceive(context: Context?, intent: Intent?) {
            transactionName  = intent?.getStringExtra("transactionName") ?: ""
            price = intent?.getDoubleExtra("price", 0.0 ) ?: 0.0
            Log.d("BroadcastRandomizeTransaction", "Random text: $transactionName $price")
            EventBus.getDefault().post(RandomTransactionEvent(transactionName, price))
        }
    }