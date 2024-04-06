package com.example.bondoman.models

data class ScanResponse(
    val items: Items
) {
    override fun toString() = "Response(items=$items)"
}

data class Items(
    val items: List<Item>
) {
    override fun toString() = "Items(items=$items)"
}

data class Item(
    val name: String,
    val qty: Int,
    val price: Double
) {
    override fun toString() = "Item(name='$name', qty=$qty, price=$price)"
}