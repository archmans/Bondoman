package com.example.bondoman
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bondoman.databinding.FragmentSettingBinding

class SettingFragment: Fragment() {
    private lateinit var binding : FragmentSettingBinding
//    private lateinit var sendEmailButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navController = findNavController()
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_setting, container, false)
//        sendEmailButton = view.findViewById(R.id.sendEmailButton)
//
//        // Set OnClickListener
//        sendEmailButton.setOnClickListener {
//            sendEmail()
//        }
//
//        return view
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendEmailButton.setOnClickListener {
            sendEmail()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun sendEmail() {
        // Create an intent to send an email
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822" // Email MIME type

        // Fill in the email details (optional)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("bintanghijriawan433@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the email")

        try {
            // Start the email activity
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: ActivityNotFoundException) {
            // Handle errors if no email client is installed
            Toast.makeText(activity, "No email app found", Toast.LENGTH_SHORT).show()
        }
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
}