package com.example.onfire

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class AuthActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    lateinit var edt: EditText

    lateinit var edt2: EditText

    lateinit var btn1: Button

    lateinit var btn2: Button







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        analytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "AuthActivity")
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "AuthActivity")
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        btn1 = findViewById(R.id.boton1)
        btn2 = findViewById(R.id.boton2)
        edt = findViewById(R.id.correo)
        edt2 = findViewById(R.id.contraseña)



        setup()

    }

    private fun setup() {
        title = "Autenticación"
        btn1.setOnClickListener {
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt.text.toString(), edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        showLogout(it.result?.user?.email ?: "", ProviderType.BASIC)

                    }else {
                        Log.w("AuthActivity", "createUserWithEmail:failure", it.exception)
                        showAlert(it.exception)

                    }


                }


            }
        }

        btn2.setOnClickListener {
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(), edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        showLogout(it.result?.user?.email ?: "", ProviderType.BASIC)

                    }else {
                        Log.w("AuthActivity", "signInWithEmail:failure", it.exception)
                        showAlert(it.exception)

                    }


                }


            }
        }
    }

    private fun showAlert(e: Exception?){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        val message = when (e) {
            is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido o la contraseña es incorrecta."
            is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está en uso."
            else -> "Se ha producido un error autenticando al usuario."
        }
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLogout(email: String, provider: ProviderType){
        val LogoutIntent = Intent(this, LogoutActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(LogoutIntent)

    }


}
