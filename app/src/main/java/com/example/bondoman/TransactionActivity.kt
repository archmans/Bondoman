package com.example.bondoman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.databinding.ActivityTransactionBinding
import com.example.bondoman.retrofit.data.Transaction

class TransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionBinding
    private lateinit var transactionData : RecyclerView
    private val listTransaction= ArrayList<Transaction>()
    private lateinit var searchBar: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionData = findViewById(R.id.itemTransaction)
        transactionData.setHasFixedSize(true)
        transactionData.layoutManager = LinearLayoutManager(this)

        listTransaction.add(
            Transaction(
                "Semen Tiga Roda",
                "22 Maret 2024",
                "Pengeluaran",
                200000,
                "Jakarta"
            )
        )
        listTransaction.add(
            Transaction(
                "Semen Tiga Roda",
                "22 Maret 2024",
                "Pengeluaran",
                300000,
                "Jakarta"
            )
        )
        listTransaction.add(
            Transaction(
                "Semen Tiga Roda",
                "22 Maret 2024",
                "Pengeluaran",
                300000,
                "Jakarta"
            )
        )
        listTransaction.add(
            Transaction(
                "Mapel Sirup",
                "22 Maret 2024",
                "Pengeluaran",
                100000,
                "Jakarta"
            )
        )
        listTransaction.add(
            Transaction(
                "Jeruks",
                "22 Maret 2024",
                "Pengeluaran",
                200000,
                "Jakarta"
            )
        )
        listTransaction.add(Transaction("Apel", "22 Maret 2024", "Pengeluaran", 300000, "Jakarta"))
        val filteredList: ArrayList<Transaction> = ArrayList(listTransaction)
        var transactionAdapter = TransactionAdapter(filteredList)
        transactionData.adapter = transactionAdapter

        searchBar = findViewById(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the filteredList based on the search text
                val searchText = s.toString().trim()
                filteredList.clear()

                if (searchText.isEmpty()) {
                    // If search text is empty, show all items
                    filteredList.addAll(listTransaction)
                } else {
                    // Filter the list based on the search text
                    filteredList.addAll(listTransaction.filter { transaction ->
                        transaction.name != null && transaction.name.contains(
                            searchText,
                            ignoreCase = true
                        )
                    })
                }

                // Notify the adapter about the updated list
                transactionAdapter = TransactionAdapter(filteredList)
                transactionData.adapter = transactionAdapter
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })
    }
}