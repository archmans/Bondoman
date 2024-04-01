package com.example.bondoman
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.content.Intent
import android.widget.EditText
import com.example.bondoman.services.RandomizeTransaction

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logout, container, false)

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            logout()
        }

        view.findViewById<Button>(R.id.randomButton).setOnClickListener {
            val listRandomText = listOf("MacBook Pro", "MacBook Air", "Mac Mini", "Mac Pro", "iMac")
            val randomText = listRandomText.random()

            val randomizeTransactionIntent = Intent(requireContext(), RandomizeTransaction::class.java)
            randomizeTransactionIntent.putExtra("transactionName", randomText)
            randomizeTransactionIntent.setAction("com.example.bondoman.services.RandomizeTransaction")
            requireContext().sendBroadcast(randomizeTransactionIntent)
        }
        return view
    }

    private fun logout() {
        clearUserSession()
        val loginIntent = Intent(requireContext(), LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }

    private fun clearUserSession() {
        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun updateEditText(randomText: String?) {
        view?.findViewById<EditText>(R.id.transactionName)?.setText(randomText)
    }
}