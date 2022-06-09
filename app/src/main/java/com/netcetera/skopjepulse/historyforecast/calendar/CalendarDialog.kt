package com.netcetera.skopjepulse.historyforecast.calendar

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_dialog.*
import java.time.ZoneOffset


import java.util.*


class CalendarDialog : DialogFragment() {

  companion object {
    var latestDateSelected: Long? = null
    var MONTH: Int? = 0
    var YEAR: Int? = 0
  }

  @RequiresApi(Build.VERSION_CODES.O)
  var zoneOffset: ZoneOffset = ZoneOffset.ofHours(0)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.calendar_dialog, container)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    val arrayOfMonths = arrayOf(
      requireContext().getString(R.string.january),
      requireContext().getString(R.string.february),
      requireContext().getString(R.string.march),
      requireContext().getString(R.string.april),
      requireContext().getString(R.string.may),
      requireContext().getString(R.string.june),
      requireContext().getString(R.string.july),
      requireContext().getString(R.string.august),
      requireContext().getString(R.string.september),
      requireContext().getString(R.string.october),
      requireContext().getString(R.string.november),
      requireContext().getString(R.string.december)
    )

    val arrayOfYear = arrayOf("2017", "2018", "2019", "2020", "2021", "2022")

    val calendar = Calendar.getInstance()

    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }

    val recyclerView = calendarRecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.visibility = View.INVISIBLE
    recyclerView.adapter = CalendarAdapter(requireContext(), zoneOffset)


    calendarMonthYearText.setOnClickListener {
      val currentYear = calendar.get(Calendar.YEAR).toString()
      calendarDialogOkButton.visibility = View.GONE
      calendarLine.visibility = View.GONE
      calendarMonthYearText.visibility = View.GONE
      calendarYearPicker.visibility = View.VISIBLE
      calendarYearPicker.text = currentYear
      calendarPreviousArrow.visibility = View.GONE
      calendarNextArrow.visibility = View.GONE
      calendarHeader.visibility = View.GONE
      calendarRecyclerView.visibility = View.GONE
      monthYearPickerRecyclerView.visibility = View.VISIBLE
      val monthAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfMonths)
      monthYearPickerRecyclerView.layoutManager =
        StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
      monthYearPickerRecyclerView.adapter = monthAdapter
      monthAdapter.onItemClick = {
        calendarLine.visibility = View.VISIBLE
        calendarYearPicker.visibility = View.GONE
        calendarMonthYearText.visibility = View.VISIBLE
        calendarPreviousArrow.visibility = View.VISIBLE
        calendarNextArrow.visibility = View.VISIBLE
        calendarHeader.visibility = View.VISIBLE
        calendarRecyclerView.visibility = View.VISIBLE
        monthYearPickerRecyclerView.visibility = View.GONE
      }

      //Year Picker
      calendarYearPicker.setOnClickListener {
        calendarYearPicker.visibility = View.GONE
        calendarMonthYearText.visibility = View.GONE
        calendarPreviousArrow.visibility = View.GONE
        calendarNextArrow.visibility = View.GONE
        calendarHeader.visibility = View.GONE
        calendarRecyclerView.visibility = View.GONE
        monthYearPickerRecyclerView.visibility = View.VISIBLE
        monthYearPickerRecyclerView.layoutManager =
          StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        val yearAdapter = CalendarMonthYearPickerAdapter(requireContext(), arrayOfYear)
        monthYearPickerRecyclerView.adapter = yearAdapter
        yearAdapter.onItemClick = {
          calendarDialogOkButton.visibility = View.VISIBLE
          calendarLine.visibility = View.VISIBLE
          calendarYearPicker.visibility = View.GONE
          calendarMonthYearText.visibility = View.VISIBLE
          calendarPreviousArrow.visibility = View.VISIBLE
          calendarNextArrow.visibility = View.VISIBLE
          calendarHeader.visibility = View.VISIBLE
          calendarRecyclerView.visibility = View.VISIBLE
          monthYearPickerRecyclerView.visibility = View.GONE
        }
      }
    }

    calendarDialogCancelButton.setOnClickListener {
      dismiss()
    }

  }

  private fun getMonthNumber(month: String): Int {
    when (month) {
      getString(R.string.january) -> {
        return 0
      }
      getString(R.string.february) -> {
        return 1
      }
      getString(R.string.march) -> {
        return 2
      }
      getString(R.string.april) -> {
        return 3
      }
      getString(R.string.may) -> {
        return 4
      }
      getString(R.string.june) -> {
        return 5
      }
      getString(R.string.july) -> {
        return 6
      }
      getString(R.string.august) -> {
        return 7
      }
      getString(R.string.september) -> {
        return 8
      }
      getString(R.string.october) -> {
        return 9
      }
      getString(R.string.november) -> {
        return 10
      }
      getString(R.string.december) -> {
        return 11
      }
      else -> {
        return Calendar.MONTH
      }
    }
  }
}


