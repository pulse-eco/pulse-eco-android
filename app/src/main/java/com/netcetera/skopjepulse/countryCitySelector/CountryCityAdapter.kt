package com.netcetera.skopjepulse.countryCitySelector

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcetera.skopjepulse.Constants
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.item_city.view.*
import kotlinx.android.synthetic.main.item_country.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
* Implementation of [RecyclerView.Adapter] and Holders for [RecyclerView] in the [CountryCitySelectorActivity]
*/

class CountryCityAdapter(var data: List<Any>?, var clickListener: OnCityClickListener, var context : Context) : RecyclerView.Adapter<CountryCityAdapter.BaseViewHolder<*>>(), Filterable{

  private var dataShow: MutableList<Any>
  val sharedPref = context.getSharedPreferences(Constants.SELECTED_CITIES, Context.MODE_PRIVATE)

  companion object {
    private val TYPE_COUNTRY = 0
    private val TYPE_CITY = 1
  }

  init {
    dataShow = ArrayList()
    dataShow = data as MutableList<Any>
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
      is CityViewHolder -> holder.bind(element as CityItem, clickListener, sharedPref)
      is CountryViewHolder -> holder.bind(element as CountryItem, clickListener, sharedPref)
      else -> throw IllegalArgumentException()
    }
  }

  override fun getItemViewType(position: Int): Int {
    val comparable = dataShow[position]
    return when (comparable) {
      is CityItem -> TYPE_CITY
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

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        dataShow = results?.values as MutableList<Any>
        notifyDataSetChanged()
      }

    }
  }



  abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, clickListener: OnCityClickListener, sharedPref: SharedPreferences)
  }

  class CityViewHolder(val view: View) : BaseViewHolder<CityItem>(view), View.OnClickListener {

    private val cityCheckBox = view.checkBoxCity

    override fun bind(item: CityItem, clickListener: OnCityClickListener, sharedPref: SharedPreferences) {
      cityCheckBox.text = item.name
      var selectedCitiesSet = HashSet<CityItem>()
      val gson = Gson()
      val selectedCities = sharedPref.getString(Constants.SELECTED_CITIES, "")
      if (selectedCities != ""){
        val type = object: TypeToken<HashSet<CityItem>>() {}.type
        selectedCitiesSet = gson.fromJson(selectedCities, type)
      }
      cityCheckBox.isChecked = selectedCitiesSet.contains(item)

      itemView.checkBoxCity.setOnClickListener{
          clickListener.onCityClick(item, adapterPosition, itemView.checkBoxCity.isChecked)
       }
    }

    override fun onClick(v: View?) {

    }
  }

  class CountryViewHolder(val view: View) : BaseViewHolder<CountryItem>(view) {

    private val countryNameTextView = view.txtCountryName

    override fun bind(item: CountryItem, clickListener: OnCityClickListener, sharedPref: SharedPreferences) {
      countryNameTextView.text = item.countryName
    }
  }

  interface OnCityClickListener{
    fun onCityClick(cityItem: CityItem, position: Int, isChecked: Boolean)
  }

}