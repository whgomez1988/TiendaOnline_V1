package com.example.tiendaonline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class ProductListActivity : AppCompatActivity(), OnMapReadyCallback {

    private val database = FirebaseDatabase.getInstance().getReference("productos")
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productos: MutableList<Producto>
    private lateinit var googleMap: GoogleMap
    private lateinit var productImage: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        productos = mutableListOf()
        productAdapter = ProductAdapter(productos, this)
        recyclerView.adapter = productAdapter

        productImage = findViewById(R.id.productImage)
        productImage.setOnClickListener {
            showImagePickerDialog()
        }

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cargar productos
        cargarProductos()

        // Botón para agregar un producto
        val btnAgregarProducto: Button = findViewById(R.id.btnAgregarProducto)
        btnAgregarProducto.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
            val precio = findViewById<EditText>(R.id.editTextPrecio).text.toString().toDoubleOrNull() ?: 0.0
            val descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString()

            if (nombre.isNotEmpty() && precio > 0 && descripcion.isNotEmpty()) {
                val producto = Producto(nombre = nombre, precio = precio, descripcion = descripcion)
                agregarProducto(producto)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Inicializar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Botón para ir a la pantalla de inicio de sesión
        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón para ir al carrito de compras
        val btnCart: Button = findViewById(R.id.btnCart)
        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Solicitar permisos
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }
    }

    // Cargar productos desde Firebase
    private fun cargarProductos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                for (productoSnapshot in snapshot.children) {
                    val producto = productoSnapshot.getValue(Producto::class.java)
                    if (producto != null) {
                        producto.id = productoSnapshot.key
                        productos.add(producto)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductListActivity, "Error al cargar productos.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Agregar producto a Firebase
    private fun agregarProducto(producto: Producto) {
        val nuevoProductoId = database.push().key // Genera un ID único
        producto.id = nuevoProductoId
        if (nuevoProductoId != null) {
            database.child(nuevoProductoId).setValue(producto).addOnCompleteListener {
                Toast.makeText(this, "Producto agregado.", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
        }
    }

    // Limpiar campos después de agregar el producto
    private fun limpiarCampos() {
        findViewById<EditText>(R.id.editTextNombre).text.clear()
        findViewById<EditText>(R.id.editTextPrecio).text.clear()
        findViewById<EditText>(R.id.editTextDescripcion).text.clear()
        productImage.setImageResource(R.drawable.ic_camera) // Restablecer la imagen al ícono de cámara
    }

    // Inicializar el mapa
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true // Habilitar controles de zoom
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Mi Ubicación"))
                }
            }
        } else {
            checkPermissions()
        }
    }

    private fun setStoreLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val storeLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.addMarker(MarkerOptions().position(storeLatLng).title("Ubicación de la Tienda"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLatLng, 15f))
                }
            }
        } else {
            checkPermissions()
        }
    }

    // Mostrar el diálogo para seleccionar una imagen
    internal fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar desde galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar imagen")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePhoto()
                1 -> pickImageFromGallery()
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun pickImageFromGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    productImage.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImage: Uri? = data?.data
                    productImage.setImageURI(selectedImage)
                }
            }
        }
    }
}




















