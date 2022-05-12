package com.netcetera.skopjepulse.historyAndForecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_dialog.*


class CalendarDialog: DialogFragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.calendar_dialog, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val arrayOfMonths = arrayOf("January","February","March","April","May","June","July","Avgust","September","October","November","December")
    val arrayOfYear = arrayOf("2017","2018","2019","2020","2021","2022")


    monthPicker.setOnClickListener {
        calendarViewPicker.isVisible = false
        monthYearPickerRecyclerView.isVisible = true

        monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        val calendarAdapter = CalendarAdapter(arrayOfMonths)
        monthYearPickerRecyclerView.adapter = calendarAdapter
    }

    yearPicker.setOnClickListener {
      calendarViewPicker.isVisible = false
      monthYearPickerRecyclerView.isVisible = true

      monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
      val calendarAdapter = CalendarAdapter(arrayOfYear)
      monthYearPickerRecyclerView.adapter = calendarAdapter
    }

    calendarDialogOkButton.setOnClickListener {
      val calendar = CalendarDialog()
      calendar.show(parentFragmentManager,"calendar")
    }
  }



}