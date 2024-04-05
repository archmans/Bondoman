package com.example.bondoman

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.databinding.FragmentAddTransactionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.Locale
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.services.RandomTransactionEvent
import com.example.bondoman.services.RandomizeTransaction
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class AddTransactionFragment : Fragment(){
    private lateinit var binding: FragmentAddTransactionBinding
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var address : String? = null
    private var isFetched : Boolean = false
    private lateinit var db: DBViewModel

    private var pendingTransaction: MutableList<Pair<String, Double>> = mutableListOf()

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        db = ViewModelProvider(requireActivity())[DBViewModel::class.java]

        if (pendingTransaction.isNotEmpty()){
            pendingTransaction.forEach { pair ->
                db.addTransaksi(pair.first, "Pengeluaran", pair.second, "Unknown")
            }
            pendingTransaction.clear()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categorySpinner: Spinner = binding.addCategoryField
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("fetchLocation", "Both fine and coarse location permissions granted")
            fetchLocation { fetchedAddress ->
                address = fetchedAddress
            }
        } else {
            Log.e("fetchLocation", "Requesting permissions for fine and coarse location")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        binding.addButton.setOnClickListener {
            fetchLocation { fetchedAddress ->
                address = fetchedAddress
            }
            // Retrieve values from other fields
            val name = binding.addNameField.text.toString()
            val amountText = binding.addPriceField.text.toString()
            // Retrieve selected category from the Spinner
            val category = categorySpinner.selectedItem.toString()

            Log.d("addTransactionWoi", "Button clicked")


            val amount: Double
            try {
                amount = amountText.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Amount must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val location = if (address != null) {
                address!!
            } else {
                binding.addLocationField.text.toString()
            }

            Log.d("addTransactionWoi", "the location is $location")

            db.addTransaksi(name, category, amount, location)
            requireActivity().onBackPressed()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRandomTransactionReceived(event: com.example.bondoman.services.RandomTransactionEvent) {
        val transactionName = event.transactionName
        val price = event.price
        pendingTransaction.add(Pair(transactionName, price))
    }

    private fun fetchLocation(callback: (String) -> Unit) {
        var address = "Unknown"

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    Log.e("fetchLocation", "Fetching location")
                    val latitude = location.latitude
                    val longitude = location.longitude
                    address = getAddress(latitude, longitude)
                    callback(address)
                }
            }.addOnFailureListener { e ->
                Log.e("fetchLocation", "Failed to fetch location: ${e.message}")
                Toast.makeText(requireContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show()
                callback(address)
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            callback(address)
        }
    }



    private fun getAddress(latitude: Double, longitude: Double): String {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            "${address.getAddressLine(0)}, ${address.locality}, ${address.adminArea}, ${address.countryName}"
        } else {
            "Unknown Location"
        }
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
                        address = fetchedAddress
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