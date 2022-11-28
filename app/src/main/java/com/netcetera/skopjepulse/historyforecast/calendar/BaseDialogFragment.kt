package com.netcetera.skopjepulse.historyforecast.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.netcetera.skopjepulse.base.viewModel.BaseViewModel
import com.squareup.leakcanary.RefWatcher
import kotlinx.android.synthetic.main.simple_error_layout.*
import org.koin.android.ext.android.inject

abstract class BaseDialogFragment<out T : BaseViewModel> : DialogFragment() {
  private val refWatcher : RefWatcher by inject()
  abstract val viewModel: T

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    errorView?.let { errorViewTextView ->
      viewModel.errorMessage.observe(viewLifecycleOwner) {
        errorViewTextView.text = it
        if (it?.isNotBlank() == true) {
          errorViewTextView.visibility = View.VISIBLE
        } else {
          errorViewTextView.visibility = View.GONE
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    refWatcher.watch(this)
  }
}