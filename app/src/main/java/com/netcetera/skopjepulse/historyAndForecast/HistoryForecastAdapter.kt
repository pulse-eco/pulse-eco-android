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
import java.text.SimpleDateFormat

class HistoryForecastAdapter(
  val context: Context,
  private val items: ArrayList<HistoryForecastDataModel>
) : RecyclerView.Adapter<HistoryForecastAdapter.BaseViewHolder<*>>() {

  var selectedPosition = -1

  companion object {
    const val VIEW_TYPE_EXPLORE = 1
    const val VIEW_TYPE_DATE = 2
    const val VIEW_TYPE_DATE_FORECAST = 3
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
    val context = parent.context
    return when (viewType) {
      VIEW_TYPE_EXPLORE -> {
        ExploreViewHolder(LayoutInflater.from(context).inflate(R.layout.explore_button, parent, false))
      }
      VIEW_TYPE_DATE -> {
        DateViewHolder(LayoutInflater.from(context).inflate(R.layout.date_button, parent, false))
      }
      VIEW_TYPE_DATE_FORECAST -> {
        ForecastViewHolder(LayoutInflater.from(context).inflate(R.layout.date_button, parent, false))
      }
      else -> {throw IllegalArgumentException("Invalid view type")
      }
    }
  }

  override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
    holder.bind()
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun getItemViewType(position: Int): Int {
    return items[position].viewType
  }

  abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind()
  }

  inner class ExploreViewHolder(view: View): BaseViewHolder<HistoryForecastDataModel>(view) {
    private val exploreView = view.exploreButton
    override fun bind() {
      context as FragmentActivity
      val fragmentManager = context.supportFragmentManager
      exploreView.setOnClickListener {
        val calendarDialog = CalendarDialog()
        calendarDialog.show(fragmentManager, "calendar_dialog")
      }
    }
  }

  inner class DateViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val dateButton = view.dateButton
    private val bodyAmount = view.textViewAmount
    private val titleDate = view.textDateButtonDay
    val format = SimpleDateFormat("EEEE")
    override fun bind() {
      val value = items[adapterPosition].averageWeeklyDataModel.value
      val dateOfSensorToString = format.format(items[adapterPosition].averageWeeklyDataModel.stamp)
      titleDate.text = dateOfSensorToString.substring(0,3)
      bodyAmount.text = value.toInt().toString()
      val color = items[adapterPosition].sensorValueColor
      bodyAmount.setBackgroundColor(color.legendColor)
      dateButton.setOnClickListener {
        selectedPosition = adapterPosition
        notifyDataSetChanged()
      }
      if (selectedPosition > 0) {
        if (selectedPosition == adapterPosition) {
          dateButton.setBackgroundResource(R.drawable.date_button_clicked_shape)
        } else {
          dateButton.setBackgroundResource(R.drawable.date_button_unclicked_shape)
        }
      } else {
        if (adapterPosition == 5) {
          dateButton.setBackgroundResource(R.drawable.date_button_clicked_shape)
        }
      }
    }
  }

  inner class ForecastViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val dateButton = view.dateButton
    private val bodyAmount = view.textViewAmount
    private val titleDate = view.textDateButtonDay
    override fun bind() {
      titleDate.text = getDayName(adapterPosition)
      bodyAmount.text = getPollutionAmount(adapterPosition).toString()
      bodyAmount.setBackgroundResource(getModuloColor(adapterPosition))
      dateButton.visibility = View.GONE
    }
  }

  private fun getModuloColor(pos: Int): Int {
    return when {
      pos % 3 == 0 -> {
        R.drawable.green_shape_with_radius
      }
      pos % 3 == 1 -> {
        R.drawable.orange_shape_with_radius
      }
      else -> {
        R.drawable.red_shape_radius
      }
    }
  }

  private fun getPollutionAmount(pos: Int): Int {
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
