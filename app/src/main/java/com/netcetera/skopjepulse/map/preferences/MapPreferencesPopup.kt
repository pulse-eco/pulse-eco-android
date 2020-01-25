package com.netcetera.skopjepulse.map.preferences


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.map.preferences.DataVisualization.MARKERS
import com.netcetera.skopjepulse.map.preferences.DataVisualization.VORONOI
import com.netcetera.skopjepulse.map.preferences.MapType.DEFAULT
import com.netcetera.skopjepulse.map.preferences.MapType.SATELLITE
import com.netcetera.skopjepulse.map.preferences.MapType.TERRAIN
import kotlinx.android.synthetic.main.map_layers_picker_dilog.view.dataVisualizationItemGeographic
import kotlinx.android.synthetic.main.map_layers_picker_dilog.view.dataVisualizationItemSensorMarkers
import kotlinx.android.synthetic.main.map_layers_picker_dilog.view.mapTypeRadioGroup

typealias PreferencesChangeListener = (Preferences) -> Unit

class MapPreferencesPopup(context: Context) : Observer<Preferences> {
  private val pickerView: ViewGroup
  private val popupWindow: PopupWindow
  private var listener: PreferencesChangeListener? = null

  init {
    @SuppressLint("InflateParams")
    pickerView = LayoutInflater.from(context).inflate(
        R.layout.map_layers_picker_dilog, null) as ViewGroup



    popupWindow = PopupWindow(pickerView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
        true)
  }

  override fun onChanged(newPreferences: Preferences?) {
    if (newPreferences != null) {
      // Remove old listeners
      pickerView.mapTypeRadioGroup.setOnCheckedChangeListener(null)
      pickerView.dataVisualizationItemSensorMarkers.setOnCheckedChangeListener(null)
      pickerView.dataVisualizationItemGeographic.setOnCheckedChangeListener(null)

      // Update UI values
      pickerView.mapTypeRadioGroup.check(
          when (newPreferences.mapType) {
            DEFAULT -> R.id.mapTypeRadioItemDefault
            SATELLITE -> R.id.mapTypeRadioItemSatellite
            TERRAIN -> R.id.mapTypeRadioItemTerrain
          })
      pickerView.dataVisualizationItemSensorMarkers.isChecked =
          newPreferences.dataVisualization.contains(MARKERS)
      pickerView.dataVisualizationItemGeographic.isChecked =
          newPreferences.dataVisualization.contains(VORONOI)

      // New listeners registration
      pickerView.mapTypeRadioGroup.setOnCheckedChangeListener { _, mapTypeId ->
        listener?.invoke(newPreferences.copy(mapType = when (mapTypeId) {
          R.id.mapTypeRadioItemSatellite -> SATELLITE
          R.id.mapTypeRadioItemTerrain -> TERRAIN
          else -> DEFAULT
        }))
        dismiss()
      }
      pickerView.dataVisualizationItemSensorMarkers.setOnCheckedChangeListener { _, isChecked ->
        listener?.invoke(newPreferences.copy(
            dataVisualization = newPreferences.dataVisualization.toMutableSet().apply {
              if (isChecked) add(MARKERS) else remove(MARKERS)
            }))
      }
      pickerView.dataVisualizationItemGeographic.setOnCheckedChangeListener { _, isChecked ->
        listener?.invoke(newPreferences.copy(
            dataVisualization = newPreferences.dataVisualization.toMutableSet().apply {
              if (isChecked) add(VORONOI) else remove(VORONOI)
            }))
      }
    } else {
      dismiss()
    }
  }

  fun show(anchor: View) {
    if (!popupWindow.isShowing) popupWindow.showAsDropDown(anchor)
  }

  fun dismiss() {
    popupWindow.dismiss()
  }

  fun toggleShown(anchor: View) {
    if (popupWindow.isShowing) dismiss() else show(anchor)
  }

  fun onPreferenceChange(listener: PreferencesChangeListener) {
    this.listener = listener
  }

}