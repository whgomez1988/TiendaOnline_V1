package com.example.tiendaonline

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val productos: MutableList<Producto>, private val activity: ProductListActivity) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${producto.precio}"
        holder.descripcion.text = producto.descripcion

        // Manejar el clic en el elemento de la lista
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra("nombre", producto.nombre)
            intent.putExtra("precio", producto.precio)
            intent.putExtra("descripcion", producto.descripcion)
            intent.putExtra("imageUri", producto.imageUri) // Asegúrate de que Producto tenga un campo imageUri
            activity.startActivity(intent)
        }

        // Manejar el clic en la imagen del producto
        holder.productImage.setOnClickListener {
            activity.showImagePickerDialog()
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.cartProductName)
        val precio: TextView = view.findViewById(R.id.cartProductPrice)
        val descripcion: TextView = view.findViewById(R.id.cartProductDescription)
        val productImage: ImageView = view.findViewById(R.id.productImage)
    }
}







