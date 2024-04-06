package com.example.bondoman

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bondoman.AddTransactionFragment.Companion.LOCATION_PERMISSION_REQUEST_CODE
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

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("fetchLocation", "Both fine and coarse location permissions granted")
            fetchLocation { fetchedAddress ->
                binding.locationField.setText(fetchedAddress)
            }
        } else {
            Log.e("fetchLocation", "Requesting permissions for fine and coarse location")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
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
    private fun fetchLocation(callback: (String) -> Unit) {
        var address = "Unknown"
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    fetchLocation { fetchedAddress ->
                        binding.locationField.setText(fetchedAddress)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e("fetchLocation", "Location permissions denied")
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}