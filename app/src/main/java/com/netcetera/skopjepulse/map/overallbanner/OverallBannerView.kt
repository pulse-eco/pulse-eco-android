package com.netcetera.skopjepulse.map.overallbanner

import android.view.ViewGroup.LayoutParams
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.overall_banner_layout.view.description
import kotlinx.android.synthetic.main.overall_banner_layout.view.expandIcon
import kotlinx.android.synthetic.main.overall_banner_layout.view.legendView
import kotlinx.android.synthetic.main.overall_banner_layout.view.title
import kotlinx.android.synthetic.main.overall_banner_layout.view.value
import kotlinx.android.synthetic.main.overall_banner_layout.view.valueUnit

typealias ToggleListener = (isOpen : Boolean) -> Unit

class OverallBannerView(private val overallBannerView : CardView) : Observer<OverallBannerData?> {
  private var listener : ToggleListener? = null

  init {
    overallBannerView.setOnClickListener {
      val isOpen = overallBannerView.description.isVisible
      overallBannerView.updateLayoutParams {
        width = if (isOpen) LayoutParams.WRAP_CONTENT else LayoutParams.MATCH_PARENT
        height = LayoutParams.WRAP_CONTENT
      }
      overallBannerView.description.isVisible = !isOpen
      overallBannerView.legendView.isVisible = !isOpen
      overallBannerView.expandIcon.setImageResource(if (isOpen) R.drawable.ic_overall_unfold else R.drawable.ic_overall_fold)
      listener?.invoke(!isOpen)
    }
  }

  override fun onChanged(data: OverallBannerData?) {
    if (data == null) {
      overallBannerView.isVisible = false
    } else {
      overallBannerView.isVisible = true
      overallBannerView.apply {
        title.text = data.title
        value.text = data.value
        valueUnit.text = data.valueUnit
        description.text = data.description
        overallBannerView.setCardBackgroundColor(data.backgroundColor)
        legendView.legend = data.legend
      }
    }
  }

  fun onToggled(listener: ToggleListener) {
    this.listener = listener
  }
}