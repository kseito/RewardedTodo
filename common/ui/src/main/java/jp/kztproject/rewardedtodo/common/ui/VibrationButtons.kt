package jp.kztproject.rewardedtodo.common.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager

@SuppressLint("MissingPermission")
fun Context.vibrate() {
    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val vibratorEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
    val combinedVibration = CombinedVibration.createParallel(vibratorEffect)
    vibratorManager.vibrate(combinedVibration)
}

@SuppressLint("MissingPermission")
fun Context.vibrateRichly() {
    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val timings: LongArray = longArrayOf(
        0, 40, 20, 30, 25, 60, 30, 70, 25, 80, 20, 60, 15,
        40, 10, 30, 10, 20, 10, 10,
    )
    val amplitudes: IntArray = intArrayOf(
        0, 255, 180, 200, 160, 220, 140, 180, 120, 150, 100,
        120, 90, 80, 70, 60, 50, 40, 30, 20,
    )
    val newVibration = VibrationEffect.createWaveform(timings, amplitudes, -1)
    val combinedVibration = CombinedVibration.createParallel(newVibration)
    vibratorManager.vibrate(combinedVibration)
}
