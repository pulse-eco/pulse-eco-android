package com.netcetera.skopjepulse.countryCitySelector

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import kotlinx.android.synthetic.main.activity_city_selector.*


/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(){

  private lateinit var mAdapter: CountryCityAdapter
  private lateinit var countryCityViewModel: CountryCityViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_city_selector)

    var toolbar = findViewById<Toolbar>(R.id.toolbar_to_go_back)

    setSupportActionBar(toolbar)
    supportActionBar?.title = ""

    btn_go_back.setOnClickListener {
      onBackPressed()
    }

    countryCityViewModel = ViewModelProviders.of(this).get(CountryCityViewModel::class.java)

    val recyclerview : RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.getSelectableCities(), this)
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryCityList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

    text_search.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.search_suggested)
        else txtResult.text = getString(R.string.search_results)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.search_suggested)
        else txtResult.text = getString(R.string.search_results)
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.search_suggested)
        else txtResult.text = getString(R.string.search_results)
      }

    })

    val scrollListener = object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
//          RecyclerView.SCROLL_STATE_IDLE -> fab_check.show()
//          else -> fab_check.hide()
        }
        super.onScrollStateChanged(recyclerView, newState)
      }
    }
    countryCityRecyclerView.clearOnScrollListeners()
    countryCityRecyclerView.addOnScrollListener(scrollListener)

  }

  fun getCountryCityViewModel(): CountryCityViewModel{
    return countryCityViewModel
  }
}