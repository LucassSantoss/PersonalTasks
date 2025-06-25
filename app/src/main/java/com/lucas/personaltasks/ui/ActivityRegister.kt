package com.lucas.personaltasks.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.ActivityMainBinding
import com.lucas.personaltasks.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityRegister : AppCompatActivity() {
    private val arb: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(arb.root)

        setSupportActionBar(arb.toolbarIn.toolbar)
        supportActionBar?.title = "Register"

        arb.signUpBt.setOnClickListener {
            val signUpCoroutine = CoroutineScope(Dispatchers.IO)

            signUpCoroutine.launch {
                Firebase.auth.createUserWithEmailAndPassword(
                    arb.emailRegisterEt.text.toString(),
                    arb.passwordRegisterEt.text.toString()
                ).addOnFailureListener {
                    Toast.makeText(
                        this@ActivityRegister,
                        "Registration failed. Cause: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnSuccessListener {
                    Toast.makeText(
                        this@ActivityRegister,
                        "Registration successful.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        }
    }


}