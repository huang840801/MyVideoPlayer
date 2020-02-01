package com.guanhong.myvideoplayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback {

    private val mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4"

    private var mSurfaceView: SurfaceView? = null//展示 视频的
    private var mHolder: SurfaceHolder? = null//
    private var mSeekBar: SeekBar? = null
    private var mMediaPlayer: MediaPlayer? = null//播放音视频的
    private var flage = true
    private var myRecever: MyRecever? = null

    //    private VideoView mVideoView;

    //    private MediaPlayer mMediaPlayer;
    //    private MediaController mMediaController;

    private var mButtonVolume: Button? = null
    private var mButtonRewind: Button? = null
    private var mButtonPlay: Button? = null
    private var mButtonForward: Button? = null
    private var mButtonFullScreen: Button? = null

    private var mCurrentTime: TextView? = null
    private var mTotalTime: TextView? = null

    private var mCurrentPosition = 0
    private var mDuration: Int = 0

    private var mHandler: Handler? = null

    private var mLinearLayout: LinearLayout? = null

    private var isMute = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {


        mButtonVolume = findViewById(R.id.btn_volume)
        mButtonRewind = findViewById(R.id.btn_rewind)
        mButtonPlay = findViewById(R.id.btn_play)
        mButtonForward = findViewById(R.id.btn_forward)
        mButtonFullScreen = findViewById(R.id.btn_fullscreen)

        mCurrentTime = findViewById(R.id.textview_current_time)
        mTotalTime = findViewById(R.id.textview_total_time)

        mSeekBar = findViewById(R.id.seek_bar)

        mLinearLayout = findViewById(R.id.ll)

        mHandler = Handler()

        mSurfaceView = findViewById(R.id.surfaceview)
        mHolder = mSurfaceView!!.holder
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(mVideoUrl))
        //        mVideoView = findViewById(R.id.videoview);

        mHolder!!.addCallback(this)
        mHolder!!.setKeepScreenOn(false)
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        mDuration = mMediaPlayer!!.duration
        mSeekBar!!.max = mDuration
        mSeekBar!!.progress = 0

        myRecever = MyRecever()
        val filter = IntentFilter()
        filter.addAction("song")
        registerReceiver(myRecever, filter)
        mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mMediaPlayer!!.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mMediaPlayer!!.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mMediaPlayer!!.start()

            }
        })


        mButtonPlay!!.setOnClickListener(this)
        mButtonVolume!!.setOnClickListener(this)
        mButtonRewind!!.setOnClickListener(this)
        mButtonForward!!.setOnClickListener(this)
        mButtonFullScreen!!.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_play ->

                playOrPause()
            R.id.btn_forward ->

                videoForward()
            R.id.btn_rewind ->

                videoRewind()
            R.id.btn_volume ->

                volumeMute()
            R.id.btn_fullscreen ->

                videoFullScreen()
        }

    }

    private fun videoFullScreen() {
        Log.d(TAG, "  fullscreen")

    }

    private fun volumeMute() {

        //        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //        mMediaPlayer.prepareAsync();


        if (isMute) {

            Log.d(TAG, "  mute")

            mButtonVolume!!.setBackgroundResource(R.drawable.volume_open)
            isMute = false

            mMediaPlayer!!.setVolume(0f, 0f)

        } else {

            Log.d(TAG, "  open")

            mButtonVolume!!.setBackgroundResource(R.drawable.volume_off)
            isMute = true

            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.start()
            mMediaPlayer!!.setVolume(1f, 1f)


            //            mMediaPlayer.setVolume(100f,100f);
            //            AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
            //            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            //            mMediaPlayer.start();
            //            mMediaPlayer.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));

        }

    }

    private fun videoForward() {
        Log.d(TAG, "  videoForward mCurrentPosition = $mCurrentPosition")

        //        mMediaPlayer.seekTo();
        mSeekBar!!.progress = mCurrentPosition + 1500
    }

    private fun videoRewind() {
        Log.d(TAG, "  videoRewind mCurrentPosition = $mCurrentPosition")

        mSeekBar!!.progress = mCurrentPosition - 1500


    }

    private fun playOrPause() {


        if (!mMediaPlayer!!.isPlaying && mMediaPlayer != null) {

            Log.d(TAG, "  playing")

            mButtonPlay!!.setBackgroundResource(R.drawable.pause)
            mMediaPlayer!!.start()

            mCurrentPosition = mMediaPlayer!!.currentPosition
            mDuration = mMediaPlayer!!.duration
            Log.d(TAG, "  currentPosition = $mCurrentPosition  mDuration = $mDuration")


        } else if (mMediaPlayer!!.isPlaying && mMediaPlayer != null) {
            Log.d(TAG, "  pause")
            mButtonPlay!!.setBackgroundResource(R.drawable.play_arrow)
            mMediaPlayer!!.pause()

        }


        //        setSeekBar();
        //        setTime(mCurrentPosition, mDuration);
    }

    private fun setTime(currentPosition: Int, duration: Int) {

        mTotalTime!!.text = ((duration / 1000 / 60 % 60 / 10).toString() + "" + duration / 1000 / 60 % 60 % 10 + ":"
                + mDuration / 1000 % 60 / 10 + "" + mDuration / 1000 % 60 % 10)
        mCurrentTime!!.text = ((currentPosition / 1000 / 60 % 60 / 10).toString() + "" + currentPosition / 1000 / 60 % 60 % 10 + ":"
                + currentPosition / 1000 % 60 / 10 + "" + currentPosition / 1000 % 60 % 10)
    }


    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.setDisplay(surfaceHolder)//视频在SurfaceView上面展示  绑定
        }
        MedaplayProgess().start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {

    }


    private inner class MyRecever : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                val pross = intent.getIntExtra("pross", -1)
                mSeekBar!!.progress = pross//为seekbar设置进度

                setTime(pross, mDuration)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myRecever)
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()//释放资源
            mMediaPlayer = null
        }

    }

    private inner class MedaplayProgess : Thread() {
        override fun run() {
            super.run()

            val intent = Intent()
            intent.action = "song"
            while (flage) {
                val currentPosition = mMediaPlayer!!.currentPosition//获取当前进度

                intent.putExtra("pross", currentPosition)
                if (currentPosition == mMediaPlayer!!.duration) {
                    flage = false
                }
                sendBroadcast(intent)
            }

        }
    }

    companion object {

        val TAG = "Huang"
    }
}
