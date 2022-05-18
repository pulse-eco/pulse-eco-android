package com.netcetera.skopjepulse.historyAndForecast


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.month_year_picker_button.view.*
import java.util.*


class CalendarAdapter(val items: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



  var selectedPosition = -1
  companion object{
    var MONTH_YEAR_VALUE: String = ""

  }
  var onItemClick: ((String) -> Unit )? = null

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.textButtonMonthYearPicker)
    val montYearPickerButton = view.monthYearPickerButton
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.month_year_picker_button, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    val calendar = Calendar.getInstance()
    val calendarMonth = calendar.get(Calendar.MONTH)
    val calendarYear = calendar.get(Calendar.YEAR)
    var selected = false
    if (holder is ViewHolder) {
      holder.textView.text = item

      holder.itemView.setOnClickListener {
        selectedPosition = position
        notifyDataSetChanged()
        MONTH_YEAR_VALUE = item
        onItemClick?.invoke(item)
      }

      if (selectedPosition < 0 && item == calendarYear.toString())
      {
        holder.montYearPickerButton.setBackgroundResource(R.drawable.circle_shape_calendar_dark_blue)
        holder.textView.setTextColor(Color.WHITE)
      }

      if (selectedPosition < 0 && item == getMonthName(calendarMonth))
      {
        holder.montYearPickerButton.setBackgroundResource(R.drawable.circle_shape_calendar_dark_blue)
        holder.textView.setTextColor(Color.WHITE)
      }


      if(selectedPosition == position)
      {
        holder.montYearPickerButton.setBackgroundResource(R.drawable.circle_shape_calendar_dark_blue)
        holder.textView.setTextColor(Color.WHITE)
      }
      else{
        holder.montYearPickerButton.setBackgroundResource(R.drawable.circle_shape_calendar_white)
        holder.textView.setTextColor(Color.BLACK)
      }


    }
  }



  override fun getItemCount(): Int {
    return items.size
  }

  interface RecyclerViewInterface{
    fun onClick(position: Int)
  }


  private fun getMonthName(numberOfMonth: Int): String {
    when (numberOfMonth) {
      0 -> {
        return "January"
      }
      1 -> {
        return "February"
      }
      2 -> {
        return "March"
      }
      3 -> {
        return "April"
      }
      4 -> {
        return "May"
      }
      5 -> {
        return "June"
      }
      6 -> {
        return "July"
      }
      7 ->{
        return "Avgust"
      }
      8->{
        return "September"
      }
      9->{
        return "October"
      }
      10->{
        return "November"
      }
      11 ->{
        return "December"
      }
      else ->{
        return "Unknown"
      }
    }
  }
}