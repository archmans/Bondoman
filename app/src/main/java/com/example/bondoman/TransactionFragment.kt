package com.example.bondoman
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.databinding.FragmentTransaksiBinding
import com.example.bondoman.retrofit.data.entity.TransactionEntity
import com.example.bondoman.retrofit.adapter.TransactionAdapter

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransaksiBinding
    private lateinit var transactionData: RecyclerView
    private val listTransaction = ArrayList<TransactionEntity>()
    private lateinit var searchBar: TextView
    lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionDB
//    private lateinit var dbTransaction: DbTransaction

//    private var receiver = TransactionRandomizerReceiver()

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

        database = TransactionDB.getInstance(requireContext())


        adapter = TransactionAdapter(listTransaction)

        transactionData = binding.itemTransaction
        transactionData.adapter = adapter
        transactionData.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        transactionData.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))


        binding.addButton.setOnClickListener {
            val intent = Intent(requireContext(), ContainerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the BroadcastReceiver when the fragment's view is destroyed
//        requireContext().unregisterReceiver(receiver)
        database.close()
    }

    override fun onResume() {
        super.onResume()
        loadDatas()
    }

    private fun loadDatas() {
        listTransaction.clear()

        listTransaction.addAll(database.transactionDao().getAll())
        adapter.notifyDataSetChanged()
    }
}