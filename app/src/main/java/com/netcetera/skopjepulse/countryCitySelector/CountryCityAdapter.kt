package com.netcetera.skopjepulse.countryCitySelector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.extensions.transformData
import kotlinx.android.synthetic.main.item_city.view.*
import kotlinx.android.synthetic.main.item_country.view.*
import kotlin.collections.ArrayList

/**
* Implementation of [RecyclerView.Adapter] and Holders for [RecyclerView] in the [CountryCitySelectorActivity]
*/

class CountryCityAdapter(var data: List<CountryItem>?, var clickListener: OnCityClickListener) : RecyclerView.Adapter<CountryCityAdapter.BaseViewHolder<*>>(), Filterable{

  private var dataShow: MutableList<Any>

  companion object {
    private val TYPE_COUNTRY = 0
    private val TYPE_CITY = 1
  }

  init {
    dataShow = ArrayList()
    dataShow = data!!.transformData()
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
      is CityViewHolder -> holder.bind(element as City, clickListener)
      is CountryViewHolder -> holder.bind(element as CountryItem, clickListener)
      else -> throw IllegalArgumentException()
    }
  }

  override fun getItemViewType(position: Int): Int {
    val comparable = dataShow[position]
    return when (comparable) {
      is City -> TYPE_CITY
      is CountryItem -> TYPE_COUNTRY
      else -> throw IllegalArgumentException("Invalid type of data " + position)
    }

  }

  override fun getItemCount(): Int {
    return dataShow.size
  }

  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filter = CountryCityFilter(constraint, data)
        val filterResults = FilterResults()
        filterResults.values = filter.filterCountryCity()
        return filterResults
      }

      @Suppress("UNCHECKED_CAST")
      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        dataShow = results?.values as MutableList<Any>
        notifyDataSetChanged()
      }

    }
  }





  abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, clickListener: OnCityClickListener)
  }

  class CityViewHolder(val view: View) : BaseViewHolder<City>(view), View.OnClickListener {

    private val cityTextView = view.txtCityName

    override fun bind(item: City, clickListener: OnCityClickListener) {
      cityTextView.text = item.name
      itemView.setOnClickListener{
          clickListener.onCityClick(item, adapterPosition)
       }
    }

    override fun onClick(v: View?) {

    }
  }

  class CountryViewHolder(val view: View) : BaseViewHolder<CountryItem>(view) {

    private val countryNameTextView = view.txtCountryName

    override fun bind(item: CountryItem, clickListener: OnCityClickListener) {
      countryNameTextView.text = item.name
    }
  }

  interface OnCityClickListener{
    fun onCityClick(city: City, position: Int)
  }

}