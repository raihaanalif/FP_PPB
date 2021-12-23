package com.example.musicapp.player

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.musicapp.R
import com.example.musicapp.adapter.MusicAdapter
import com.example.musicapp.model.ModelMusic
import com.example.musicapp.network.Api
import org.json.JSONException
import org.json.JSONObject
import com.example.musicapp.adapter.MusicAdapter.onSelectData
import com.example.musicapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MusicPlayerActivity : AppCompatActivity(), onSelectData {
    private var musicAdapter: MusicAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelMusic: MutableList<ModelMusic> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val so = findViewById<Button>(R.id.signout)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Is Displaying a data")

//        rvListMusic.setHasFixedSize(true)
//        rvListMusic.layoutManager =
//            LinearLayoutManager(this)

        //get data Music
        getListMusic()
    }


    private fun getListMusic() {
        progressDialog!!.show()
        AndroidNetworking.get(Api.MusicList)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    try {
                        progressDialog!!.dismiss()
                        val playerArray = response!!.getJSONArray("post")
                        for(i in 0 until playerArray.length()){
                            if (i > 3){
                                val temp = playerArray.getJSONObject(i)
                                val dataApi = ModelMusic()
                                dataApi.strId = temp.getString("id")
                                dataApi.cover = temp.getString("coverartikel")
                                dataApi.strBand = temp.getString("namaband")
                                dataApi.strTitle = temp.getString("judulmusic")
                                modelMusic.add(dataApi)
                                showPlaylist()
//                                Toast.makeText(
//                                    this@MusicPlayerActivity,
//                                    dataApi.strTitle, Toast.LENGTH_SHORT
//                                ).show()
                            }
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(this@MusicPlayerActivity, "Failed Display Music",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(anError: ANError) {
                    progressDialog!!.dismiss()
                    Toast.makeText(
                        this@MusicPlayerActivity,
                        "Your connection internet is unstable", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun showPlaylist(){
//        Toast.makeText(this@MusicPlayerActivity, "BERHASIL",
//            Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_main)
        val rvListMusic = findViewById<RecyclerView>(R.id.rvListMusic)
        rvListMusic.setHasFixedSize(true)
        rvListMusic.layoutManager =
            LinearLayoutManager(this)
        musicAdapter = MusicAdapter(
            this@MusicPlayerActivity,
            modelMusic,
            this
        )
        rvListMusic.adapter = musicAdapter
    }

    override fun onSelected(modelMusic: ModelMusic) {
        val intent = Intent(this@MusicPlayerActivity, DetailMusicActivity::class.java)
        intent.putExtra("detailMusic", modelMusic)
        startActivity(intent)
    }

    companion object{
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean){
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

    fun signout(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@MusicPlayerActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

//        Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show()
    }
}