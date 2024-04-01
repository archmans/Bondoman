package com.example.bondoman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.databinding.FragmentDetailTransactionBinding
import com.example.bondoman.databinding.FragmentEditTransactionBinding

class DetailTransactionFragment : Fragment() {
    private lateinit var binding : FragmentDetailTransactionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}