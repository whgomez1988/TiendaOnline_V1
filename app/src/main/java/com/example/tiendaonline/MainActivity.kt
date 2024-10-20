package com.example.tiendaonline

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Aquí no necesitas aplicar WindowInsets
        // Si necesitas un padding en el LinearLayout, lo puedes definir directamente en el XML
    }
}
