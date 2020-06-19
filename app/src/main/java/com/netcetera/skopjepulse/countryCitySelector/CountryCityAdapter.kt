package com.netcetera.skopjepulse.countryCitySelector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.item_city.view.*
import kotlinx.android.synthetic.main.item_country.view.*

/**
* Implementation of [RecyclerView.Adapter] and Holders for [RecyclerView] in the [CountryCitySelectorActivity]
*/

class CountryCityAdapter(data: List<Any>?) : RecyclerView.Adapter<CountryCityAdapter.BaseViewHolder<*>>() {

  private var dataShow: MutableList<Any>

  companion object {
    private val TYPE_COUNTRY = 0
    private val TYPE_CITY = 1
  }

  init {
    dataShow = ArrayList()
    if (data != null) {
      for (i in data){
        dataShow.add(Country((i as Country).name))
        dataShow.addAll((i as Country).listCity)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
    val context = parent.context
    return when (viewType) {
      TYPE_COUNTRY -> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        CountryViewHolder(view)
      }
      TYPE_CITY -> {
        val view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false)
        CityViewHolder(view)
      }
      else -> throw IllegalArgumentException("Invalid view type")
    }
  }

  override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
    val element = dataShow[position]
    when(holder){
      is CityViewHolder -> holder.bind(element as City)
      is CountryViewHolder -> holder.bind(element as Country)
      else -> throw IllegalArgumentException()
    }
  }

  override fun getItemViewType(position: Int): Int {
    val comparable = dataShow[position]
    return when (comparable) {
      is City -> TYPE_CITY
      is Country -> TYPE_COUNTRY
      else -> throw IllegalArgumentException("Invalid type of data " + position)
    }

  }

  override fun getItemCount(): Int {
    return dataShow.size
  }





  abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
  }

  class CityViewHolder(val view: View) : BaseViewHolder<City>(view) {

    private val cityTextView = view.txtCityName

    override fun bind(item: City) {
      cityTextView.text = item.name
    }
  }

  class CountryViewHolder(val view: View) : BaseViewHolder<Country>(view) {

    private val countryNameTextView = view.txtCountryName

    override fun bind(item: Country) {
      countryNameTextView.text = item.name
    }
  }

}