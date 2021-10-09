package com.example.audioplayer_usingservices

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MediaPlayerService : Service() {
    private var songURI: Uri? = null
    private val channelId = "foreground_channel_id"
    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate() {
        super.onCreate()
        Log.d("onStartCommand","onCreate")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("onStartCommand","onStartCommand")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(songURI.toString())
            .setContentText("Playing")
            .setSmallIcon(R.drawable.play)
            .setContentIntent(pendingIntent)
            .build()

      startForeground(1, notification)
        songURI = intent?.getParcelableExtra(MainActivity.SONG)

        if (!this::mediaPlayer.isInitialized) {

            mediaPlayer = MediaPlayer.create(this, songURI)
            mediaPlayer.isLooping = false
            mediaPlayer.start()

        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.release()
                mediaPlayer = MediaPlayer.create(this, songURI)
                mediaPlayer.start()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onStartCommand","onDestroy")

    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
