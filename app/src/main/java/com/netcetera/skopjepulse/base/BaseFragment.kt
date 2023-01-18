package com.netcetera.skopjepulse.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.netcetera.skopjepulse.base.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.simple_error_layout.*


abstract class BaseFragment<out T : BaseViewModel> : Fragment() {

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
}