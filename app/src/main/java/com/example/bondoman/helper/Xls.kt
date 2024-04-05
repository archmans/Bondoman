package com.example.bondoman.helper

import android.content.Context
import com.example.bondoman.retrofit.data.entity.TransactionEntity
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

class Xls {
    companion object {
        // Dummy Transaction class for testing
        fun saveXls(
            context: Context,
            transactionList: List<TransactionEntity>,
            fileFormat: String,
            directory: File
        ): String {
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
            // Populate data rows
            for ((index, transaction) in transactionList.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(transaction.name ?: "") // Handle null name

                // Format the date and set it to the cell
                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                row.createCell(1).setCellValue(transaction.date?.format(dateFormat) ?: "") // Handle null date

                row.createCell(2).setCellValue(transaction.category.toString()) // Handle null category
                row.createCell(3).setCellValue(transaction.price?.toDouble() ?: 0.0) // Handle null price
                row.createCell(4).setCellValue(transaction.location ?: "") // Handle null location
            }

            // Write the workbook to a file
            val fileName = "transaction_list.$fileFormat"
            val file = File(directory, "your_file_name.$fileFormat")
            FileOutputStream(file).use {
                workbook.write(it)
            }

            return file.absolutePath
        }
    }
}