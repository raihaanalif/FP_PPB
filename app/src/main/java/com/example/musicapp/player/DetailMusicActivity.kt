package com.example.musicapp.player

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.musicapp.R
import com.example.musicapp.model.ModelMusic
import com.example.musicapp.network.Api
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class DetailMusicActivity : AppCompatActivity() {

    var idLagu: String? = null
    var modelMusic: ModelMusic? = null
    var progressDialog: ProgressDialog? = null
    var mHandler: Handler? = null
    var mRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar_detail = findViewById<Toolbar>(R.id.toolbar_detail)
        val imgPause = findViewById<ImageView>(R.id.imgPause)
        val imgPlay = findViewById<ImageView>(R.id.imgPlay)
        val imgCover = findViewById<ImageView>(R.id.imgCover)
        //set Transparent Statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        toolbar_detail.title = null
        setSupportActionBar(toolbar_detail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("System will display your music, enjoy")

        mHandler = Handler()

        modelMusic = intent.getSerializableExtra("detailMusic") as ModelMusic
        if (modelMusic != null) {
            idLagu = modelMusic!!.strId

            //Get image source
            Glide.with(this)
                .load(modelMusic!!.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgCover)

            imgPause.visibility = View.INVISIBLE
            imgPlay.visibility = View.VISIBLE

            //Method get data
            getDetailMusic()
        }
    }

    private fun getDetailMusic() {
        progressDialog!!.show()
        setContentView(R.layout.activity_detail)
        val tvTitleMusic = findViewById<TextView>(R.id.tvTitleMusic)
        val tvBand = findViewById<TextView>(R.id.tvBand)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val imgPlay = findViewById<ImageView>(R.id.imgPlay)
        val imgPause = findViewById<ImageView>(R.id.imgPause)
        val imgCover = findViewById<ImageView>(R.id.imgCover)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val toolbar_details = findViewById<Toolbar>(R.id.toolbar_detail)

        AndroidNetworking.get(Api.DetailMusic)
            .addPathParameter("id", idLagu)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        progressDialog!!.dismiss()
                        val playerArray = response.getJSONArray("data")
                        for (i in 0 until playerArray.length()) {

                            val temp = playerArray.getJSONObject(i)

                            val title = temp.getString("judulmusic")
                            tvTitleMusic!!.text = title

                            val band = temp.getString("namaband")
                            tvBand!!.text = band

                            val img = temp.getString("coverartikel")
                            //Get Image
                            Glide.with(this@DetailMusicActivity)
                                .load(img)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .placeholder(R.drawable.ic_no_image_foreground)
                                .into(imgCover)

                            val urlMusic = temp.getString("linkmp3")
                            val mediaPlayer = MediaPlayer()
                            var currentPosition = mediaPlayer.currentPosition/1000

                            imgPlay!!.setOnClickListener {

                                try {
                                    if(currentPosition == 0){
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                                        mediaPlayer.setDataSource(urlMusic)
                                        mediaPlayer.prepare()
                                        mediaPlayer.start()
                                    }else{
                                        mediaPlayer.start()
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                imgPlay.visibility = View.INVISIBLE
                                imgPause!!.visibility = View.VISIBLE
                                seekBar!!.max = mediaPlayer.duration / 1000

                                mRunnable = Runnable {
                                    val mCurrentPosition = mediaPlayer.currentPosition / 1000
                                    val duration = mediaPlayer.duration
                                    @SuppressLint("DefaultLocale")
                                    val time = String.format("%02d min, %02d sec",
                                        TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                                        TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
                                    )
                                    seekBar.progress = mCurrentPosition
                                    mHandler!!.postDelayed(mRunnable!!, 1000)
                                    tvTime!!.text = time
                                }
                                mHandler!!.postDelayed(mRunnable!!, 1000)
                            }
                            imgPause!!.setOnClickListener {
                                mediaPlayer.pause()
                                currentPosition = mediaPlayer.currentPosition / 1000
                                imgPlay.visibility = View.VISIBLE
                                imgPause.visibility = View.INVISIBLE
                            }


                            toolbar_details.setOnClickListener{
                                mediaPlayer.stop()
                                mediaPlayer.reset()
                                intent = Intent(this@DetailMusicActivity, MusicPlayerActivity::class.java)
                                startActivity(intent)
                            }


                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@DetailMusicActivity,
                            "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError) {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@DetailMusicActivity,
                        "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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