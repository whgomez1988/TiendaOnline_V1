package com.example.tiendaonline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cartItems = mutableListOf()
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        // Cargar los productos del carrito (esto puede ser desde una base de datos o SharedPreferences)
        cargarCarrito()
    }

    private fun cargarCarrito() {
        // Aquí puedes cargar los productos del carrito desde una base de datos o SharedPreferences
        // Por ahora, vamos a simular algunos productos en el carrito
        cartItems.add(Producto("Producto 1", 10.0, "Descripción del producto 1"))
        cartItems.add(Producto("Producto 2", 20.0, "Descripción del producto 2"))
        cartAdapter.notifyDataSetChanged()
    }
}
