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
import com.netcetera.skopjepulse.extensions.applyCitySelectPulseStyling
import com.netcetera.skopjepulse.extensions.updateForCity
import kotlinx.android.synthetic.main.city_select_item_layout.view.*
import java.util.Locale

typealias CitySelectListener = (city : City) -> Unit

class CitySelectAdapter(val longClickListener: OnCitySelectLongClickListener) : RecyclerView.Adapter<CitySelectItemViewHolder>(), Observer<List<CitySelectItem>> {

  private var citySelectListener : CitySelectListener? = null

  private val differ = AsyncListDiffer<CitySelectItem>(this, object : DiffUtil.ItemCallback<CitySelectItem?>() {
    override fun areItemsTheSame(oldItem: CitySelectItem, newItem: CitySelectItem): Boolean {
      return oldItem.city.name == newItem.city.name
    }

    override fun areContentsTheSame(oldItem: CitySelectItem, newItem: CitySelectItem): Boolean {
      return oldItem == newItem
    }
  })

  fun getElement(position: Int):CitySelectItem{
    return differ.currentList[position]
  }

  override fun onChanged(newItems: List<CitySelectItem>?) {
    differ.submitList(newItems)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySelectItemViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.city_select_item_layout, parent, false)
    return CitySelectItemViewHolder(view) { city -> citySelectListener?.invoke(city) }
  }

  override fun getItemCount(): Int = differ.currentList.size

  override fun onBindViewHolder(holder: CitySelectItemViewHolder, position: Int) {
    holder.bind(differ.currentList[position], longClickListener)
  }

  fun onCitySelected(listener : CitySelectListener) {
    citySelectListener = listener
  }

  interface OnCitySelectLongClickListener{
    fun onCitySelectLongClick(city: CitySelectItem)
  }
}

class CitySelectItemViewHolder(view: View, citySelectListener: CitySelectListener) : RecyclerView.ViewHolder(view) {
  lateinit var googleMap: GoogleMap

  var citySelectItem : CitySelectItem? = null

  init {
    itemView.citySelectMapBackground.onCreate(null)
    itemView.citySelectMapBackground.visibility = View.INVISIBLE
    itemView.citySelectMapBackground.getMapAsync { googleMap ->
      googleMap.applyCitySelectPulseStyling(itemView.context)
      itemView.citySelectMapBackground.visibility = View.VISIBLE
      this.googleMap = googleMap
      internalDisplayItemMap()
    }
     itemView.citySelectItem.setOnClickListener {
      citySelectListener.invoke(citySelectItem!!.city)
    }
  }
  fun bind(citySelectItem: CitySelectItem, longClickListener: CitySelectAdapter.OnCitySelectLongClickListener) {
    this.citySelectItem = citySelectItem
    itemView.citySelectItem.setOnLongClickListener {
      longClickListener.onCitySelectLongClick(citySelectItem)
      true
    }
    internalDisplayItem()
  }

  private fun internalDisplayItem() {
    val citySelectItem = this.citySelectItem
    if (citySelectItem != null) {
      itemView.citySelectMeasureContainer.setCardBackgroundColor(citySelectItem.color)
      itemView.citySelectCityLabel.text = citySelectItem.city.name.toUpperCase(Locale.US)
      itemView.citySelectCountryLabel.text = citySelectItem.city.countryName
      itemView.citySelectOverallStatus.text = citySelectItem.measurementDescription
      itemView.citySelectMeasureValue.text = citySelectItem.measurementValue
      itemView.citySelectMeasureLabel.text = citySelectItem.measurementUnit
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