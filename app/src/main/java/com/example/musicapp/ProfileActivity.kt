package com.example.musicapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity(){

   private var GALLERY_INTENT_CODE = 1023
   lateinit var mAuth: FirebaseAuth
   lateinit var mStore: FirebaseFirestore
   private var firebaseUserID: String =""
   lateinit var imageUri : Uri

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_profile)
      val phone = findViewById<EditText>(R.id.eTelp)
      val name = findViewById<EditText>(R.id.eNama)
      val email = findViewById<EditText>(R.id.email)

      val img = findViewById<ImageView>(R.id.account_image)

      mAuth = FirebaseAuth.getInstance()

      img.setOnClickListener{
         val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
         startActivityForResult(intent, GALLERY_INTENT_CODE)
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(requestCode == GALLERY_INTENT_CODE && requestCode == RESULT_OK){
         val imgBitmap = data?.extras?.get("data") as Bitmap
         uploadImage(imgBitmap)
      }
   }

   private fun uploadImage(imgBitmap: Bitmap){
      val baos = ByteArrayOutputStream()
      val mStore = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth
         .getInstance().currentUser?.uid}")

      imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
      val image = baos.toByteArray()

      mStore.putBytes(image).addOnCompleteListener{
         if(it.isSuccessful){
            mStore.downloadUrl.addOnCompleteListener{
               it.result.let{
                  imageUri = it
                  setContentView(R.layout.activity_profile)
                  val img = findViewById<ImageView>(R.id.account_image)
                  img.setImageBitmap(imgBitmap)
               }
            }
         }
      }

   }

}