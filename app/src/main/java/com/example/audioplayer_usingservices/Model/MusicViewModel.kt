package com.example.audioplayer_usingservices.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {

    private var _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    fun setMusic(songs: MutableList<Song>) {

        _songs.postValue(songs)
    }
}