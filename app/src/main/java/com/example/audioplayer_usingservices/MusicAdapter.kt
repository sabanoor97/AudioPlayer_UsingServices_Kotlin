package com.example.audioplayer_usingservices

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer_usingservices.Model.Song

class MusicAdapter(private val context: Context, val onClick : (Uri)->Unit) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    var songs: MutableList<Song> = ArrayList()

    fun submitList(musicList: MutableList<Song>) {
        this.songs = musicList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.rv_items,
                parent, false
            )
        )
    }
    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "$position", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse(songs[position].url)
            onClick.invoke(uri)

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var songTitle = itemView.findViewById<TextView>(R.id.tv_audioName)

        fun bind(data: Song) {
            songTitle.text = data.name
        }
    }
}