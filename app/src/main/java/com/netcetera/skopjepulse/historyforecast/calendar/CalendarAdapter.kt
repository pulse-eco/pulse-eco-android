package com.netcetera.skopjepulse.historyforecast.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.calendar_slot.view.*
import java.time.ZoneOffset

class CalendarAdapter(val context: Context, private val zoneOffset: ZoneOffset) :
  RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {


  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dayOfMonth = itemView.dayTextView
    private val slotContainer = itemView.slotConstraintLayout
  }

  private val diffCallback: DiffUtil.ItemCallback<DayOfMonth> =
    object : DiffUtil.ItemCallback<DayOfMonth>() {
      override fun areItemsTheSame(oldItem: DayOfMonth, newItem: DayOfMonth): Boolean {
        return equalItems(oldItem, newItem)
      }

      override fun areContentsTheSame(oldItem: DayOfMonth, newItem: DayOfMonth): Boolean {
        return false
      }

      private fun equalItems(oldItem: DayOfMonth, newItem: DayOfMonth): Boolean {
        val dayOld = oldItem.day
        val dayNew = newItem.day
        val grayOld = oldItem.grayOut
        val grayNew = newItem.grayOut
        return dayOld == dayNew && grayOld == grayNew
      }
    }

  private val differ: AsyncListDiffer<DayOfMonth> = AsyncListDiffer(this, diffCallback)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
    val view = LayoutInflater.from(context).inflate(R.layout.calendar_slot, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: CalendarAdapter.ViewHolder, position: Int) {}

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

}