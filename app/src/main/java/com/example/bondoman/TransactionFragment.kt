package com.example.bondoman
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.databinding.FragmentTransaksiBinding
import com.example.bondoman.retrofit.data.entity.TransactionEntity
import com.example.bondoman.retrofit.adapter.TransactionAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

var pendingTransaction: MutableList<Pair<String, Double>> = mutableListOf()

class TransactionFragment : Fragment() {
    private lateinit var binding: FragmentTransaksiBinding
    private lateinit var transactionData: RecyclerView
    private val listTransaction = ArrayList<TransactionEntity>()
    private lateinit var searchBar: TextView
    lateinit var adapter: TransactionAdapter

    private lateinit var database: TransactionDB
    private lateinit var db: DBViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = TransactionDB.getInstance(requireContext())
        binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val navbar = requireActivity().findViewById<LinearLayout>(R.id.navbar_main)
        val toolbar = requireActivity().findViewById<RelativeLayout>(R.id.toolbar)
        navbar.setBackgroundResource(R.drawable.navbar_background)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        val textView = toolbar.findViewById<TextView>(R.id.toolbar_text)
        val transactionButton = requireActivity().findViewById<ImageButton>(R.id.transaction_button)
        val graphButton = requireActivity().findViewById<ImageButton>(R.id.graph_button)
        val settingButton = requireActivity().findViewById<ImageButton>(R.id.setting_button)
        textView.text = "Transaksi"
        textView.setTextColor(Color.WHITE)
        toolbar.findViewById<ImageButton>(R.id.toolbar_back_button)
            .setImageResource(R.drawable.ic_arrow_left_white)
        transactionButton.isSelected = true
        graphButton.isSelected = false
        settingButton.isSelected = false



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter(listTransaction)

        transactionData = binding.itemTransaction
        transactionData.adapter = adapter
        transactionData.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        transactionData.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )

        db = ViewModelProvider(requireActivity())[DBViewModel::class.java]

        Log.d("PENDING LIST NOT EMPTY?", pendingTransaction.isNotEmpty().toString())
        if (pendingTransaction.isNotEmpty()) {
            if (!database.isOpen){
                database.openHelper.writableDatabase
            }
            pendingTransaction.forEach { pair ->
                Log.d("ADDED TO DB", pair.first)
                db.addTransaksi(pair.first, "Pengeluaran", pair.second, "Unknown")
            }
            pendingTransaction.clear()
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(requireContext(), ContainerActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            val intent = Intent(requireContext(), ContainerActivity::class.java)
            startActivity(intent)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRandomTransactionReceived(event: com.example.bondoman.services.RandomTransactionEvent) {
        val transactionName = event.transactionName
        val price = event.price
        Log.d("TRANSACTION", transactionName+ "ADDED TO PENDING TRANSACTION")
        pendingTransaction.add(Pair(transactionName, price))
    }

    override fun onDestroyView() {
        super.onDestroyView()
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