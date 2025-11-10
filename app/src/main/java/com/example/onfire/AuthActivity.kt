package com.example.onfire // Asegúrate de que este sea tu paquete correcto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    lateinit var correoEditText: EditText
    lateinit var contraseñaEditText: EditText
    lateinit var registrarButton: Button
    lateinit var loginButton: Button


    //private lateinit var googleSignInClient: GoogleSignInClient
    //private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    //private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // --- Configuración de Google Sign-In ---

        // 1. Configurar las opciones de Google Sign-In solicitando el ID Token.
        //    El ID de cliente web se obtiene de tu archivo google-services.json,
        //    así que no necesitas añadirlo manualmente si usas getString.
        //val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            //.requestEmail()
            //.build()

        // 2. Crear un GoogleSignInClient
        //googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 3. Lanzador para el resultado del inicio de sesión de Google
        //googleSignInLauncher = registerForActivityResult(
            //ActivityResultContracts.StartActivityForResult()
        //) { result ->
            //if (result.resultCode == Activity.RESULT_OK) {
                //val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                //handleGoogleSignInResult(task)
            //} else {
                //Toast.makeText(this, "El inicio de sesión con Google fue cancelado", Toast.LENGTH_SHORT).show()
            //}
        //}

        // --- Configuración de los botones de la UI ---

        correoEditText = findViewById(R.id.correo)
        contraseñaEditText = findViewById(R.id.contraseña)
        registrarButton = findViewById(R.id.boton1)
        loginButton = findViewById(R.id.boton2)
        //val googleButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.boton3)

        // Botón Registrar (Correo y Contraseña)
        registrarButton.setOnClickListener {
            val email = correoEditText.text.toString()
            val password = contraseñaEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showHome(email)
                        } else {
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "El correo y la contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "El correo y la contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón de Google (`boton3`)
        //googleButton.setOnClickListener {
            //signInWithGoogle()
        //}
    }

    //private fun signInWithGoogle() {
        //val signInIntent = googleSignInClient.signInIntent
        //googleSignInLauncher.launch(signInIntent)
    //}

    //private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        //try {
            // Obtener la cuenta de Google
            //val account = completedTask.getResult(ApiException::class.java)!!
            //Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            // Autenticar con Firebase
            //firebaseAuthWithGoogle(account.idToken!!)
        //} catch (e: ApiException) {
            // Error al iniciar sesión con Google
            //Log.w(TAG, "El inicio de sesión con Google falló", e)
            //Toast.makeText(this, "Fallo en el inicio de sesión con Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        //}
    //}

    //private fun firebaseAuthWithGoogle(idToken: String) {
        //val credential = GoogleAuthProvider.getCredential(idToken, null)
        //firebaseAuth.signInWithCredential(credential)
            //.addOnCompleteListener(this) { task ->
                //if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    //val user = firebaseAuth.currentUser
                    //showHome(user?.email ?: "Sin Correo")
                //} else {
                    // Si falla el inicio de sesión, muestra un mensaje.
                    //Toast.makeText(this, "Fallo en la autenticación con Firebase", Toast.LENGTH_SHORT).show()
                //}
            //}
    //}

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
