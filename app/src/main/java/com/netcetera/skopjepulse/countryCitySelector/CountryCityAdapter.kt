package com.netcetera.skopjepulse.countryCitySelector

import android.content.Context
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
import java.util.*
import kotlin.collections.ArrayList

/**
* Implementation of [RecyclerView.Adapter] and Holders for [RecyclerView] in the [CountryCitySelectorActivity]
*/

class CountryCityAdapter(var data: List<CountryItem>?, var clickListener: OnCityClickListener, var context : Context) : RecyclerView.Adapter<CountryCityAdapter.BaseViewHolder<*>>(), Filterable{

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
      is CityViewHolder -> holder.bind(element as City, clickListener, context)
      is CountryViewHolder -> holder.bind(element as CountryItem, clickListener, context)
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
    abstract fun bind(item: T, clickListener: OnCityClickListener, context: Context)
  }

  class CityViewHolder(val view: View) : BaseViewHolder<City>(view), View.OnClickListener {

    private val cityCheckBox = view.checkBoxCity

    override fun bind(item: City, clickListener: OnCityClickListener, context: Context) {
      cityCheckBox.text = item.name
      val sharedPref = context.getSharedPreferences(context.getString(R.string.selected_cities), Context.MODE_PRIVATE) ?: return
      val selected_cities : String?  = sharedPref.getString(context.getString(R.string.selected_cities), "")?.toLowerCase(Locale.ROOT)
      val selected_cities_list: List<String>? = selected_cities?.split(",")?.map { it.trim() }
      cityCheckBox.isChecked = selected_cities_list?.contains(item.name.toLowerCase(Locale.ROOT))!!

      itemView.checkBoxCity.setOnClickListener{
          clickListener.onCityClick(item, adapterPosition, itemView.checkBoxCity.isChecked)
       }
    }

    override fun onClick(v: View?) {

    }
  }

  class CountryViewHolder(val view: View) : BaseViewHolder<CountryItem>(view) {

    private val countryNameTextView = view.txtCountryName

    override fun bind(item: CountryItem, clickListener: OnCityClickListener, context: Context) {
      countryNameTextView.text = item.countryName
    }
  }

  interface OnCityClickListener{
    fun onCityClick(city: City, position: Int, isChecked: Boolean)
  }

}