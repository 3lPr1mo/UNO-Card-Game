package com.example.uno_card_game

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    // R elements
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSingup: Button

    // Authentication
    private lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.email_edt)
        edtPassword = findViewById(R.id.password_edt)
        btnLogin = findViewById(R.id.login_button)
        btnSingup = findViewById(R.id.SingUp_button)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            closeKeyboard()
            authentication(email, password)
        }

        btnSingup.setOnClickListener{
            closeKeyboard()
            goIntent(SingUpActivity::class.java)
        }
    }

    // Function to close the keyboard once user click a button
    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }

    // Function to start another activity without repeat a lot of code
    private fun goIntent(activityClass: Class<*>){
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun authentication(email: String, password: String){

        fAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // loggin user
                    goIntent(UserActivity::class.java)
                } else {
                    Toast.makeText(this@MainActivity, "User doesn't exist", Toast.LENGTH_LONG).show()
                }
            }
    }
}