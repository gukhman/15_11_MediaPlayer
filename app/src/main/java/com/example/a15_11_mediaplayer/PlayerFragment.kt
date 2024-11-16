package com.example.a15_11_mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.a15_11_mediaplayer.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private lateinit var binding: FragmentPlayerBinding

    private var isPlaying = false
    private var currentTrackIndex = 0
    private val tracks = listOf(
        R.raw.underpressure,
        R.raw.vedmaosel,
        R.raw.iwasmadeforlovinyou,
        R.raw.kosmonavt,
        R.raw.tomsdiner
    )
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlayerBinding.bind(view)

        mediaPlayer = MediaPlayer.create(context, tracks[currentTrackIndex])
        mediaPlayer?.setOnCompletionListener {
            onTrackCompletion()
        }

        // Play/Pause
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseTrack()
            } else {
                playTrack()
            }
        }

        // Stop
        binding.stopButton.setOnClickListener {
            stopTrack()
        }

        // Prev
        binding.prevTrackButton.setOnClickListener {
            previousTrack()
        }

        // Next
        binding.nextTrackButton.setOnClickListener {
            nextTrack()
        }

        // SeekBar для громкости
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                mediaPlayer?.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // SeekBar для прогресса
        binding.progressSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * mediaPlayer!!.duration / 100)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Настройка начального состояния для SeekBar
        binding.progressSeekBar.max = 100
        binding.volumeSeekBar.progress = 70
    }

    private fun playTrack() {
        mediaPlayer?.start()
        isPlaying = true
        binding.playPauseButton.text = "Pause"
        updateProgress()
    }

    private fun pauseTrack() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.playPauseButton.text = "Play"
    }

    private fun stopTrack() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(context, tracks[currentTrackIndex])
        isPlaying = false
        binding.playPauseButton.text = "Play"
        binding.progressSeekBar.progress = 0
    }

    private fun previousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size
        changeTrack()
        playTrack()
    }

    private fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
        changeTrack()
        playTrack()
    }

    private fun changeTrack() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(context, tracks[currentTrackIndex]).apply {
            setOnCompletionListener {
                onTrackCompletion()
            }
        }

        mediaPlayer?.start()
        if (isPlaying) {
            binding.playPauseButton.text = "Pause"
        }
        updateProgress()
    }

    private fun onTrackCompletion() {
        nextTrack()
    }

    private fun updateProgress() {
        val currentProgress =
            (mediaPlayer?.currentPosition ?: 0) * 100 / (mediaPlayer?.duration ?: 1)
        binding.progressSeekBar.progress = currentProgress
        if (isPlaying) {
            Handler(Looper.getMainLooper()).postDelayed({
                updateProgress()
            }, 1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
