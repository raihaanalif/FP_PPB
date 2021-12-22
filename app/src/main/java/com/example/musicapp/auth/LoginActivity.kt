package com.example.musicapp.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.MusicPlayerActivity
import com.example.musicapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val eEmail = findViewById<EditText>(R.id.email)
        val ePassword = findViewById<EditText>(R.id.password)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        setWindowFlag(
            this,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            false
        )
        window.statusBarColor = Color.TRANSPARENT

        val reg = findViewById<TextView>(R.id.register)
        reg.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        val log = findViewById<Button>(R.id.log_button)
        log.setOnClickListener{
            val email: String = eEmail.text.toString()
            val password: String = ePassword.text.toString()

            if (email == ""){
                Toast.makeText(this@LoginActivity, "Please enter your email",
                    Toast.LENGTH_SHORT).show()
            }else if(password == "") {
                Toast.makeText(
                    this@LoginActivity, "Please enter your password",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            val intent = Intent(this@LoginActivity, MusicPlayerActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this@LoginActivity,
                                "Error Message:" + task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}