package com.netcetera.skopjepulse.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import com.airbnb.lottie.LottieAnimationView

const val PULSE_LOGO_ANIMATION_SPEED = 1.4f

fun LottieAnimationView.playIfNotPlaying() {
  if (!isAnimating) {
    playAnimation()
  }
}

fun LottieAnimationView.stopWhenDone(whenDone: () -> Unit) {
  if (isAnimating) {
    addAnimatorListener(object : AnimatorListenerAdapter() {
      override fun onAnimationRepeat(animation: Animator) {
        removeAnimatorListener(this)
        cancelAnimation()
        whenDone.invoke()
      }

      override fun onAnimationEnd(animation: Animator) {
        removeAnimatorListener(this)
      }

      override fun onAnimationCancel(animation: Animator) {
        removeAnimatorListener(this)
      }
    })
  }
}