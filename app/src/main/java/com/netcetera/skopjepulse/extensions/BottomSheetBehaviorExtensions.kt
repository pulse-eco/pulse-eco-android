package com.netcetera.skopjepulse.extensions

import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlin.math.abs

fun BottomSheetBehavior<out View>.onStateChange(callbacks: Array<BottomSheetCallback>) {
  addBottomSheetCallback(object : BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
      callbacks.forEach { it.onSlide(bottomSheet, slideOffset) }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      callbacks.forEach { it.onStateChanged(bottomSheet, newState) }
    }

  })
}

fun dimOnExpand(what : View) : BottomSheetCallback {
  return object : BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
      what.alpha = abs(slideOffset * 0.45f)
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      what.isVisible = newState != BottomSheetBehavior.STATE_COLLAPSED
    }
  }
}

fun onExpanded(action: () -> Unit) : BottomSheetCallback {
  return object : BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
      // no-op
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      if (newState == STATE_EXPANDED) action.invoke()
    }
  }
}

fun BottomSheetBehavior<out View>.toggle() {
  if (state == BottomSheetBehavior.STATE_COLLAPSED) {
    state = STATE_EXPANDED
  } else if (state == STATE_EXPANDED) {
    state = BottomSheetBehavior.STATE_COLLAPSED
  }

}