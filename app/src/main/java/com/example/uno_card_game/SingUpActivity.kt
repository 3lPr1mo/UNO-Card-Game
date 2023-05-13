package com.example.uno_card_game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SingUpActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerBtn: Button

    private lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        fAuth = FirebaseAuth.getInstance()

        username = findViewById(R.id.username_singup_edt)
        email = findViewById(R.id.email_singup_edt)
        password = findViewById(R.id.password_singup_edt)
        registerBtn = findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {
            closeKeyboard()
            register(username.text.toString(),
                    email.text.toString(),
                    password.text.toString())
        }

    }

    // Function to register the user on Firebase
    private fun register(username: String, email: String, password: String){
        fAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val intent = Intent(this@SingUpActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@SingUpActivity, "An Error has ocurred, try again", Toast.LENGTH_LONG).show()
                }
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

}