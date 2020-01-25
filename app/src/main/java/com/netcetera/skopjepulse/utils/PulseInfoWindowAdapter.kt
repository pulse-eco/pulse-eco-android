package com.netcetera.skopjepulse.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.marker_info_window_layout.view.markerTitle

class PulseInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
  private val markerView: View by lazy {
    LayoutInflater.from(context).inflate(R.layout.marker_info_window_layout, null, false)
  }
  private val markerTitleView: TextView by lazy { markerView.markerTitle }

  override fun getInfoContents(marker: Marker?): View? = null

  override fun getInfoWindow(marker: Marker?): View? {
    if (marker != null) {
      markerTitleView.text = marker.title
      return markerView
    }
    return null
  }
}