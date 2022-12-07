package com.netcetera.skopjepulse.historyforecast

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.Constants.Companion.YEAR_MONTH_DAY
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.Band
import com.netcetera.skopjepulse.base.model.SensorReading
import com.netcetera.skopjepulse.historyforecast.CalendarUtils.formatDate
import com.netcetera.skopjepulse.historyforecast.CalendarUtils.formatWeekday
import com.netcetera.skopjepulse.historyforecast.CalendarUtils.showDayAndMonth
import kotlinx.android.synthetic.main.date_button.view.*
import kotlinx.android.synthetic.main.explore_button.view.*
import java.util.*

class HistoryForecastAdapter(
  val context: Context,
  private val items: ArrayList<HistoryForecastDataModel>
) : RecyclerView.Adapter<HistoryForecastAdapter.BaseViewHolder<*>>() {

  companion object {
    const val VIEW_TYPE_EXPLORE = 1
    const val VIEW_TYPE_DATE = 2
    const val VIEW_TYPE_DATE_FORECAST = 3
    var SELECTED_DATE: Date = Calendar.getInstance().time
    var selectedSensorReading: SensorReading? = null
  }

  var selectedPosition = -1
  var onItemClickExplore: ((String) -> Unit)? = null
  var onItemClick: ((Date?) -> Unit)? = null

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
    private val dateButton = view.cardViewDateButton
    private val dateTitle = view.textViewDayDate
    private val dateAmount = view.textViewPollutionAmount
    private val gradientDrawable = GradientDrawable()

    override fun bind() {
      val sensorReading = items[adapterPosition].averageWeeklyDataModel
      val sensorValueColor = items[adapterPosition].sensorValueColor
      dateTitle.text = setDate(context, sensorReading?.stamp)
      gradientDrawable.cornerRadius = 10f

      if (sensorReading?.value == -1.0) {//Ask Pance about invalid measurement
        noSensorDataAvailable(dateButton, dateAmount, gradientDrawable)
      } else {
        sensorDataAvailable(dateAmount, gradientDrawable, sensorReading, sensorValueColor)
      }

      dateAmount.background = gradientDrawable

      dateButton.setOnClickListener {
        selectedPosition = adapterPosition
        selectedSensorReading = sensorReading!!
        notifyDataSetChanged()
        SELECTED_DATE = sensorReading.stamp
        onItemClick?.invoke(SELECTED_DATE)
      }

      if (selectedPosition > 0) {
        if (selectedPosition == adapterPosition) {
          dateButton.setBackgroundResource(R.drawable.date_button_clicked_shape)
        }
        else {
          dateButton.setBackgroundResource(R.drawable.date_button_unclicked_shape)
        }
      }
      else {
        if (adapterPosition == itemCount - 1) {
          dateButton.setBackgroundResource(R.drawable.date_button_clicked_shape)
        }
      }
    }
  }

  private fun noSensorDataAvailable(button: CardView, amount: TextView, drawable: GradientDrawable) {
    button.alpha = 0.7F
    amount.text = context.getString(R.string.not_available)
    drawable.setColor(ContextCompat.getColor(context, R.color.gray))
  }

  private fun sensorDataAvailable(
    amount: TextView,
    drawable: GradientDrawable,
    sensorReading: SensorReading?,
    sensorValueColor: Band?
  ) {
    amount.text = sensorReading?.value?.toInt().toString()
    sensorValueColor?.legendColor?.let { drawable.setColor(it) }
  }

  fun setDate(context: Context, date: Date?): String {
    val cal = Calendar.getInstance()
    val todayDate = cal.time
    cal.add(Calendar.DATE, -7)
    val previousDate = cal.time

    return when {
      formatDate(date, YEAR_MONTH_DAY) == formatDate(todayDate, YEAR_MONTH_DAY) -> {
        return context.getText(R.string.today).toString()
      }
      formatDate(date, YEAR_MONTH_DAY) <= formatDate(previousDate, YEAR_MONTH_DAY) -> {
        showDayAndMonth(context, date)
      }
      else -> {
        formatWeekday(context, date)
      }
    }
  }

  inner class ForecastViewHolder(view: View) : BaseViewHolder<HistoryForecastDataModel>(view) {
    private val dateButton = view.cardViewDateButton
    override fun bind() {
      dateButton.visibility = View.GONE
    }
  }

}
