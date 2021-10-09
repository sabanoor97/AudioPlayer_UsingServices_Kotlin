package com.example.audioplayer_usingservices

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer_usingservices.Model.MusicViewModel
import com.example.audioplayer_usingservices.Model.Song
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    companion object {
        const val SONG = "song"
    }

    private lateinit var musicViewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()

        musicViewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)

        checkPermissions()
        observeSongs()
    }

    private fun initUI() {
        //recycler view
        recyclerView = findViewById(R.id.rv_listOfMusic)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL
            )
        )
        musicAdapter = MusicAdapter(this) { uri ->

            val songIntent = Intent(this, MediaPlayerService::class.java)
            songIntent.putExtra(SONG, uri)
          ContextCompat.  startForegroundService(this,songIntent)

        }
        recyclerView.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            musicAdapter
        }
    }

    private fun observeSongs() {
        musicViewModel.songs.observe(this, {

            it?.let {
                if (this::musicAdapter.isInitialized) {
                    musicAdapter.submitList(it as MutableList<Song>)
                }
            }
        })
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getMusics()

        } else {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        getMusics()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                    }
                }).check()
        }
    }

    private fun getMusics() {
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = managedQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        val songs: MutableList<Song> = ArrayList()

        while (cursor.moveToNext()) {
            songs.add(
                Song(cursor.getString(2), cursor.getString(3))
            )
        }
        musicViewModel.setMusic(songs)
    }
}
