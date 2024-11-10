package com.example.tiendaonline

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // Registro del resultado de Google Sign-In
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario está autenticado
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Usuario autenticado, redirigir a ProductListActivity
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Usuario no autenticado, continuar con MainActivity
            setContentView(R.layout.activity_main)

            // Configuración de Google Sign-In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Aquí debe ir el ID de cliente correcto
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            auth = FirebaseAuth.getInstance()

            // Listener para el botón de login con Google
            findViewById<Button>(R.id.loginButton).setOnClickListener {
                signInWithGoogle()
            }

            // Listener para el botón de login con correo y contraseña
            findViewById<Button>(R.id.btnLogin).setOnClickListener {
                val email = findViewById<EditText>(R.id.inputEmail).text.toString()
                val password = findViewById<EditText>(R.id.inputPassword).text.toString()
                signInWithEmail(email, password)
            }

            // Listener para el botón de registro
            findViewById<Button>(R.id.btnRegister).setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            // Manejo de inicio de sesión exitoso
            // Guardar el estado de autenticación
            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            // Redirigir a ProductListActivity
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: ApiException) {
            // Manejo del error en caso de fallo
            e.printStackTrace()
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    // Redirigir a ProductListActivity
                    val intent = Intent(this, ProductListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Si el inicio de sesión falla, mostrar un mensaje al usuario.
                    Toast.makeText(baseContext, "Autenticación fallida.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}















