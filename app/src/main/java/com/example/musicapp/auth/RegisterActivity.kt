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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.MusicPlayerActivity
import com.example.musicapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val eName = findViewById<EditText>(R.id.name)
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

        val back = findViewById<ImageView>(R.id.back_login)
        back.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        val reg = findViewById<Button>(R.id.reg_button)
        reg.setOnClickListener{
            val name: String = eName.text.toString()
            val email: String = eEmail.text.toString()
            val password: String = ePassword.text.toString()

            if(name == ""){
                Toast.makeText(this@RegisterActivity, "Please enter your name",
                    Toast.LENGTH_SHORT).show()
            }else if (email == ""){
                Toast.makeText(this@RegisterActivity, "Please enter your email",
                    Toast.LENGTH_SHORT).show()
            }else if(password == ""){
                Toast.makeText(this@RegisterActivity, "Please enter your password",
                    Toast.LENGTH_SHORT).show()
            }else{
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            firebaseUserID = mAuth.currentUser!!.uid
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                                .child(firebaseUserID)

                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = firebaseUserID
                            userHashMap["name"] = name
                            userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/mymusicapp-c7078.appspot.com/o/photo.png?alt=media&token=64e2aac2-cdb5-4623-86b1-934d94ae87a6"
                            userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/mymusicapp-c7078.appspot.com/o/cover.jpg?alt=media&token=e086baab-ca07-4202-b0fc-2f6a5c8f049d"

                            refUsers.updateChildren(userHashMap)
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        val intent = Intent(this@RegisterActivity, MusicPlayerActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        }else{
                            Toast.makeText(this@RegisterActivity,
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