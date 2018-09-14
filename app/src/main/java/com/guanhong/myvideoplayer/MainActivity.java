package com.guanhong.myvideoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String TAG = "Huang";

    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";

    private SurfaceView mSurfaceView;//展示 视频的
    private SurfaceHolder mHolder;//
    private SeekBar mSeekBar;
    private MediaPlayer mMediaPlayer;//播放音视频的
    private boolean flage = true;
    private MyRecever myRecever;

//    private VideoView mVideoView;

//    private MediaPlayer mMediaPlayer;
//    private MediaController mMediaController;

    private Button mButtonVolume;
    private Button mButtonRewind;
    private Button mButtonPlay;
    private Button mButtonForward;
    private Button mButtonFullScreen;

    private TextView mCurrentTime;
    private TextView mTotalTime;

    private int mCurrentPosition = 0;
    private int mDuration;

    private Handler mHandler;
    private Runnable mRunnable;

    private LinearLayout mLinearLayout;

    private boolean isMute = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {


        mButtonVolume = findViewById(R.id.btn_volume);
        mButtonRewind = findViewById(R.id.btn_rewind);
        mButtonPlay = findViewById(R.id.btn_play);
        mButtonForward = findViewById(R.id.btn_forward);
        mButtonFullScreen = findViewById(R.id.btn_fullscreen);

        mCurrentTime = findViewById(R.id.textview_current_time);
        mTotalTime = findViewById(R.id.textview_total_time);

        mSeekBar = findViewById(R.id.seek_bar);

        mLinearLayout = findViewById(R.id.ll);

        mHandler = new Handler();

        mSurfaceView = findViewById(R.id.surfaceview);
        mHolder = mSurfaceView.getHolder();
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(mVideoUrl));
//        mVideoView = findViewById(R.id.videoview);

        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mDuration = mMediaPlayer.getDuration();
        mSeekBar.setMax(mDuration);
        mSeekBar.setProgress(0);

        myRecever = new MyRecever();
        IntentFilter filter = new IntentFilter();
        filter.addAction("song");
        registerReceiver(myRecever, filter);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mMediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.start();

            }
        });


        mButtonPlay.setOnClickListener(this);
        mButtonVolume.setOnClickListener(this);
        mButtonRewind.setOnClickListener(this);
        mButtonForward.setOnClickListener(this);
        mButtonFullScreen.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:

                playOrPause();

                break;
            case R.id.btn_forward:

                videoForward();

                break;
            case R.id.btn_rewind:

                videoRewind();

                break;
            case R.id.btn_volume:

                volumeMute();

                break;
            case R.id.btn_fullscreen:

                videoFullScreen();

                break;


        }

    }

    private void videoFullScreen() {
        Log.d(TAG, "  fullscreen");

    }

    private void volumeMute() {


        if (isMute) {

            Log.d(TAG, "  mute");

            mButtonVolume.setBackgroundResource(R.drawable.volume_open);
            isMute = false;

            mMediaPlayer.setVolume(0f,0f);

        } else {

            Log.d(TAG, "  open");

            mButtonVolume.setBackgroundResource(R.drawable.volume_off);
            isMute = true;

            mMediaPlayer.setVolume(1000f,1000f);

        }

    }

    private void videoForward() {
        Log.d(TAG, "  videoForward mCurrentPosition = " +mCurrentPosition);

        mSeekBar.setProgress(mCurrentPosition + 1500);
    }

    private void videoRewind() {
        Log.d(TAG, "  videoRewind mCurrentPosition = " +mCurrentPosition);

        mSeekBar.setProgress(mCurrentPosition - 1500);


    }

    private void playOrPause() {


        if (!mMediaPlayer.isPlaying() && mMediaPlayer != null) {

            Log.d(TAG, "  playing");

            mButtonPlay.setBackgroundResource(R.drawable.pause);
            mMediaPlayer.start();

            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            mDuration = mMediaPlayer.getDuration();
            Log.d(TAG, "  currentPosition = " + mCurrentPosition + "  mDuration = " + mDuration);


        }
        else if (mMediaPlayer.isPlaying() && mMediaPlayer != null) {
            Log.d(TAG, "  pause");
            mButtonPlay.setBackgroundResource(R.drawable.play_arrow);
            mMediaPlayer.pause();

        }


//        setSeekBar();
//        setTime(mCurrentPosition, mDuration);
    }

    private void setTime(int currentPosition, int duration) {

        mTotalTime.setText((duration / 1000) / 60 % 60 / 10 + "" + (duration / 1000) / 60 % 60 % 10 + ":"
                + (mDuration / 1000) % 60 / 10 + "" + (mDuration / 1000) % 60 % 10);
        mCurrentTime.setText((currentPosition / 1000) / 60 % 60 / 10 + "" + (currentPosition / 1000) / 60 % 60 % 10 + ":"
                + (currentPosition / 1000) % 60 / 10 + "" + (currentPosition / 1000) % 60 % 10);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mMediaPlayer !=null){
            mMediaPlayer.setDisplay(surfaceHolder);//视频在SurfaceView上面展示  绑定
        }
        new MedaplayProgess().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }


    private class MyRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int pross = intent.getIntExtra("pross", -1);
                mSeekBar.setProgress(pross);//为seekbar设置进度

                setTime(pross, mDuration);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myRecever);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();//释放资源
            mMediaPlayer = null;
        }

    }

    private class MedaplayProgess extends Thread{
        @Override
        public void run() {
            super.run();

            Intent intent = new Intent();
            intent.setAction("song");
            while (flage){
                int currentPosition = mMediaPlayer.getCurrentPosition();//获取当前进度

                intent.putExtra("pross",currentPosition);
                if(currentPosition == mMediaPlayer.getDuration()){
                    flage= false;
                }
                sendBroadcast(intent);
            }

        }
    }
}
