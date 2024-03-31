package com.example.bondoman.helper

import android.content.Context
import com.example.bondoman.retrofit.data.Transaction
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class Xls {
    companion object {
        // Dummy Transaction class for testing
        fun saveTransactionSpreadsheet(context: Context, transactionList: List<Transaction>, fileFormat: String): String {
            // Create a new XSSFWorkbook or HSSFWorkbook based on the file format
            val workbook = if (fileFormat == "xlsx") XSSFWorkbook() else HSSFWorkbook()

            // Create a new sheet
            val sheet = workbook.createSheet("Transaction List")

            // Create a header row with all required columns
            val headers = arrayOf("Name", "Date", "Category", "Price", "Location")
            val headerRow = sheet.createRow(0)
            for ((index, header) in headers.withIndex()) {
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }

            // Populate data rows
            for ((index, transaction) in transactionList.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(transaction.name ?: "") // Handle null name
                row.createCell(1).setCellValue(transaction.date ?: "") // Handle null date
                row.createCell(2).setCellValue(transaction.category ?: "") // Handle null category
                row.createCell(3).setCellValue(transaction.price?.toDouble() ?: 0.0) // Handle null price
                row.createCell(4).setCellValue(transaction.location ?: "") // Handle null location
            }

            // Write the workbook to a file
            val fileName = "transaction_list.$fileFormat"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use {
                workbook.write(it)
            }

            return file.absolutePath
        }
    }

}