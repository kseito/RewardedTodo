package jp.kztproject.rewardedtodo.common.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager

@SuppressLint("MissingPermission")
fun Context.vibrate() {
    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val vibratorEffect =  VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
    val combinedVibration = CombinedVibration.createParallel(vibratorEffect)
    vibratorManager.vibrate(combinedVibration)
}

@SuppressLint("MissingPermission")
fun Context.vibrateRichly() {
    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val timings: LongArray = longArrayOf(
        25, 25, 50, 25, 25, 25, 25, 25, 25, 25, 75, 25, 25,
        300, 25, 25, 150, 25, 25, 25
    )
    val amplitudes: IntArray = intArrayOf(
        38, 77, 79, 84, 92, 99, 121, 143, 180, 217, 255, 170, 85,
        0, 85, 170, 255, 170, 85, 0
    )
    val newVibration = VibrationEffect.createWaveform(timings, amplitudes, -1)
    val combinedVibration = CombinedVibration.createParallel(newVibration)
    vibratorManager.vibrate(combinedVibration)
}