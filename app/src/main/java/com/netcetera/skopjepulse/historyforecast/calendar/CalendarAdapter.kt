package com.netcetera.skopjepulse.historyforecast.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.historyforecast.CalendarUtils.getWholeMonth
import kotlinx.android.synthetic.main.calendar_slot.view.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class CalendarAdapter(
  val context: Context,
  private val items: ArrayList<CalendarItemsDataModel>,
  val date: LocalDate,
  val values: List<CalendarValuesDataModel>?,
  val todayValue: Int?
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

  companion object {
    var CURRENT_MONTH = getWholeMonth()
    var DATE_INPUT: LocalDate? = null
  }

  var onItemClick: ((String) -> Unit)? = null

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

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayOfMonth = itemView.dayTextView
    val slotContainer = itemView.slotConstraintLayout

    val currentMonthName = CURRENT_MONTH.month.name
    val currentYear = CURRENT_MONTH.year

    val newMonth = DATE_INPUT?.month?.name
    val newMonthValue = DATE_INPUT?.month?.value
    val newYear = DATE_INPUT?.year

    val todayDate = LocalDate.now().dayOfMonth

    @SuppressLint("ResourceType")
    fun bind() {
      val newList = mutableListOf<CalendarValuesDataModel>()
      val position = items[adapterPosition]

      for (i in 0 until position.startDayOfMonth) {
        newList.add(CalendarValuesDataModel(null,null))
      }
      if (values != null) {
        for (element in values) {
          newList.add(element)
        }
      }
      newList.toList()

      if (adapterPosition >= position.startDayOfMonth) {
        slotContainer.setOnClickListener {
          val date = "${position.day}/${newMonthValue}/$newYear"
          onItemClick?.invoke(date)
        }

        dayOfMonth.text = position.day.toString()
        dayOfMonth.setTextColor(Color.BLACK)

        slotContainer.setBackgroundResource(R.drawable.calendar_circle)

        if (currentMonthName == newMonth!! && currentYear == newYear) {
          if (position.day > todayDate) {
            dayOfMonth.setTextColor(Color.GRAY)
            slotContainer.setBackgroundResource(0)
            slotContainer.alpha = 0.6F
            slotContainer.isClickable = false
          }
          if (position.day == todayDate) {
            if (todayValue != null) {
              slotContainer.setBackgroundColor(todayValue)
              slotContainer.setBackgroundResource(0)
              dayOfMonth.setTextColor(Color.WHITE)
              dayOfMonth.background.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                  todayValue, BlendModeCompat.SRC_ATOP
                )
            }
          }
        }

        val systemTimeZone: ZoneId = ZoneId.systemDefault()
          for (i in newList.indices) {
            if (adapterPosition == i) {
            val color = newList[adapterPosition].sensorValueColor
            val dom = newList[adapterPosition].averageWeeklyDataModel?.stamp
            val stamp = dom?.toInstant()?.atZone(systemTimeZone)
            val stampDay = stamp?.dayOfMonth
            val stampMonth = stamp?.month?.name
            val stampYear = stamp?.year
            val domInputs = items[i].day
            if (stampMonth == newMonth && stampYear == newYear) {
              slotContainer.setOnClickListener {
                val date = "${position.day}/${newMonthValue}/$newYear"
                onItemClick?.invoke(date)
              }
              if (stampDay == domInputs) {
                color?.let {
                  slotContainer.setBackgroundColor(it.legendColor)
                  slotContainer.setBackgroundResource(R.drawable.calendar_circle)
                  dayOfMonth.setTextColor(it.legendColor)
                  slotContainer.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                      color.legendColor,
                      BlendModeCompat.SRC_ATOP
                    )
                }
              }
            }
          }
        }
      }
    }
  }
}