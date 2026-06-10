package com.karla.lab13_animaciones.ui.game

import android.media.AudioManager
import android.media.ToneGenerator
import kotlin.concurrent.thread

object SoundManager {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun playBite() {
        thread {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        }
    }

    fun playGuilt() {
        thread {
            toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
        }
    }

    fun playCombo() {
        thread {
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 100)
            Thread.sleep(100)
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_D, 150)
        }
    }

    fun playGameOver() {
        thread {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 400)
        }
    }
}