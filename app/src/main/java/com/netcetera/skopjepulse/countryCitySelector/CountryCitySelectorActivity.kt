package com.netcetera.skopjepulse.countryCitySelector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_city_selector.*

/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(), CountryCityAdapter.OnCityClickListener {

  private lateinit var mAdapter: CountryCityAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_city_selector)

    val countryCityViewModel = ViewModelProviders.of(this).get(CountryCityViewModel::class.java)

    val recyclerview : RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.countryList.value, this)
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

    text_search.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        mAdapter.filter.filter(s.toString())
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mAdapter.filter.filter(s.toString())
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mAdapter.filter.filter(s.toString())
      }

    })
  }

  override fun onCityClick(city: City, position: Int) {
    Toast.makeText(this, city.name, Toast.LENGTH_SHORT).show()
  }

}


