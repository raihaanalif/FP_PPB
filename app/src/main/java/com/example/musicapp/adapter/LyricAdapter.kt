package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.OnItemClickCallback
import com.example.musicapp.R
import com.example.musicapp.model.ModelMain

class LyricAdapter(private val items: List<ModelMain>):
    RecyclerView.Adapter<LyricAdapter.ViewHolder>() {

        private var onItemClickCallback: OnItemClickCallback? = null

        fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback?) {
            this.onItemClickCallback = onItemClickCallback
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lyric, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val modelMain = items[position]
            holder.tvNamaArtis.text = modelMain.strArtis
            holder.tvJudulLagu.text = modelMain.strTitle
            holder.itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(modelMain)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNamaArtis: TextView = itemView.findViewById(R.id.tvNamaArtis)
            val tvJudulLagu: TextView = itemView.findViewById(R.id.tvJudulLagu)

        }
}