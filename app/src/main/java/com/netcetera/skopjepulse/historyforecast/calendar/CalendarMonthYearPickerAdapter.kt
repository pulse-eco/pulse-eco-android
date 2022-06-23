package com.netcetera.skopjepulse.historyforecast.calendar


import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.month_year_picker_button.view.*
import java.util.*

class CalendarMonthYearPickerAdapter(val context: Context, private val items: Array<String>,
                                     private val values:List<CalendarValuesDataModel>?) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var selectedPosition = -1

  companion object {
    var MONTH_YEAR_VALUE: String = ""
  }

  var onItemClick: ((String) -> Unit)? = null

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.monthYearTextView)
    val montYearPickerButton: ConstraintLayout = view.yearMonthConstraintLayout
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

//  @SuppressLint("NotifyDataSetChanged")
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is ViewHolder){
      holder.bind()
    }
//    val item = items[position]
//    val calendar = Calendar.getInstance()
//    if (holder is ViewHolder) {
//      holder.textView.text = item
//      holder.itemView.setOnClickListener {
//        selectedPosition = position
//        notifyDataSetChanged()
//        MONTH_YEAR_VALUE = item
//        onItemClick?.invoke(item)
//      }
//      if(!values.isNullOrEmpty()){
//        val color = values[position].sensorValueColor
//        color?.let {
//          holder.textView.setTextColor(it.legendColor)
//        }
//      }else {
//        holder.montYearPickerButton.setBackgroundResource(R.drawable.month_year_item)
//        holder.textView.setTextColor(Color.BLACK)
//      }
//    }
  }

  override fun getItemCount(): Int {
    return items.size
  }


  private fun getMonthName(numberOfMonth: Int): String {
    when (numberOfMonth) {
      0 -> {
        return context.getString(R.string.january)
      }
      1 -> {
        return context.getString(R.string.february)
      }
      2 -> {
        return context.getString(R.string.march)
      }
      3 -> {
        return context.getString(R.string.april)
      }
      4 -> {
        return context.getString(R.string.may)
      }
      5 -> {
        return context.getString(R.string.june)
      }
      6 -> {
        return context.getString(R.string.july)
      }
      7 -> {
        return context.getString(R.string.august)
      }
      8 -> {
        return context.getString(R.string.september)
      }
      9 -> {
        return context.getString(R.string.october)
      }
      10 -> {
        return context.getString(R.string.november)
      }
      11 -> {
        return context.getString(R.string.december)
      }
      else -> {
        return context.getString(R.string.unknown)
      }
    }
  }
}