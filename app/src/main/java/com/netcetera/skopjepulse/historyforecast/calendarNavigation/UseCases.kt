package com.netcetera.skopjepulse.historyforecast.calendarNavigation

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.netcetera.skopjepulse.historyforecast.CalendarUtils
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarAdapter
import com.netcetera.skopjepulse.map.MapFragment
import java.util.*

object UseCases {

  fun showCalendarHideRecyclerView(
    context: Context,
    calendarPreviousArrow: TextView,
    calendarNextArrow: TextView,
    calendarHeader: TableLayout,
    calendarYearPicker: TextView,
    calendarMonthYearText: TextView,
    recyclerView: RecyclerView,
    monthYearPickerRecyclerView: RecyclerView
  ) {
    calendarPreviousArrow.visibility = View.VISIBLE
    calendarNextArrow.visibility = View.VISIBLE
    calendarHeader.visibility = View.VISIBLE
    calendarYearPicker.visibility = View.GONE
    calendarMonthYearText.visibility = View.VISIBLE
//    val month = CalendarAdapter.DATE_INPUT?.month.toString().capitalize(Locale.getDefault())
//    val newInputs = "${CalendarUtils.getMonthInAppLanguage(context, month)} ${CalendarAdapter.DATE_INPUT?.year}"
//    calendarMonthYearText.text = newInputs

    recyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
    calendarYearPicker.visibility = View.GONE
  }

  fun calendarCancelButton(alertDialog: AlertDialog, calendarDialogCancelButton: TextView) {
    calendarDialogCancelButton.setOnClickListener {
      alertDialog.dismiss()
    }
  }

  fun calendarViewSetView(
    calendarMonthYearText: TextView,
    calendarYearPicker: TextView,
    calendarPreviousArrow: TextView,
    calendarNextArrow: TextView,
    calendarHeader: TableLayout,
    recyclerView: RecyclerView,
    monthYearPickerRecyclerView: RecyclerView,
    alertDialog: AlertDialog,
    calendarDialogCancelButton: TextView
  ) {
    calendarMonthYearText.visibility = View.VISIBLE
    calendarYearPicker.visibility = View.GONE
    calendarPreviousArrow.visibility = View.VISIBLE
    calendarNextArrow.visibility = View.VISIBLE
    calendarHeader.visibility = View.VISIBLE
    recyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
    calendarCancelButton(alertDialog,calendarDialogCancelButton)
  }

  fun monthRecyclerViewSetView(
    calendarMonthYearText: TextView,
    calendarYearPicker: TextView,
    calendarPreviousArrow: TextView,
    calendarNextArrow: TextView,
    calendarHeader: TableLayout,
    recyclerView: RecyclerView,
    monthYearPickerRecyclerView: RecyclerView
  ) {
    calendarMonthYearText.visibility = View.GONE
    calendarYearPicker.visibility = View.VISIBLE
    calendarYearPicker.text = "${MapFragment.CHOSEN_YEAR ?: CalendarAdapter.DATE_INPUT?.year}"
    calendarPreviousArrow.visibility = View.GONE
    calendarNextArrow.visibility = View.GONE
    calendarHeader.visibility = View.GONE
    recyclerView.visibility = View.GONE
    monthYearPickerRecyclerView.visibility = View.VISIBLE
  }

  fun yearRecyclerViewSetView(
    calendarMonthYearText: TextView, calendarYearPicker: TextView,
    calendarPreviousArrow: TextView, calendarNextArrow: TextView, calendarHeader: TableLayout,
    recyclerView: RecyclerView, monthYearPickerRecyclerView: RecyclerView
  ) {
    calendarYearPicker.visibility = View.GONE
    calendarMonthYearText.visibility = View.GONE
    calendarPreviousArrow.visibility = View.GONE
    calendarNextArrow.visibility = View.GONE
    calendarHeader.visibility = View.GONE
    recyclerView.visibility = View.GONE
    monthYearPickerRecyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
  }

  fun monthsToDays(
    context: Context,
    calendarMonthYearText: TextView,
    calendarPreviousArrow: TextView,
    calendarNextArrow: TextView,
    calendarHeader: TableLayout,
    recyclerView: RecyclerView,
    monthYearPickerRecyclerView: RecyclerView
  ) {
//    val month = CalendarAdapter.DATE_INPUT?.month.toString().capitalize(Locale.getDefault())
//    val newInputs = "${CalendarUtils.getMonthInAppLanguage(context, month)} ${CalendarAdapter.DATE_INPUT?.year}"
    calendarMonthYearText.visibility = View.VISIBLE
//    calendarMonthYearText.text = newInputs
    calendarPreviousArrow.visibility = View.VISIBLE
    calendarNextArrow.visibility = View.VISIBLE
    calendarHeader.visibility = View.VISIBLE
    recyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
  }

}