package com.example.bondoman
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bondoman.databinding.FragmentSettingBinding
import com.example.bondoman.helper.Xls
import com.example.bondoman.models.SqlTransaction
import com.example.bondoman.sql.TransactionSQL

class SettingFragment: Fragment() {
    private lateinit var binding : FragmentSettingBinding
    private var savedFilePath: String? = null
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
        binding.saveButton.setOnClickListener{

            // Retrieve transactions from the database
            val dbTransaction = TransactionSQL(requireContext())
            dbTransaction.open()
            val listTransaction = dbTransaction.findAll()

            if (listTransaction.isNotEmpty()) {
                // If there are transactions, show the format selection dialog
                showFormatSelectionDialog(listTransaction)
            } else {
                // Handle case when there are no transactions in the database
                Toast.makeText(requireContext(), "No transactions found in the database", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sendEmailButton.setOnClickListener {
            sendEmail()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.randomizeButton.setOnClickListener {
            sendEmail()
        }
    }

    private fun showFormatSelectionDialog(listTransaction: List<SqlTransaction>) {
        val formatOptions = arrayOf("XLS", "XLSX")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select File Format")
            .setItems(formatOptions) { dialog, which ->
                val fileFormat = if (which == 0) "xls" else "xlsx"
                savedFilePath = Xls.saveXls(requireContext(), listTransaction, fileFormat)
                showMassage("File has been saved successfully")
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showMassage(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                // Do something when OK button is clicked
                dialog.dismiss()
            }
            .create()
            .show()
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