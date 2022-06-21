package com.netcetera.skopjepulse.historyforecast

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarAdapter
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarDialog
import com.netcetera.skopjepulse.historyforecast.calendar.CalendarItemsDataModel
import com.netcetera.skopjepulse.map.MapFragment
import java.time.LocalDate
import java.util.*

object CalendarUtils {

   fun intValueForDayOfWeek(day: String): Int {
    when (day) {
      "Mon" -> {
        return 0
      }
      "Tue" -> {
        return 1
      }
      "Wed" -> {
        return 2
      }
      "Thu" -> {
        return 3
      }
      "Fri" -> {
        return 4
      }
      "Sat" -> {
        return 5
      }
      else -> {
        return 6
      }
    }
  }

 fun getMonthByLanguage(context: Context,month: String): String {
    when (month) {
      "January" -> {
        return context.getString(R.string.january)
      }
      "February" -> {
        return  context.getString(R.string.february)
      }
      "March" -> {
        return  context.getString(R.string.march)
      }
      "April" -> {
        return  context.getString(R.string.april)
      }
      "May" -> {
        return  context.getString(R.string.may)
      }
      "June" -> {
        return  context.getString(R.string.june)
      }
      "July" -> {
        return  context.getString(R.string.july)
      }
      "August" -> {
        return  context.getString(R.string.august)
      }
      "September" -> {
        return  context.getString(R.string.september)
      }
      "October" -> {
        return  context.getString(R.string.october)
      }
      "November" -> {
        return  context.getString(R.string.november)
      }
      else -> {
        return  context.getString(R.string.december)
      }
    }
  }

  fun getMonthNumber(context: Context,month: String): Int {
    when (month) {
      context.getString(R.string.january) -> {
        return 1
      }
      context.getString(R.string.february) -> {
        return 2
      }
      context.getString(R.string.march) -> {
        return 3
      }
      context.getString(R.string.april) -> {
        return 4
      }
      context.getString(R.string.may) -> {
        return 5
      }
      context.getString(R.string.june) -> {
        return 6
      }
      context.getString(R.string.july) -> {
        return 7
      }
      context.getString(R.string.august) -> {
        return 8
      }
      context.getString(R.string.september) -> {
        return 9
      }
      context.getString(R.string.october) -> {
        return 10
      }
      context.getString(R.string.november) -> {
        return 11
      }
      context.getString(R.string.december) -> {
        return 12
      }
      else -> {
        return Calendar.MONTH
      }
    }
  }


  fun setUpCalendarRecyclerView(calendarRecyclerView: RecyclerView,context: Context,list:ArrayList<CalendarItemsDataModel>, dateInput: LocalDate,alertDialog: AlertDialog){
    val layoutManager = object : GridLayoutManager(context, 7) {
      override fun supportsPredictiveItemAnimations(): Boolean {
        return false
      }
    }
    val recyclerView = calendarRecyclerView
    recyclerView.layoutManager = layoutManager
    val adapter =  CalendarAdapter(context, list, dateInput,MapFragment.calendarValuesResult, MapFragment.bandValueOverallData)
    recyclerView.adapter = adapter
    adapter.onItemClick = {
      val clickedDate  = LocalDate.parse(CalendarAdapter.DATE_CLICKED, CalendarDialog.formatter)
      val fromClickedDate = clickedDate.plusDays(4)
      MapFragment.toDate = fromClickedDate
      MapFragment.fromDate = MapFragment.toDate.minusDays(8)
      alertDialog.dismiss()
    }
    recyclerView.suppressLayout(true)

  }

  fun getFullMonthName(context: Context,short: String): String {
    when (short) {
     context.getString(R.string.january).substring(0,3) -> {
        return context.getString(R.string.january)
      }

      context.getString(R.string.february).substring(0,3) -> {
        return context.getString(R.string.february)
      }

      context.getString(R.string.march).substring(0,3) -> {
        return context.getString(R.string.march)
      }

      context.getString(R.string.april).substring(0,3) -> {
        return context.getString(R.string.april)
      }

      context.getString(R.string.may).substring(0,3) -> {
        return context.getString(R.string.may)
      }

      context.getString(R.string.june).substring(0,3) -> {
        return context.getString(R.string.june)
      }

      context.getString(R.string.july).substring(0,3) -> {
        return context.getString(R.string.july)
      }

      context.getString(R.string.august).substring(0,3) -> {
        return context.getString(R.string.august)
      }

      context.getString(R.string.september).substring(0,3) -> {
        return context.getString(R.string.september)
      }

      context.getString(R.string.october).substring(0,3) -> {
        return context.getString(R.string.october)
      }

      context.getString(R.string.november).substring(0,3) -> {
        return context.getString(R.string.november)
      }
      else -> {
        return context.getString(R.string.december)
      }

    }
  }



fun showCalendarHideRecyclerView(context: Context, calendarPreviousArrow: TextView, calendarNextArrow: TextView,
                                 calendarHeader: TableLayout, calendarYearPicker: TextView, calendarMonthYearText: TextView,
                                 calendarLine: View, recyclerView: RecyclerView, monthYearPickerRecyclerView:RecyclerView) {
    calendarPreviousArrow.visibility = View.VISIBLE
    calendarNextArrow.visibility = View.VISIBLE
    calendarHeader.visibility = View.VISIBLE
    calendarYearPicker.visibility = View.GONE
    calendarMonthYearText.visibility = View.VISIBLE
    val month = CalendarAdapter.DATE_INPUT?.month.toString()
    val monthFirstUpper = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase()
    calendarMonthYearText.text = "${CalendarUtils.getMonthByLanguage(context,monthFirstUpper)} ${CalendarAdapter.DATE_INPUT?.year}"
    calendarLine.visibility = View.VISIBLE
    recyclerView.visibility = View.VISIBLE
    monthYearPickerRecyclerView.visibility = View.GONE
    calendarYearPicker.visibility = View.GONE
  }
}