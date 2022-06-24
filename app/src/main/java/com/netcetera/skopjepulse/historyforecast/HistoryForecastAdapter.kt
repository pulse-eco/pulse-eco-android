package com.netcetera.skopjepulse.historyforecast

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.SensorReading
import kotlinx.android.synthetic.main.date_button.view.*
import kotlinx.android.synthetic.main.explore_button.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryForecastAdapter(
  val context: Context,
  private val items: ArrayList<HistoryForecastDataModel>
) : RecyclerView.Adapter<HistoryForecastAdapter.BaseViewHolder<*>>() {

  var selectedPosition = -1

  companion object {
    const val VIEW_TYPE_EXPLORE = 1
    const val VIEW_TYPE_DATE = 2
    const val VIEW_TYPE_DATE_FORECAST = 3
    var TIME_STAMP: Date = Calendar.getInstance().time
    var selectedSensorReading: SensorReading? = null
  }

  var onItemClick: ((Date?) -> Unit)? = null
  var onItemClickExplore: ((String) -> Unit)? = null


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
    val context = parent.context
    return when (viewType) {
      VIEW_TYPE_EXPLORE -> {
        ExploreViewHolder(
          LayoutInflater.from(context).inflate(R.layout.explore_button, parent, false)
        )
      }
      VIEW_TYPE_DATE -> {
        DateViewHolder(
          LayoutInflater.from(context).inflate(R.layout.date_button, parent, false)
        )
      }
      VIEW_TYPE_DATE_FORECAST -> {
        ForecastViewHolder(
          LayoutInflater.from(context).inflate(R.layout.date_button, parent, false)
        )
      }
      else -> {
        throw IllegalArgumentException("Invalid view type")
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

  inner class ExploreViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val exploreView = view.exploreButton
    override fun bind() {
      exploreView.setOnClickListener {
        onItemClickExplore?.invoke("calendar")
      }
    }
  }

  inner class DateViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val cardView = view.cardViewDateButton
    private val titleDayDate = view.textViewDayDate
    private val bodyDayAmount = view.textViewPollutionAmount
    override fun bind() {
      val sensorReading = items[adapterPosition].averageWeeklyDataModel
      val color = items[adapterPosition].sensorValueColor
      titleDayDate.text = formatDayTitle(context, sensorReading?.stamp)

      if (sensorReading?.value == -1.0) {
        bodyDayAmount.text = context.getString(R.string.not_available)
        bodyDayAmount.setBackgroundResource(R.color.gray)
        cardView.alpha = 0.7F
      } else {
        bodyDayAmount.text = sensorReading?.value?.toInt().toString()
        color?.let { bodyDayAmount.setBackgroundColor(it.legendColor) }
        cardView.alpha = 1F
      }

      cardView.setOnClickListener {
        selectedPosition = adapterPosition
        selectedSensorReading = sensorReading
        notifyDataSetChanged()
        TIME_STAMP = sensorReading!!.stamp
        onItemClick?.invoke(TIME_STAMP)
      }
      if (selectedPosition > 0) {
        if (selectedPosition == adapterPosition) {
          cardView.setBackgroundResource(R.drawable.date_button_clicked_shape)
        } else {
          cardView.setBackgroundResource(R.drawable.date_button_unclicked_shape)
        }
      } else {
        if (adapterPosition == itemCount - 1) {
          cardView.setBackgroundResource(R.drawable.date_button_clicked_shape)
        }
      }
    }
  }

  fun formatDayTitle(context: Context, timeStamp: Date?): String {
    val cal = Calendar.getInstance()
    val todayDate = cal.time
    cal.add(Calendar.DATE, -7)
    val dateOneWeekAgo = cal.time

    return when {
      yearMonthDayDateFormat(timeStamp) <= yearMonthDayDateFormat(dateOneWeekAgo) -> {
        dateShownInsteadDayOfWeek(timeStamp)
      }
      dateShownInsteadDayOfWeek(timeStamp) == dateShownInsteadDayOfWeek(todayDate) -> {
        context.getText(R.string.today).toString()
      }
      else -> {
        dayOfWeekShown(timeStamp)
      }
    }
  }

  inner class ForecastViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val dateButton = view.cardViewDateButton
    override fun bind() {
      dateButton.visibility = View.GONE
    }
  }

  private fun dateShownInsteadDayOfWeek(stamp: Date?): String {
    val format = SimpleDateFormat("d MMM", Locale.getDefault())
    return format.format(stamp!!)
  }

  private fun dayOfWeekShown(stamp: Date?): String {
    val formatWeekDay = SimpleDateFormat("EEE", Locale.getDefault())
    return formatWeekDay.format(stamp!!)
  }

  private fun yearMonthDayDateFormat(stamp: Date?): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(stamp!!)
  }

}
