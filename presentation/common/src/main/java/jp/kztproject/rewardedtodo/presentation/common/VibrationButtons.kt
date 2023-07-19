package jp.kztproject.rewardedtodo.presentation.common

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
