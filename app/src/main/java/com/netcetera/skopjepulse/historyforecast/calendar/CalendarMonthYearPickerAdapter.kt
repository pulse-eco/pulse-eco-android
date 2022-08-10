package com.netcetera.skopjepulse.historyforecast.calendar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R

class CalendarMonthYearPickerAdapter(
  val context: Context, private val items: Array<String>,
  private val values: List<CalendarValuesDataModel>?
) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var selectedPosition = -1

  companion object {
    var MONTH_YEAR_VALUE: String = ""
  }

  var onItemClick: ((String) -> Unit)? = null

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.monthYearTextView)
    val montYearPickerButton: ConstraintLayout = view.findViewById(R.id.yearMonthConstraintLayout)
    fun bind(){
      val item = items[adapterPosition]
      textView.text = item
      itemView.setOnClickListener {
        selectedPosition = adapterPosition
        notifyDataSetChanged()
        MONTH_YEAR_VALUE = item
        onItemClick?.invoke(item)
      }
      montYearPickerButton.setBackgroundResource(R.drawable.month_year_item)
      textView.setTextColor(Color.BLACK)
      if(!values.isNullOrEmpty()){
        for(i in values.indices){
          if (i == adapterPosition){
        val color = values[adapterPosition].sensorValueColor
        color?.let {
          textView.setTextColor(it.legendColor)
          montYearPickerButton.setBackgroundColor(it.legendColor)
          montYearPickerButton.setBackgroundResource(R.drawable.month_year_item)
          montYearPickerButton.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color.legendColor, BlendModeCompat.SRC_ATOP) }
          }}}
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.month_year_picker_button, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ViewHolder){
      holder.bind()
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

}