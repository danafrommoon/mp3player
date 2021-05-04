package com.example.myplayer

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

abstract class MainActivity : AppCompatActivity() {
    var play: ImageView = findViewById(R.id.play)
    var prev:ImageView = findViewById(R.id.prev)
    var next:ImageView = findViewById(R.id.next)
    var imageView:ImageView = findViewById(R.id.imageView)
    var songTitle: TextView = findViewById(R.id.songTitle)
    var mSeekBarTime: SeekBar = findViewById(R.id.seekBarTime)
    var mSeekBarVol:SeekBar = findViewById(R.id.seekBarVol)
    val songs:ArrayList<Int> = ArrayList()
    var currentIndex = 0
    var mMediaPlayer: MediaPlayer =  MediaPlayer.create(applicationContext, songs[currentIndex])
    abstract val mAudioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songs.add(0, R.raw.furelise)
        songs.add(1, R.raw.gluboko)
        songs.add(2, R.raw.makeitright)
        songs.add(3, R.raw.regular)
        songs.add(4, R.raw.saveyourtears)

        val maxV = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val curV = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mSeekBarVol.max = maxV
        mSeekBarVol.progress = curV

        mSeekBarVol.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar:SeekBar, progress:Int, fromUser:Boolean) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar:SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar:SeekBar) {
            }
        })

        play.setOnClickListener {
            mSeekBarTime.max = mMediaPlayer.duration
            if (mMediaPlayer != null && mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
                play.setImageResource(R.drawable.play_foreground)
            } else {
                mMediaPlayer.start()
                play.setImageResource(R.drawable.pause_foreground)
            }
            songNames()
        }

        next.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View) {
                if (mMediaPlayer != null)
                {
                    play.setImageResource(R.drawable.pause_foreground)
                }
                if (currentIndex < songs.size - 1)
                {
                    currentIndex++
                }
                else
                {
                    currentIndex = 0
                }
                if (mMediaPlayer.isPlaying)
                {
                    mMediaPlayer.stop()
                }
                mMediaPlayer = MediaPlayer.create(applicationContext, songs.get(currentIndex))
                mMediaPlayer.start()
                songNames()
            }
        })

        prev.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View) {
                if (mMediaPlayer != null)
                {
                    play.setImageResource(R.drawable.pause_foreground)
                }
                if (currentIndex > 0)
                {
                    currentIndex--
                }
                else
                {
                    currentIndex = songs.size - 1
                }
                if (mMediaPlayer.isPlaying)
                {
                    mMediaPlayer.stop()
                }
                mMediaPlayer = MediaPlayer.create(applicationContext, songs.get(currentIndex))
                mMediaPlayer.start()
                songNames()
            }
        })

    }

    private fun songNames() {
        if (currentIndex === 0)
        {
            songTitle.text = "Fur Elise - Ludwig van Beethoven"
            imageView.setImageResource(R.drawable.furelise)
        }
        if (currentIndex === 1)
        {
            songTitle.text = "Глубоко - Монатик"
            imageView.setImageResource(R.drawable.gluboko)
        }
        if (currentIndex === 2)
        {
            songTitle.text = "Make It Right - BTS"
            imageView.setImageResource(R.drawable.makeitright)
        }
        if (currentIndex === 3)
        {
            songTitle.text = "Regular - NCT"
            imageView.setImageResource(R.drawable.regular)
        }
        if (currentIndex === 4)
        {
            songTitle.text = "Save your tears - The Weeknd"
            imageView.setImageResource(R.drawable.saveyourtears)
        }

        mMediaPlayer.setOnPreparedListener(object:MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp:MediaPlayer) {
                mSeekBarTime.max = mMediaPlayer.duration
                mMediaPlayer.start()
            }
        })

        mSeekBarTime.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar:SeekBar, progress:Int, fromUser:Boolean) {
                if (fromUser)
                {
                    mMediaPlayer.seekTo(progress)
                    mSeekBarTime.progress = progress
                }
            }
            override fun onStartTrackingTouch(seekBar:SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar:SeekBar) {
            }
        })

        var handler: Handler = object:Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg:Message) {
                mSeekBarTime.progress = msg.what
            }
        }

        Thread(object:Runnable {
            override fun run() {
                while (mMediaPlayer != null)
                {
                    try
                    {
                        if (mMediaPlayer.isPlaying)
                        {
                            val message = Message()
                            message.what = mMediaPlayer.currentPosition
                            handler.sendMessage(message)
                            Thread.sleep(1000)
                        }
                    }
                    catch (e:InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }).start()

    }

}