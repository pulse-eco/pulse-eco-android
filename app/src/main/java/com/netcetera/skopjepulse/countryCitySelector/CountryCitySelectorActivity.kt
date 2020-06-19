package com.netcetera.skopjepulse.countryCitySelector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_city_selector.*

/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity() {

  private lateinit var mAdapter: CountryCityAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_city_selector)

    val countryCityViewModel = ViewModelProviders.of(this).get(CountryCityViewModel::class.java)

    val recyclerview : RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.countryList.value)
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

  }
}
