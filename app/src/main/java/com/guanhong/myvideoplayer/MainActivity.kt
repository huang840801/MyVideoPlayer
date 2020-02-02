package com.guanhong.myvideoplayer

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var surfaceHolder: SurfaceHolder

    private var handler = Handler()
    private var currentTime = 0
    private var isMute = false

    companion object {

        const val VIDEO_URL = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(VIDEO_URL))

        initSurfaceView()

        setTotalTime(mediaPlayer.duration)

        seekBar.max = mediaPlayer.duration
        seekBar.progress = 0

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, isFromUser: Boolean) {

                currentTime = progressValue

                if (isFromUser) {
                    mediaPlayer.seekTo(progressValue)
                }

                if (progressValue == seekBar.max) {

                    stopVideoPlaying()
                    videoPause()
                }

                setCurrentTime(currentTime)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        playBtn.setOnClickListener {

            if (mediaPlayer.isPlaying) {

                videoPause()
            } else {

                videoPlay()
            }
        }
        volumeBtn.setOnClickListener {

            setVolume()
        }
        rewindBtn.setOnClickListener {

            currentTime -= 15000
            mediaPlayer.seekTo(currentTime)
        }
        forwardBtn.setOnClickListener {

            currentTime += 15000
            mediaPlayer.seekTo(currentTime)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.release()
    }

    private fun initSurfaceView() {

        surfaceHolder = surfaceView!!.holder

        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                mediaPlayer.setDisplay(surfaceHolder)

                hideProgressBar()
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}
        })
        surfaceHolder.setKeepScreenOn(false)
        surfaceHolder.setType(AudioManager.STREAM_MUSIC)
    }

    private fun setTotalTime(millisecond: Int) {

        val minutes = millisecond / 1000 / 60

        val minutesString = if (minutes < 10) {

            "0$minutes"
        } else {
            minutes.toString()
        }
        val second = millisecond / 1000 % 60

        val secondString = if (second < 10) {

            "0$second"
        } else {
            second.toString()
        }
        totalTime.text = "$minutesString : $secondString"
    }

    private fun setCurrentTime(millisecond: Int) {

        val minutes = millisecond / 1000 / 60

        val minutesString = if (minutes < 10) {

            "0$minutes"
        } else {
            minutes.toString()
        }
        val second = millisecond / 1000 % 60

        val secondString = if (second < 10) {

            "0$second"
        } else {
            second.toString()
        }

        currentTimeTextView.text = "$minutesString : $secondString"
    }

    private fun videoPlay() {

        startVideoPlaying()

        playBtn!!.setBackgroundResource(R.drawable.pause)
        mediaPlayer.start()
    }

    private fun videoPause() {

        playBtn.setBackgroundResource(R.drawable.play_arrow)
        mediaPlayer.pause()
        stopVideoPlaying()
    }

    private fun setVolume() {

        isMute = !isMute

        if (isMute) {

            volumeBtn.setBackgroundResource(R.drawable.volume_off)

            mediaPlayer.setVolume(0f, 0f)

        } else {

            volumeBtn!!.setBackgroundResource(R.drawable.volume_open)

            mediaPlayer.setVolume(1f, 1f)
        }
    }

    private fun startVideoPlaying() {

        currentTime = mediaPlayer.currentPosition

        handler.postDelayed({

            seekBar.progress = currentTime
            startVideoPlaying()

        }, 1000)
    }

    private fun stopVideoPlaying() {

        handler.removeCallbacksAndMessages(null)
    }

    private fun showProgressBar() {

        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {

        progressBar.visibility = View.INVISIBLE

    }
}
