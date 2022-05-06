package com.netcetera.skopjepulse.historyAndForecast


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.date_button.view.*
import kotlinx.android.synthetic.main.explore_button.view.*
import kotlin.collections.ArrayList


class HistoryAndForecastAdapter(
  val context: Context,
  val items: ArrayList<HistoryAndForecastDataModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var currentSelectedDate: Long? = null

  companion object {
    const val VIEW_TYPE_EXPLORE = 1
    const val VIEW_TYPE_DATE = 2
  }


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    if (viewType == VIEW_TYPE_EXPLORE) {
      return ExploreViewHolder(
        LayoutInflater.from(context).inflate(R.layout.explore_button, parent, false)
      )
    } else {
      return DateViewHolder(
        LayoutInflater.from(context).inflate(R.layout.date_button, parent, false)
      )
    }

  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    val item = items[position]

    // Explore button - Calendar
    if (holder is ExploreViewHolder) {

      val datePicker =
        MaterialDatePicker.Builder.datePicker().setTheme(R.style.DefaultDatePickerTheme)
          .setTitleText("Select date")
          .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
          .build()

      val activity = context as FragmentActivity
      val fragmentManager: FragmentManager = activity.supportFragmentManager

      holder.explore.setOnClickListener {
        datePicker.show(fragmentManager, datePicker.toString())
      }

    } else if (holder is DateViewHolder) {

      if (position in 1..5) {
        holder.bodyAmount.setBackgroundResource(R.drawable.green_shape_with_radius)
        holder.bodyAmount.text = "24"
        holder.titleDate.text = "Mon"
      } else {
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


  class ExploreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val explore = view.exploreButton
  }

  class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateButton = view.dateButton
    val bodyAmount = view.textViewAmount
    val titleDate = view.textDateButtonDay

  }

  private fun onDateSelected(dateTimeStampInMillis: Long) {
    currentSelectedDate = dateTimeStampInMillis
  }
}