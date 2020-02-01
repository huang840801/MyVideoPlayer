package com.guanhong.myvideoplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var surfaceHolder: SurfaceHolder

    private var mediaPlayer: MediaPlayer? = null
    private var myReceiver: MyRecever? = null
    private var mCurrentPosition = 0
    private var mDuration: Int = 0
    private var isMute = false

    companion object {

        const val VIDEO_URL = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        initBoardCastReceiver()

        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        if (mediaPlayer != null) {
            mediaPlayer!!.setDisplay(surfaceHolder)
        }
        MediaPlayProgress().start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {

    }

    private fun initBoardCastReceiver() {

        myReceiver = MyRecever()
        val filter = IntentFilter()
        filter.addAction("song")
        registerReceiver(myReceiver, filter)
    }

    private fun initView() {

        surfaceHolder = surfaceView!!.holder
        mediaPlayer = MediaPlayer.create(this, Uri.parse(VIDEO_URL))

        surfaceHolder.addCallback(this)
        surfaceHolder.setKeepScreenOn(false)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        mDuration = mediaPlayer!!.duration
        seekBar.max = mDuration
        seekBar.progress = 0

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer!!.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mediaPlayer!!.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer!!.start()

            }
        })
    }

    private fun initListener() {

        playBtn.setOnClickListener {

            playOrPause()
        }
        volumeBtn.setOnClickListener {

            setVolume()
        }
        rewindBtn.setOnClickListener {

            rewindVideo()
        }
        forwardBtn.setOnClickListener {

            forwardVideo()
        }

        fullscreenBtn.setOnClickListener {
            videoFullScreen()
            //todo 橫屏
        }
    }

    private fun videoFullScreen() {

    }

    private fun setVolume() {

        isMute = !isMute

        if (isMute) {

            volumeBtn!!.setBackgroundResource(R.drawable.volume_off)

            mediaPlayer!!.setVolume(0f, 0f)

        } else {

            volumeBtn!!.setBackgroundResource(R.drawable.volume_open)

            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.start()
            mediaPlayer!!.setVolume(1f, 1f)
        }
    }

    private fun forwardVideo() {

        seekBar!!.progress = mCurrentPosition + 1500
    }

    private fun rewindVideo() {

        seekBar!!.progress = mCurrentPosition - 1500
    }

    private fun playOrPause() {

        if (mediaPlayer != null) {

            if (mediaPlayer!!.isPlaying) {

                videoPause()
            } else {

                videoPlay()
            }
        }
    }

    private fun videoPlay() {

        playBtn!!.setBackgroundResource(R.drawable.pause)
        mediaPlayer!!.start()

        mCurrentPosition = mediaPlayer!!.currentPosition
        mDuration = mediaPlayer!!.duration
    }

    private fun videoPause() {

        playBtn!!.setBackgroundResource(R.drawable.play_arrow)
        mediaPlayer!!.pause()
    }

    private fun setTime(currentPosition: Int, duration: Int) {

        totalTime!!.text = ((duration / 1000 / 60 % 60 / 10).toString() + "" + duration / 1000 / 60 % 60 % 10 + ":"
                + mDuration / 1000 % 60 / 10 + "" + mDuration / 1000 % 60 % 10)
        currentTime!!.text = ((currentPosition / 1000 / 60 % 60 / 10).toString() + "" + currentPosition / 1000 / 60 % 60 % 10 + ":"
                + currentPosition / 1000 % 60 / 10 + "" + currentPosition / 1000 % 60 % 10)
    }

    private inner class MyRecever : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                val progress = intent.getIntExtra("progress", -1)
                seekBar!!.progress = progress

                setTime(progress, mDuration)

            }
        }
    }

    private inner class MediaPlayProgress : Thread() {
        override fun run() {
            super.run()

            var isVideoEnd = false

            val intent = Intent()
            intent.action = "song"

            while (!isVideoEnd) {

                val currentPosition = mediaPlayer!!.currentPosition

                intent.putExtra("progress", currentPosition)
                if (currentPosition == mediaPlayer!!.duration) {

                    Log.d("Huang", " over")
                    isVideoEnd = true
                }
                sendBroadcast(intent)
            }
        }
    }
}
