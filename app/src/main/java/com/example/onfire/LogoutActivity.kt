package com.example.onfire

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}
class LogoutActivity : AppCompatActivity() {

    lateinit var txt: TextView
    lateinit var txt2: TextView
    lateinit var btn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txt = findViewById(R.id.emailTextView)
        txt2 = findViewById(R.id.providerTextView)
        btn = findViewById(R.id.cerrar)
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")




        setup(email ?:"", provider ?:"")
    }

    private fun setup(email: String, provider: String){
        title = "Inicio"
        txt.text = email
        btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }


    }
}