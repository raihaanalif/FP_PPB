package com.example.musicapp.lyric

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.musicapp.DetailLyricsActivity
import com.example.musicapp.OnItemClickCallback
import com.example.musicapp.R
import com.example.musicapp.adapter.LyricAdapter
import com.example.musicapp.model.ModelMain
import com.example.musicapp.network.ApiEndpoint
import org.json.JSONException
import org.json.JSONObject


class FindLyricActivity : AppCompatActivity(){
    var modelMain = ArrayList<ModelMain>()
    var mainAdapter : LyricAdapter? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyric)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Is Displaying a data")

        val searchSong = findViewById<SearchView>(R.id.searchSong)

        searchSong.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setSearchSong(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    getListSongTop()
                }
                return false
            }
        })

        val searchPlateId = searchSong.context.resources
            .getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searchSong.findViewById<View>(searchPlateId)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)

        val rvListMusic = findViewById<RecyclerView>(R.id.rvListMusic)

        rvListMusic.layoutManager = LinearLayoutManager(this)
        rvListMusic.setHasFixedSize(true)

        getListSongTop()
    }

    private fun getListSongTop(){
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.BASEURl)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        progressDialog?.dismiss()
                        val jsonArray = response.getJSONArray("data")
                        for (i in 0..10) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val dataApi = ModelMain()
                            dataApi.strId = jsonObject.getString("songId")
                            dataApi.strArtis = jsonObject.getString("artist")
                            dataApi.strTitle = jsonObject.getString("songTitle")
                            modelMain.add(dataApi)
                        }
                        showRecyclerSong()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@FindLyricActivity, "Gagal menampilkan data!",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError) {
                    progressDialog?.dismiss()
                    Toast.makeText(this@FindLyricActivity, "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setSearchSong(query: String) {
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.SEARCHURl)
            .addPathParameter("query", query)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        progressDialog?.dismiss()
                        val jsonArray = response.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val dataApi = ModelMain()
                            dataApi.strId = jsonObject.getString("songId")
                            dataApi.strArtis = jsonObject.getString("artist").replace("· ", "")
                            dataApi.strTitle = jsonObject.getString("songTitle")
                            modelMain.add(dataApi)
                        }
                        showRecyclerSong()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@FindLyricActivity, "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError) {
                    progressDialog?.dismiss()
                    Toast.makeText(this@FindLyricActivity, "Your connection internet is unstable", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showRecyclerSong() {
        setContentView(R.layout.activity_lyric)
        val rvListMusic = findViewById<RecyclerView>(R.id.rvListMusic)
        mainAdapter = LyricAdapter(modelMain)
        rvListMusic.adapter = mainAdapter
        mainAdapter?.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(modelMain: ModelMain) {
                val intent = Intent(this@FindLyricActivity, DetailLyricsActivity::class.java)
                intent.putExtra(DetailLyricsActivity.LIST_LYRICS, modelMain)
                startActivity(intent)
            }
        })
    }
}