package com.netcetera.skopjepulse.historyAndForecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.date_button.view.*
import kotlinx.android.synthetic.main.explore_button.view.*


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

      holder.explore.text = context.getString(R.string.explore)

      context as FragmentActivity
      val fragmentManager = context.supportFragmentManager

      holder.explore.setOnClickListener {
        val calendarDialog = CalendarDialog()
        calendarDialog.show(fragmentManager,"calendar_dialog")
      }

    } else if (holder is DateViewHolder) {
      holder.titleDate.text = getDayName(position)
      holder.bodyAmount.text = getPollutionAmount(position).toString()
      holder.bodyAmount.setBackgroundResource(getModuloColor(position))
      if (position == 6 || position == 7) {
        holder.dateButton.alpha = 0.4F
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
    val explore = view.textExplore
  }

  class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateButton = view.dateButton
    val bodyAmount = view.textViewAmount
    val titleDate = view.textDateButtonDay
  }

  private fun onDateSelected(dateTimeStampInMillis: Long) {
    currentSelectedDate = dateTimeStampInMillis
  }


  private fun getModuloColor(pos: Int): Int {
    if (pos % 3 == 0) {
      return R.drawable.green_shape_with_radius
    } else if (pos % 3 == 1) {
      return R.drawable.orange_shape_with_radius
    } else {
      return R.drawable.red_shape_radius
    }
  }

  private fun getPollutionAmount(pos: Int): Int{
    return pos + 1 * 3
  }

  private fun getDayName(pos: Int): String {
    when (pos) {
      1 -> {
        return context.getString(R.string.monday_short)
      }
      2 -> {
        return context.getString(R.string.tuesday_short)
      }
      3 -> {
        return context.getString(R.string.wednesday_short)
      }
      4 -> {
        return context.getString(R.string.thursday_short)
      }
      5 -> {
        return context.getString(R.string.today)
      }
      6 -> {
        return context.getString(R.string.saturday_short)
      }
      7 -> {
        return context.getString(R.string.sunday_short)
      }
      else -> {
        return context.getString(R.string.unknown)
      }
    }
  }

}
