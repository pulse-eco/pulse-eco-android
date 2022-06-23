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

import kotlinx.android.synthetic.main.calendar_slot.view.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class CalendarAdapter(
  val context: Context,
  private val items: ArrayList<CalendarItemsDataModel>,
  val date: LocalDate,
  val values: List<CalendarValuesDataModel>?,
  val todayValue: Int?) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

  var onItemClick: ((String) -> Unit)? = null

  companion object {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    var DATE_INPUT_TODAY: LocalDate = LocalDate.parse("01/06/2022", formatter)
    var DATE_INPUT: LocalDate? = null
    var DATE_CLICKED: String = "20/6/2022"
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

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayOfMonth = itemView.dayTextView
    val slotContainer = itemView.slotConstraintLayout
    val TODAYdate = LocalDate.parse("01/06/2022", formatter)
    val todayMonth = TODAYdate.month.name
    val todayYear = TODAYdate.year
    val dateMonth = DATE_INPUT?.month?.name
    val dateMonthValue = DATE_INPUT?.month?.value
    val dateYear = DATE_INPUT?.year
    val today = LocalDate.now()
    val todayDate = today.dayOfMonth
    @SuppressLint("ResourceType")
    fun bind() {
      val newList = mutableListOf<CalendarValuesDataModel>()
      for (i in 0 until items[adapterPosition].startDayOfMonth)
      {
        newList.add(CalendarValuesDataModel(null,null))
      }
      if (values != null) {
        for (element in values) {
          newList.add(element)
        }
      }
      newList.toList()
      if (adapterPosition > items[adapterPosition].startDayOfMonth || adapterPosition == items[adapterPosition].startDayOfMonth) {

        slotContainer.setOnClickListener {
          val date = "${items[adapterPosition].day}/${dateMonthValue}/$dateYear"
          DATE_CLICKED = date
          onItemClick?.invoke(date)
        }

        dayOfMonth.text = items[adapterPosition].day.toString()
        dayOfMonth.setTextColor(Color.BLACK)

        slotContainer.setBackgroundResource(R.drawable.calendar_circle)

        if (todayMonth == dateMonth!! && todayYear == dateYear) {
          if (items[adapterPosition].day > todayDate) {
            dayOfMonth.setTextColor(Color.GRAY)
            slotContainer.setBackgroundResource(0)
            slotContainer.alpha = 0.6F
            slotContainer.isClickable = false
          }
          if (items[adapterPosition].day == todayDate) {

            if(todayValue!=null){
              slotContainer.setBackgroundColor(todayValue)
              slotContainer.setBackgroundResource(0)
              dayOfMonth.setTextColor(Color.WHITE)
              dayOfMonth.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(todayValue, BlendModeCompat.SRC_ATOP)
            }

          }
        }

        val systemTimeZone: ZoneId = ZoneId.systemDefault()
          for (i in newList.indices) {
            if (adapterPosition == i){
            val color = newList[adapterPosition].sensorValueColor
            val dom = newList[adapterPosition].averageWeeklyDataModel?.stamp
            val stamp = dom?.toInstant()?.atZone(systemTimeZone)
            val stampDay = stamp?.dayOfMonth
            val stampMonth = stamp?.month?.name
            val stampYear = stamp?.year
            val domInputs = items[i].day
            if (stampMonth == dateMonth && stampYear == dateYear) {
              slotContainer.setOnClickListener {
                val date = "${items[adapterPosition].day}/${dateMonthValue}/$dateYear"
                DATE_CLICKED = date
                onItemClick?.invoke(date)
              }
              if (stampDay == domInputs) {
                color?.let {
                  slotContainer.setBackgroundColor(it.legendColor)
                  slotContainer.setBackgroundResource(R.drawable.calendar_circle)
                  dayOfMonth.setTextColor(it.legendColor)
                  slotContainer.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color.legendColor, BlendModeCompat.SRC_ATOP) }
              }
            }
          }
        }
      }
    }
  }
}