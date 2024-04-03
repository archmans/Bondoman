package com.example.bondoman.retrofit.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.R
import com.example.bondoman.retrofit.data.entity.TransactionEntity

class TransactionAdapter(private val listData: ArrayList<TransactionEntity>): RecyclerView.Adapter<TransactionAdapter.ViewHolder>(){

    override fun onBindViewHolder(holder: com.example.bondoman.TransactionAdapter.DataViewHolder, position: Int) {
        val currentData = listData[position]
        holder.txtName.text = currentData.name
        holder.txtCategory.text = currentData.category.toString()
        val temp = "IDR " + currentData.price.toString()
        holder.txtPrice.text = temp
        holder.txtLocation.text = currentData.location
        holder.txtDate.text = currentData.date

        // Set click listener for itemLocation TextView
        holder.txtLocation.setOnClickListener {
            val context = holder.itemView.context
            // Check if the location is not "Unknown" and not empty before opening Google Maps
            if (currentData.location != null && currentData.location != "Unknown" && currentData.location!!.isNotBlank()) {
                // Open Google Maps app
                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(currentData.location)}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    // Open Google Maps website in a web browser
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(currentData.location)}"))
                    context.startActivity(webIntent)
                }
            }
        }

        holder.itemView.setOnClickListener {
            // Display a toast message when the item is clicked
            val toastMessage = "Clicked item: ${currentData.name} with id ${currentData.id}"
            Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_SHORT).show()

            val intent = Intent(holder.itemView.context, AddTransactionActivity::class.java)

            // Optionally, pass data to the AddTransactionActivity
            intent.putExtra("transactionId", currentData.id)

            // Start the activity
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.example.bondoman.TransactionAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_data,parent,false)
        return com.example.bondoman.TransactionAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return listData.count()
    }
    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val txtName: TextView = item.findViewById(R.id.itemName)
        val txtCategory:TextView= item.findViewById(R.id.category)
        val txtDate: TextView= item.findViewById(R.id.itemDate)
        val txtPrice : TextView= item.findViewById(R.id.itemPrice)
        val txtLocation : TextView = item.findViewById(R.id.itemLocation)
    }
}