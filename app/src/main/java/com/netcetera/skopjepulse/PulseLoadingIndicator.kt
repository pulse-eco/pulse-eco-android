package com.netcetera.skopjepulse

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.netcetera.skopjepulse.extensions.PULSE_LOGO_ANIMATION_SPEED
import com.netcetera.skopjepulse.extensions.playIfNotPlaying
import com.netcetera.skopjepulse.extensions.stopWhenDone
import kotlinx.android.synthetic.main.map_loading_indicator.view.lottieAnimationView

class PulseLoadingIndicator(private val loadingIndicatorContainer: View) : Observer<Boolean> {
  private val lottieAnimationView: LottieAnimationView = loadingIndicatorContainer.lottieAnimationView

  override fun onChanged(isLoading: Boolean?) {
    if (isLoading == true) {
      if (!loadingIndicatorContainer.isVisible) {
        lottieAnimationView.speed = PULSE_LOGO_ANIMATION_SPEED
        lottieAnimationView.playIfNotPlaying()
        loadingIndicatorContainer.isVisible = true
      }
    } else {
      lottieAnimationView.speed = PULSE_LOGO_ANIMATION_SPEED * 1.5f
      lottieAnimationView.stopWhenDone {
        loadingIndicatorContainer.isVisible = false
      }
    }
  }
}