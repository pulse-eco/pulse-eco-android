package com.netcetera.skopjepulse.cityselect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.extensions.updateForCity
import kotlinx.android.synthetic.main.city_select_item_layout.view.*

typealias HistorySelectListener = (city: City) -> Unit

class HistoryCitySelectAdapter : RecyclerView.Adapter<CitySelectItemViewHolder>(),
  Observer<List<CitySelectItem>> {

  private var citySelectListener: CitySelectListener? = null

  private val differ =
    AsyncListDiffer<CitySelectItem>(this, object : DiffUtil.ItemCallback<CitySelectItem?>() {
      override fun areItemsTheSame(oldItem: CitySelectItem, newItem: CitySelectItem): Boolean {
        return oldItem.city.name == newItem.city.name
      }

      override fun areContentsTheSame(oldItem: CitySelectItem, newItem: CitySelectItem): Boolean {
        return oldItem == newItem
      }
    })

  override fun onChanged(newItems: List<CitySelectItem>?) {
    differ.submitList(newItems)
  }

  fun del(position: Int): String {
    val list = arrayListOf<CitySelectItem>()
    list.addAll(differ.currentList)
    val city = list[position]
    list.removeAt(position)
    onChanged(list)
    notifyDataSetChanged()
    return city.city.name
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySelectItemViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.city_select_item_layout, parent, false)
    return CitySelectItemViewHolder(view) { city -> citySelectListener?.invoke(city) }
  }

  override fun getItemCount(): Int = differ.currentList.size

  override fun onBindViewHolder(holder: CitySelectItemViewHolder, position: Int) {
    holder.bind(differ.currentList[position])
  }

  fun onCitySelected(listener: CitySelectListener) {
    citySelectListener = listener
  }
}

class HistorySelectItemViewHolder(view: View, citySelectListener: CitySelectListener) :
  RecyclerView.ViewHolder(view) {
  lateinit var googleMap: GoogleMap

  var citySelectItem: CitySelectItem? = null

  init {
    itemView.setOnClickListener {
      citySelectListener.invoke(citySelectItem!!.city)
    }
    internalDisplayItemMap()
  }

  fun bind(citySelectItem: CitySelectItem) {
    this.citySelectItem = citySelectItem
    internalDisplayItem()
  }

  private fun internalDisplayItem() {
    val citySelectItem = this.citySelectItem
    if (citySelectItem != null) {
      itemView.citySelectMeasureValue.visibility = View.VISIBLE
      itemView.citySelectMeasureLabel.visibility = View.VISIBLE
      itemView.imageNoDataAvailable.visibility = View.GONE
      itemView.citySelectMeasureContainer.setCardBackgroundColor(citySelectItem.color)
      itemView.citySelectCityLabel.text = citySelectItem.city.displayName
      itemView.citySelectCountryLabel.text = citySelectItem.city.countryName
      itemView.citySelectOverallStatus.text = citySelectItem.measurementDescription
      itemView.citySelectMeasureValue.text = citySelectItem.measurementValue
      itemView.citySelectMeasureLabel.text = citySelectItem.measurementUnit
      if (citySelectItem.measurementValue == "N/A") {
        itemView.citySelectMeasureValue.visibility = View.GONE
        itemView.citySelectMeasureLabel.visibility = View.GONE
        itemView.imageNoDataAvailable.visibility = View.VISIBLE
      }
      internalDisplayItemMap()
    }
  }

  private fun internalDisplayItemMap() {
    val citySelectItem = this.citySelectItem
    if (citySelectItem != null && ::googleMap.isInitialized) {
      googleMap.updateForCity(citySelectItem.city)
    }
  }
}