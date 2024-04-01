package com.example.bondoman
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.databinding.FragmentTransaksiBinding
import com.example.bondoman.retrofit.data.Transaction
import com.example.bondoman.helper.Xls
import com.example.bondoman.models.SqlTransaction
import com.example.bondoman.sql.Kontraktor
import com.example.bondoman.sql.TransactionSQL

class TransactionFragment : Fragment() {

    private lateinit var binding : FragmentTransaksiBinding
    private lateinit var transactionData : RecyclerView
    private val listTransaction= ArrayList<Transaction>()
    private lateinit var searchBar: TextView
    private lateinit var dbTransaction: TransactionSQL
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbTransaction = TransactionSQL(requireContext())
        dbTransaction.open()

//        // Delete all data
//        dbTransaction.deleteAll()
//
//        // Dummy data insertion
//        insertDummyTransactions()

        transactionData = binding.itemTransaction
        transactionData.setHasFixedSize(true)
        transactionData.layoutManager = LinearLayoutManager(requireContext())
        loadDatas(view)
//        listTransaction.add(Transaction("Semen Tiga Roda","22 Maret 2024", "Pengeluaran", 200000, "Jakarta"))
//        listTransaction.add(Transaction("Semen Tiga Roda","22 Maret 2024", "Pengeluaran", 300000, "Jakarta"))
//        listTransaction.add(Transaction("Semen Tiga Roda","22 Maret 2024", "Pengeluaran", 300000, "Jakarta"))
//        listTransaction.add(Transaction("Mapel Sirup","22 Maret 2024", "Pengeluaran", 100000, "Jakarta"))
//        listTransaction.add(Transaction("Jeruks","22 Maret 2024", "Pengeluaran", 200000, "Jakarta"))
//        listTransaction.add(Transaction("Apel","22 Maret 2024", "Pengeluaran", 300000, "Jakarta"))


        val filteredList: ArrayList<Transaction> = ArrayList(listTransaction)
        var transactionAdapter = TransactionAdapter(filteredList)
        transactionData.adapter = transactionAdapter

        searchBar = binding.searchBar

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
                        transaction.name != null && transaction.name.contains(searchText, ignoreCase = true)
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

    private fun insertDummyTransactions() {
        val dummyTransaction1 = ContentValues().apply {
            put(Kontraktor.TransactionTable.NAME, "Transaction 1")
            put(Kontraktor.TransactionTable.CATEGORY, "Category 1")
            put(Kontraktor.TransactionTable.DATE, "2024-04-01")
            put(Kontraktor.TransactionTable.PRICE, 100)
            put(Kontraktor.TransactionTable.LOCATION, "Location 1")
        }

        val dummyTransaction2 = ContentValues().apply {
            put(Kontraktor.TransactionTable.NAME, "Transaction 2")
            put(Kontraktor.TransactionTable.CATEGORY, "Category 2")
            put(Kontraktor.TransactionTable.DATE, "2024-04-02")
            put(Kontraktor.TransactionTable.PRICE, 200)
            put(Kontraktor.TransactionTable.LOCATION, "Location 2")
        }

        val dummyTransaction3 = ContentValues().apply {
            put(Kontraktor.TransactionTable.NAME, "Transaction 3")
            put(Kontraktor.TransactionTable.CATEGORY, "Category 3")
            put(Kontraktor.TransactionTable.DATE, "2024-04-03")
            put(Kontraktor.TransactionTable.PRICE, 300)
            put(Kontraktor.TransactionTable.LOCATION, "Location 3")
        }

        val dummyTransaction4 = ContentValues().apply {
            put(Kontraktor.TransactionTable.NAME, "Transaction 4")
            put(Kontraktor.TransactionTable.CATEGORY, "Category 4")
            put(Kontraktor.TransactionTable.DATE, "2024-04-04")
            put(Kontraktor.TransactionTable.PRICE, 400)
            put(Kontraktor.TransactionTable.LOCATION, "Location 4")
        }

        val dummyTransaction5 = ContentValues().apply {
            put(Kontraktor.TransactionTable.NAME, "Transaction 5")
            put(Kontraktor.TransactionTable.CATEGORY, "Category 5")
            put(Kontraktor.TransactionTable.DATE, "2024-04-05")
            put(Kontraktor.TransactionTable.PRICE, 500)
            put(Kontraktor.TransactionTable.LOCATION, "Location 5")
        }

        // Insert dummy transactions into the database
        dbTransaction.insert(dummyTransaction1)
        dbTransaction.insert(dummyTransaction2)
        dbTransaction.insert(dummyTransaction3)
        dbTransaction.insert(dummyTransaction4)
        dbTransaction.insert(dummyTransaction5)

    }

    fun loadDatas(view: View) {

        insertDummyTransactions()
        val datas = dbTransaction.findAll()

        datas.forEach {
                item ->
            listTransaction.add(Transaction(item.id,item.name,item.date,item.category, item.price,item.location))
        }
    }
}