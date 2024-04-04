package com.example.bondoman

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.databinding.FragmentEditTransactionBinding
import com.example.bondoman.retrofit.adapter.TransactionAdapter
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.retrofit.data.entity.TransactionEntity

class EditTransactionFragment : Fragment() {
    private lateinit var binding : FragmentEditTransactionBinding
    private lateinit var db : TransactionDB
    private lateinit var adapter : TransactionAdapter
    private val listTransaction = ArrayList<TransactionEntity>()
    companion object {
        private const val ARG_TRANSACTION_ID = "id"

        fun newInstance(id: Int): EditTransactionFragment {
            val fragment = EditTransactionFragment()
            val args = Bundle()
            args.putInt(ARG_TRANSACTION_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt(ARG_TRANSACTION_ID) ?: -1
        db = TransactionDB.getInstance(requireContext())
        fetchTransactionDetails(id)

        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes") { _, _ ->
                    val transaction = db.transactionDao().getId(id)
                    db.transactionDao().delete(transaction)
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        binding.saveButton.setOnClickListener {
            db = TransactionDB.getInstance(requireContext())
            val kategori = db.transactionDao().getId(id).category
            val date = db.transactionDao().getId(id).date
            val nama = binding.nameField.text.toString()
            val nominal = binding.priceField.text.toString().toDouble()
            val lokasi = binding.locationField.text.toString()

            val newTransaction = TransactionEntity(
                id = id,
                name = nama,
                price = nominal,
                location = lokasi,
                category = kategori,
                date = date
            )

            db.transactionDao().update(newTransaction)

            requireActivity().onBackPressed()
        }
    }

    private fun fetchTransactionDetails(id: Int) {
        val transaction = db.transactionDao().getId(id)
        binding.nameField.setText(transaction.name)
        binding.priceField.setText(transaction.price.toString())
        binding.locationField.setText(transaction.location)
        binding.dateField.setText(transaction.date)
        binding.categoryField.setText(transaction.category.toString())
    }

}