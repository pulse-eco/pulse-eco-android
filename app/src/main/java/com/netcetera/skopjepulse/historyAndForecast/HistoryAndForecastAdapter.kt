package com.netcetera.skopjepulse.historyAndForecast

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.date_button.view.*
import kotlinx.android.synthetic.main.explore_button.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryAndForecastAdapter(val context: Context,val items: ArrayList<HistoryAndForecastDataModel> ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  companion object{
    const val VIEW_TYPE_EXPLORE = 1
    const val VIEW_TYPE_DATE = 2
  }


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    if(viewType == VIEW_TYPE_EXPLORE){
      return ExploreViewHolder(LayoutInflater.from(context).inflate(R.layout.explore_button,parent,false))
    }
    else
    {
      return DateViewHolder(LayoutInflater.from(context).inflate(R.layout.date_button,parent,false))
    }

  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    val item = items[position]

    // Explore button - Calendar
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
      calendar.set(Calendar.YEAR,year)
      calendar.set(Calendar.MONTH,month)
      calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
      updateLable(calendar)
    }

    if(holder is ExploreViewHolder){

      holder.explore.setOnClickListener {
        DatePickerDialog(context,datePicker,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
      }
    }
    else if (holder is DateViewHolder){

      if(position in 1..5){
        holder.bodyAmount.setBackgroundResource(R.drawable.green_shape_with_radius)
        holder.bodyAmount.text = "24"
        holder.titleDate.text = "Mon"
      }

      else{
        holder.dateButton.alpha = 0.4F
        holder.bodyAmount.setBackgroundResource(R.drawable.orange_shape_with_radius)
        holder.bodyAmount.text = "24"
        holder.titleDate.text = "Mon"
      }

    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun getItemViewType(position: Int): Int {
    return items[position].viewType
  }


  class ExploreViewHolder(view: View): RecyclerView.ViewHolder(view){
    val explore = view.exploreButton
  }

  class DateViewHolder(view: View): RecyclerView.ViewHolder(view){
    val dateButton = view.dateButton
    val bodyAmount = view.textViewAmount
    val titleDate = view.textDateButtonDay

  }


  private fun updateLable(calendar: Calendar) {
    val myFormat = "dd-MM-yyyy"
    val sdf = SimpleDateFormat(myFormat,Locale.US)
  }


}