package com.example.tiendaonline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productName: TextView = findViewById(R.id.productName)
        val productPrice: TextView = findViewById(R.id.productPrice)
        val productDescription: TextView = findViewById(R.id.productDescription)
        val productImage: ImageView = findViewById(R.id.productImage)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)

        // Obtener los datos del producto desde el intent
        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getDoubleExtra("precio", 0.0)
        val descripcion = intent.getStringExtra("descripcion")
        val imageUri = intent.getStringExtra("imageUri")

        // Establecer los datos del producto en las vistas
        productName.text = nombre
        productPrice.text = getString(R.string.product_price, precio)
        productDescription.text = descripcion
        if (imageUri != null) {
            productImage.setImageURI(Uri.parse(imageUri))
        }

        // Manejar el clic en el botón de añadir al carrito
        btnAddToCart.setOnClickListener {
            // Lógica para añadir el producto al carrito
            // Puedes implementar la lógica según tus necesidades
            Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()

            // Redirigir a CartActivity
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}



