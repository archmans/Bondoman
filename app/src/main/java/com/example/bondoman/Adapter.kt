package com.example.bondoman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.retrofit.data.Transaction

class TransactionAdapter(private  val listData: ArrayList<Transaction>): RecyclerView.Adapter<TransactionAdapter.DataViewHolder>(){

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentData = listData[position]
        holder.txtName.text = currentData.name
        holder.txtCategory.text = currentData.category
        val temp = "IDR " + currentData.price.toString()
        holder.txtPrice.text = temp
        holder.txtLocation.text = currentData.location
        holder.txtDate.text = currentData.date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_transaction,parent,false)
        return DataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.count()
    }
    class DataViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val txtName: TextView = item.findViewById(R.id.itemName)
        val txtCategory: TextView = item.findViewById(R.id.category)
        val txtDate: TextView = item.findViewById(R.id.itemDate)
        val txtPrice : TextView = item.findViewById(R.id.itemPrice)
        val txtLocation : TextView = item.findViewById(R.id.itemLocation)
    }
}