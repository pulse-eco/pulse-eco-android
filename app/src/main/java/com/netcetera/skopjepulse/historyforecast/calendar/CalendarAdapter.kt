package com.netcetera.skopjepulse.historyforecast.calendar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_slot.view.*
import java.time.LocalDate

class CalendarAdapter(
  val context: Context,
  private val items: ArrayList<CalendarTry>,
  val startOfMonth: Int
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {


  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayOfMonth = itemView.dayTextView
    val slotContainer = itemView.slotConstraintLayout
    val startOfMonthPosition = startOfMonth - 1
    val today = LocalDate.now()
    val todayDate = today.dayOfMonth
    fun bind() {
      for (i in 0 until itemCount) {
        if (adapterPosition > startOfMonthPosition || adapterPosition == startOfMonthPosition) {
          dayOfMonth.text = items[adapterPosition].day.toString()
          dayOfMonth.setTextColor(Color.BLACK)
          slotContainer.setBackgroundResource(R.drawable.calendar_circle)

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