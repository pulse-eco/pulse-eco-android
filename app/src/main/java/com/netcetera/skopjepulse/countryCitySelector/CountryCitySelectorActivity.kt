package com.netcetera.skopjepulse.countryCitySelector

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_city_selector.*


/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(), CountryCityAdapter.OnCityClickListener {

  private lateinit var mAdapter: CountryCityAdapter
  private lateinit var faButton: FloatingActionButton
  private lateinit var countryCityViewModel: CountryCityViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_city_selector)

    countryCityViewModel = ViewModelProviders.of(this).get(CountryCityViewModel::class.java)

    val recyclerview : RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.countryList.value, this, this)
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

    faButton = fab_check
    faButton.setOnClickListener{
      countryCityViewModel.saveCheckedCities()
      finish()
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
          RecyclerView.SCROLL_STATE_IDLE -> fab_check.show()
          else -> fab_check.hide()
        }
        super.onScrollStateChanged(recyclerView, newState)
      }
    }
    countryCityRecyclerView.clearOnScrollListeners()
    countryCityRecyclerView.addOnScrollListener(scrollListener)

  }

  override fun onCityClick(city: City, position: Int, isChecked : Boolean) {
    countryCityViewModel.onCityCheck(city,isChecked)
  }

}


