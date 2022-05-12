package com.netcetera.skopjepulse.historyAndForecast

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.month_year_picker_button.view.*


class CalendarAdapter(val items: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.textButtonMonthYearPicker)
    val montYearPickerButton = view.monthYearPickerButton
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.month_year_picker_button, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    if (holder is ViewHolder) {
      holder.textView.text = item

      holder.montYearPickerButton.setOnClickListener {
        holder.montYearPickerButton.setBackgroundResource(R.drawable.circle_shape_calendar_dark_blue)
        holder.textView.setTextColor(Color.WHITE)
      }
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

}