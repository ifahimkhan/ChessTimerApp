package com.fahim.chesstimer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.util.Log

class SoundManager(context: Context) {

    private var soundPool: SoundPool?
    private var tapSoundId: Int = 0
    private var toneGenerator: ToneGenerator? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        tapSoundId = soundPool!!.load(context, R.raw.wood_tap, 1)

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (e: RuntimeException) {
            Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playTap() {
        soundPool?.play(tapSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playGameOver() {
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        toneGenerator?.release()
        toneGenerator = null
    }
}
