package com.netcetera.skopjepulse.historyforecast.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_slot.view.*
import java.lang.String
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class CalendarAdapter(
  val context: Context,
  private val items: ArrayList<CalendarTry>,
  val startOfMonth: Int,
  val date : LocalDate,
  val values:List<CalendarItemDataModel>
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
companion object{
  var DATE_INPUT: LocalDate? = null
}

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayOfMonth = itemView.dayTextView
    val slotContainer = itemView.slotConstraintLayout
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val TODAYdate = LocalDate.parse("01/06/2022", formatter)
    val todayMonth = TODAYdate.month.name
    val todayYear = TODAYdate.year.toString()
    val dateMonth = DATE_INPUT?.month?.name
    val dateYear = DATE_INPUT?.year.toString()
    val today = LocalDate.now()
    val todayDate = today.dayOfMonth
    fun bind() {
      if (adapterPosition > startOfMonth || adapterPosition == startOfMonth) {
        dayOfMonth.text = items[adapterPosition].day.toString()
        dayOfMonth.setTextColor(Color.BLACK)

         slotContainer.setBackgroundResource(R.drawable.calendar_circle)

        if (todayMonth == dateMonth!! && todayYear == dateYear) {
          if (items[adapterPosition].day > todayDate) {
            dayOfMonth.text = items[adapterPosition].day.toString()
            dayOfMonth.setTextColor(Color.GRAY)
            slotContainer.setBackgroundResource(0)
            slotContainer.alpha = 0.6F
          }
          if (items[adapterPosition].day == todayDate) {
            dayOfMonth.text = items[adapterPosition].day.toString()
            dayOfMonth.setTextColor(Color.WHITE)
            slotContainer.setBackgroundResource(R.drawable.calendar_circle_clicked)
          }
        }

        val systemTimeZone: ZoneId = ZoneId.systemDefault()
        if (adapterPosition <= values.size + 1) {
          for (i in values.indices) {
            val color = values[i].sensorValueColor
            val dom = values[i].averageWeeklyDataModel?.stamp
            val stamp = dom?.toInstant()?.atZone(systemTimeZone)
            val stampDay = stamp?.dayOfMonth
            val mutableList = items.toMutableList()
            mutableList.removeAll { it.day == 0 }
            val newlist = mutableList.toTypedArray()
            val domInputs = newlist[i].day

            val hexColor = String.format("#%06X", 0xFFFFFF and color?.legendColor!!)

            if (stampDay == domInputs) {
              color.let {
                slotContainer.setBackgroundColor(it.legendColor)
              }
            }
          }
        }
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
    val view = LayoutInflater.from(context).inflate(R.layout.calendar_slot, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: CalendarAdapter.ViewHolder, position: Int) {
    holder.bind()
  }

  override fun getItemCount(): Int {
    return items.size
  }

}