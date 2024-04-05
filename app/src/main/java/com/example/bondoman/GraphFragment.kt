package com.example.bondoman

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bondoman.retrofit.data.TransactionDB
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GraphFragment : Fragment() {

    private lateinit var db: DBViewModel
    private lateinit var database: TransactionDB
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = TransactionDB.getInstance(requireContext())
        if (!database.isOpen) {
            database.openHelper.writableDatabase
        }

        val view = inflater.inflate(R.layout.fragment_grafik, container, false)
        val pieChart: PieChart = view.findViewById(R.id.pieChart)
        db = ViewModelProvider(requireActivity())[DBViewModel::class.java]

        val navbar = requireActivity().findViewById<LinearLayout>(R.id.navbar_main)
        val toolbar = requireActivity().findViewById<RelativeLayout>(R.id.toolbar)
        navbar.setBackgroundResource(R.drawable.navbar_background)
        toolbar.setBackgroundColor(
            Color.parseColor("#000113")
        )
        val textView = toolbar.findViewById<TextView>(R.id.toolbar_text)
        val transactionButton = requireActivity().findViewById<ImageButton>(R.id.transaction_button)
        val graphButton = requireActivity().findViewById<ImageButton>(R.id.graph_button)
        val settingButton = requireActivity().findViewById<ImageButton>(R.id.setting_button)
        textView.text = "Grafik"
        textView.setTextColor(Color.WHITE)
        toolbar.findViewById<ImageButton>(R.id.toolbar_back_button).setImageResource(R.drawable.ic_arrow_left_white)
        transactionButton.isSelected = false
        graphButton.isSelected = true
        settingButton.isSelected = false

        // Dummy data
        val graphData = db.getGraphData()
        Log.d("DATA", graphData.toString())

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(graphData[0].amount!!.toFloat(), graphData[0].category.toString()))
        entries.add(PieEntry(graphData[1].amount!!.toFloat(), graphData[1].category.toString()))

        val dataSet = PieDataSet(entries, "Transaction Summary")

        val colors = ArrayList<Int>()
        colors.add(resources.getColor(R.color.colorIncome))
        colors.add(resources.getColor(R.color.colorOutcome))
        dataSet.colors = colors
        dataSet.valueTextSize = 24f
        dataSet.valueTextColor = resources.getColor(R.color.text_graph)


        val data = PieData(dataSet)

        pieChart.data = data

        pieChart.description.isEnabled = false

        pieChart.setHoleColor(android.R.color.transparent)

        val legend = pieChart.legend
        legend.isEnabled = false

        pieChart.invalidate()

        return view
    }


}
