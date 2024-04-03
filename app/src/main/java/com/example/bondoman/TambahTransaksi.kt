package com.example.bondoman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TambahTransaksi : Fragment() {
    private  lateinit var nama: EditText
    private lateinit var kategori: EditText
    private lateinit var date: EditText
    private lateinit var nominal: EditText
    private lateinit var lokasi: EditText
    private lateinit var save: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tambah_transaksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDate = currentDate.format(formatter)
        date.setText(formattedDate)
        nama = view.findViewById(R.id.NamaTransaksi)
        kategori = view.findViewById(R.id.KategoriTransaksi)
        date = view.findViewById(R.id.TanggalTransaksi)
        nominal = view.findViewById(R.id.NominalTransaksi)
        lokasi = view.findViewById(R.id.LokasiTransaksi)
        save = view.findViewById(R.id.SimpanTransaksi)
    }

}