package com.example.bondoman
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bondoman.databinding.FragmentSettingBinding
import com.example.bondoman.helper.Xls
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.services.RandomizeTransaction
import kotlin.random.Random

class SettingFragment: Fragment() {
    private lateinit var binding : FragmentSettingBinding
    private lateinit var database: TransactionDB
    private var savedFilePath: String? = null
//    private lateinit var sendEmailButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navController = findNavController()
        database = TransactionDB.getInstance(requireContext())
        val navbar = requireActivity().findViewById<LinearLayout>(R.id.navbar_main)
        val toolbar = requireActivity().findViewById<RelativeLayout>(R.id.toolbar)
        navbar.setBackgroundResource(R.drawable.navbar_background)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.broken_white
            )
        )
        val textView = toolbar.findViewById<TextView>(R.id.toolbar_text)
        val transactionButton = requireActivity().findViewById<ImageButton>(R.id.transaction_button)
        val graphButton = requireActivity().findViewById<ImageButton>(R.id.graph_button)
        val settingButton = requireActivity().findViewById<ImageButton>(R.id.setting_button)
        textView.text = "Pengaturan"
        textView.setTextColor(Color.BLACK)
        toolbar.findViewById<ImageButton>(R.id.toolbar_back_button).setImageResource(R.drawable.ic_arrow_left_black)
        transactionButton.isSelected = false
        graphButton.isSelected = false
        settingButton.isSelected = true
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener{
            saveToXls()
        }
        binding.sendEmailButton.setOnClickListener {
            sendEmail()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.randomizeButton.setOnClickListener {
            val listRandomText = listOf("MacBook Pro", "MacBook Air", "Mac Mini", "Mac Pro", "iMac")
            val randomText = listRandomText.random()
            val randomPrice: Double = String.format("%.2f", Random.nextDouble(0.0, 500.0)).toDouble()
            val randomizeTransactionIntent = Intent(requireContext(), RandomizeTransaction::class.java)
            randomizeTransactionIntent.putExtra("transactionName", randomText)
            randomizeTransactionIntent.putExtra("price", randomPrice)
            randomizeTransactionIntent.setAction("com.example.bondoman.services.RandomizeTransaction")
            requireContext().sendBroadcast(randomizeTransactionIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
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

    private fun saveToXls() {
        val items = arrayOf("xlsx", "xls")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose file format")
            .setItems(items) { dialog, which ->
                val transactions = database.transactionDao().getAll()
                val internalStorageDir = requireContext().getFilesDir()
                savedFilePath = Xls.saveXls(requireContext(), transactions, items[which], internalStorageDir)
                if (savedFilePath != null) {
                    Toast.makeText(requireContext(), "File saved to $savedFilePath", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save file", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

}