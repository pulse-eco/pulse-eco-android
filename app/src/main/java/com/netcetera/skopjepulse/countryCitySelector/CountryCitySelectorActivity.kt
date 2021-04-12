package com.netcetera.skopjepulse.countryCitySelector

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.databinding.ActivityCitySelectorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(){

  private lateinit var mAdapter: CountryCityAdapter
  private val countryCityViewModel: CountryCityViewModel by viewModel()
  private lateinit var views: ActivityCitySelectorBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    views = DataBindingUtil.setContentView(this,R.layout.activity_city_selector)

    var toolbar = findViewById<Toolbar>(R.id.toolbar_to_go_back)

    setSupportActionBar(toolbar)
    supportActionBar?.title = ""

    views.btnGoBack.setOnClickListener {
      onBackPressed()
    }

    val recyclerview: RecyclerView = views.countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.getSelectableCities()) { this.onCitySelected() }
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryCityList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

    views.textSearch.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) views.txtResult.text = getString(R.string.search_suggested)
        else views.txtResult.text = getString(R.string.search_results)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) views.txtResult.text = getString(R.string.search_suggested)
        else views.txtResult.text = getString(R.string.search_results)
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) views.txtResult.text = getString(R.string.search_suggested)
        else views.txtResult.text = getString(R.string.search_results)
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
    views.countryCityRecyclerView.clearOnScrollListeners()
    views.countryCityRecyclerView.addOnScrollListener(scrollListener)
  }

  val onCitySelected: () -> Unit = {
    countryCityViewModel.saveCheckedCities()
    this.finish()
  }
}