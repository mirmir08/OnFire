package com.example.onfire

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _currentUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    fun registerUser(email: String, password: String){
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(javaClass.simpleName, "Usuario registrado exitosamente")

            }catch (e: Exception){
                Log.d(javaClass.simpleName, "${e.message}")

            }
        }

        fun loginUser(email: String, password: String){
            viewModelScope.launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    Log.d(javaClass.simpleName, "Usuario iniciado exitosamente")

                }catch (e: Exception){
                    Log.d(javaClass.simpleName, "${e.message}")

                }
            }

        }

        fun logout(){
            try {
                auth.signOut()
                Log.d(javaClass.simpleName, "Usuario iniciado exitosamente")

            }catch (e: Exception){
                Log.d(javaClass.simpleName, "${e.message}")

            }

        }

        fun listenToAuthState(){
            auth.addAuthStateListener {
                this._currentUser.value = it.currentUser

            }
        }
    }
}