package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {
    private val alb: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val signInCoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(alb.root)

        // Configura toolbar
        setSupportActionBar(alb.toolbarIn.toolbar)
        supportActionBar?.title = "Login"

        alb.signUpBt.setOnClickListener {
            // Abre tela de Registro ao clicar no botão de registro
            startActivity(Intent(this, ActivityRegister::class.java))
        }

        alb.signInBt.setOnClickListener {
            // Faz login de um usuário no firebase utilizando email e senha
            signInCoroutineScope.launch {
                Firebase.auth.signInWithEmailAndPassword(
                    alb.emailLoginEt.text.toString(),
                    alb.passwordLoginEt.text.toString()
                ).addOnFailureListener {
                    // Caso dê errado
                    Toast.makeText(
                        this@ActivityLogin,
                        "Login failed. Cause ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnSuccessListener {
                    // Caso dê certo
                    openMainActivity()
                }
            }
        }

        alb.resetPasswordBt.setOnClickListener {
            signInCoroutineScope.launch {
                // Ao clicar no botão de resetar senha, utiliza o campo de email para realizar
                // uma solicitação ao firebase para troca de senha -> o usuário receberá um email
                // para troca de senha
                val email = alb.emailLoginEt.text.toString()
                if (email.isNotEmpty()) {
                    Firebase.auth.sendPasswordResetEmail(email)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Verifica se já existe um usuário logado, se tiver, vai para a tela principal
        if (Firebase.auth.currentUser != null) {
            openMainActivity()
        }
    }

    // Função usada para abrir a tela principal e fechar a tela atual
    private fun openMainActivity() {
        startActivity(
            Intent(this@ActivityLogin, MainActivity::class.java)
        )
        finish()
    }

}