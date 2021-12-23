package com.example.musicapp

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.musicapp.model.ModelMain
import com.example.musicapp.network.ApiEndpoint
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text

class DetailLyricsActivity : AppCompatActivity() {

    var modelMain: ModelMain? = null
    var strIdLagu: String = ""
    var strLirikLagu: String = ""
    var strNamaArtis: String = ""
    var strJudulLagu: String = ""
    var progressDialog: ProgressDialog? = null

    companion object {
        const val LIST_LYRICS = "lyrics"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lyric)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Is Displaying a data")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        modelMain = intent.getSerializableExtra(LIST_LYRICS) as ModelMain
        if (modelMain != null) {
            strIdLagu = modelMain!!.strId
            getDetailLyrics()
        }
    }

    private fun getDetailLyrics() {
        setContentView(R.layout.activity_detail_lyric)
        val tvNamaArtis = findViewById<TextView>(R.id.tvNamaArtis)
        val tvJudulLagu = findViewById<TextView>(R.id.tvJudulLagu)
        val tvLirikLagu = findViewById<TextView>(R.id.tvLirikLagu)
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.DETAILURl)
            .addPathParameter("id", strIdLagu)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        progressDialog?.dismiss()
                        val jsonObject = response.getJSONObject("data")

                        strNamaArtis = jsonObject.getString("artist")
                        tvNamaArtis.text = strNamaArtis

                        strJudulLagu = jsonObject.getString("songTitle")
                        tvJudulLagu.text = strJudulLagu

                        strLirikLagu = jsonObject.getString("songLyrics")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            tvLirikLagu.text = Html.fromHtml(strLirikLagu, Html.FROM_HTML_MODE_LEGACY)
                        else {
                            tvLirikLagu.text = Html.fromHtml(strLirikLagu)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@DetailLyricsActivity, "Gagal menampilkan data!",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError) {
                    progressDialog?.dismiss()
                    Toast.makeText(this@DetailLyricsActivity, "Tidak ada jaringan internet!",
                        Toast.LENGTH_SHORT).show()
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

}