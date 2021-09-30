package com.netcetera.skopjepulse.favouritesensors

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.extensions.toast


typealias ConfirmFavouriteSensors = (newSelectedFavourites: List<Sensor>, unselectedFavourites: List<Sensor>) -> Unit

fun Fragment.showFavouriteSensorsPicker(favouriteSensors: List<Sensor>, otherSensors: List<Sensor>,
                                        confirmFavouriteSensors: ConfirmFavouriteSensors) {
  val allSensors = favouriteSensors.map { favouriteSensor ->
    favouriteSensor to true
  }.union(otherSensors.map { otherSensor ->
    otherSensor to false
  }).toList().toMutableList()

  val selectedItems = allSensors.map { it.second }.toBooleanArray()
  AlertDialog.Builder(requireContext())
    .setTitle(R.string.tooltip_maximum_sensors_reached)
    .setMultiChoiceItems(
      allSensors.map { it.first.description }.toTypedArray(),
      selectedItems) { dialog, which, isChecked ->
      if (isChecked) {
        val canAdd = allSensors.count { it.second } < 5
        if (canAdd) {
          allSensors[which] = allSensors[which].copy(second = isChecked)
        } else {
          toast(R.string.sensor_select_dialog_warning_when_more_selected)
          selectedItems[which] = false
          (dialog as AlertDialog).listView.setItemChecked(which, false)
        }
      } else {
        allSensors[which] = allSensors[which].copy(second = false)
      }
    }
    .setNegativeButton(R.string.cancel) { _, _ ->
      // no-op
    }
    .setPositiveButton(R.string.ok) { _, _ ->
      val favourites = allSensors.filter { it.second }.map { it.first }
      val unselectedFavourites = favouriteSensors.filter { !favourites.contains(it) }
      val newSelectedFavourites = otherSensors.filter { favourites.contains(it) }
      confirmFavouriteSensors.invoke(newSelectedFavourites, unselectedFavourites)
    }

    .show()
}
