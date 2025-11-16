package com.example.onfire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class AuthActivity : AppCompatActivity() {

    lateinit var correoEditText: EditText
    lateinit var contraseñaEditText: EditText
    lateinit var registrarButton: Button
    lateinit var loginButton: Button

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        correoEditText = findViewById(R.id.correo)
        contraseñaEditText = findViewById(R.id.contraseña)
        registrarButton = findViewById(R.id.boton1)
        loginButton = findViewById(R.id.boton2)

        // Botón Registrar (Correo y Contraseña)
        registrarButton.setOnClickListener {
            val email = correoEditText.text.toString()
            val password = contraseñaEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length < 7) {
                    Toast.makeText(this, "La contraseña debe tener al menos 7 caracteres.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showHome(email)
                        } else {
                            Toast.makeText(this, "El correo no está registrado. Por favor, regístrese.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "El correo y la contraseña no pueden estar vacíos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Log In (Correo y Contraseña)
        loginButton.setOnClickListener {
            val email = correoEditText.text.toString()
            val password = contraseñaEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showHome(email)
                        } else {
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                Toast.makeText(this, "El correo no está registrado. Por favor, regístrese.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "El correo y la contraseña no pueden estar vacíos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Navega a la siguiente pantalla (HomeActivity)
    private fun showHome(email: String) {
        val homeIntent = Intent(this, LogoutActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
        finish() // Finaliza AuthActivity para que el usuario no pueda volver con el botón de atrás
        Toast.makeText(this, "¡Autenticación exitosa! Bienvenido $email", Toast.LENGTH_LONG).show()
    }
}
