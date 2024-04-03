package com.example.bondoman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GraphFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_grafik, container, false)
        val pieChart: PieChart = view.findViewById(R.id.pieChart)

        // Dummy data
        val income = 5000f
        val outcome = 3000f

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(income, "Income"))
        entries.add(PieEntry(outcome, "Outcome"))

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
