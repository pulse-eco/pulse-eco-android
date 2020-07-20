package com.netcetera.skopjepulse.countryCitySelector

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_city_selector.*
import kotlinx.android.synthetic.main.country_city_selector_appbar.*


/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(), CountryCityAdapter.OnCityClickListener{

  private lateinit var mAdapter: CountryCityAdapter
  private lateinit var countryCityViewModel: CountryCityViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_city_selector)

    countryCityViewModel = ViewModelProviders.of(this).get(CountryCityViewModel::class.java)

    val recyclerview : RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.countryCityList.value, this)
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryCityList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

    appbar_backButton.setOnClickListener {
      finish()
    }

    text_search.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        mAdapter.filter.filter(s.toString())
        if (s.toString().isBlank()){
          text_status.text = resources.getString(R.string.search_status_suggested)
        }
        else{
          text_status.text = resources.getString(R.string.search_status_results)
        }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mAdapter.filter.filter(s.toString())
        if (s.toString().isBlank()){
          text_status.text = resources.getString(R.string.search_status_suggested)
        }
        else{
          text_status.text = resources.getString(R.string.search_status_results)
        }
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mAdapter.filter.filter(s.toString())
        if (s.toString().isBlank()){
          text_status.text = resources.getString(R.string.search_status_suggested)
        }
        else{
          text_status.text = resources.getString(R.string.search_status_results)
        }
      }

    })
  }

  override fun onCityClick(city: CityItem) {
    countryCityViewModel.saveCheckedCity(city)
    finish()
  }


}


