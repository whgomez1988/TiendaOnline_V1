package com.example.tiendaonline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartItems: MutableList<Producto>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val producto = cartItems[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${producto.precio}"
        holder.descripcion.text = producto.descripcion

        // Manejar el clic en el botón de eliminar
        holder.btnEliminar.setOnClickListener {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.cartProductName)
        val precio: TextView = view.findViewById(R.id.cartProductPrice)
        val descripcion: TextView = view.findViewById(R.id.cartProductDescription)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }
}
