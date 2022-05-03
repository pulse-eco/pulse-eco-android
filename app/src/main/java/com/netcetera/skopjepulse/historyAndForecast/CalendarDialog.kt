package com.netcetera.skopjepulse.historyAndForecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_dialog.*
import kotlinx.android.synthetic.main.calendar_dialog.view.*
import kotlinx.android.synthetic.main.calendar_dialog.view.calendarPicker

class CalendarDialog:  DialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val rootView = inflater.inflate(R.layout.calendar_dialog, container)
    val month = rootView.monthPicker
    val day = rootView.calendarPicker
    val year = rootView.yearPicker

    day.setOnClickListener {
      calendarViewPicker.isVisible = true
      valuePickerMonth.isVisible = false
      valuePickerYear.isVisible = false
    }


    month.setOnClickListener {

      calendarViewPicker.isVisible = false
      valuePickerMonth.isVisible = true
      valuePickerYear.isVisible = false


      val values = arrayOf("January", "February", "March", "April", "May", "June", "July", "August","September","October","November","December")
      valuePickerMonth.minValue = 0
      valuePickerMonth.maxValue = values.size - 1
      valuePickerMonth.displayedValues = values
      valuePickerMonth.wrapSelectorWheel = true

    }

    year.setOnClickListener {
      calendarViewPicker.isVisible = false
      valuePickerMonth.isVisible = false
      valuePickerYear.isVisible = true

      valuePickerYear.minValue = 2000
      valuePickerYear.maxValue = 2022
      valuePickerYear.value= 2022
      valuePickerYear.wrapSelectorWheel = true

    }

    return rootView
  }
}