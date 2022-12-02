package com.netcetera.skopjepulse.countryCitySelector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.utils.Internationalisation
import kotlinx.android.synthetic.main.activity_city_selector.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.netcetera.skopjepulse.main.MainActivity.Companion.NEW_CITY_NAME_RESULT

/**
 * Implementation of [AppCompatActivity] for displaying Country/Cities List
 */

class CountryCitySelectorActivity : AppCompatActivity(){

  private lateinit var mAdapter: CountryCityAdapter
  private val countryCityViewModel: CountryCityViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Internationalisation.loadLocale(this)
    Internationalisation.loadLocale(applicationContext)

    setContentView(R.layout.activity_city_selector)

    val toolbar = findViewById<Toolbar>(R.id.toolbar_to_go_back)

    setSupportActionBar(toolbar)
    supportActionBar?.title = ""

    btn_go_back.setOnClickListener {
      onBackPressed()
    }

    val recyclerview: RecyclerView = countryCityRecyclerView
    recyclerview.layoutManager = LinearLayoutManager(this)
    mAdapter = CountryCityAdapter(countryCityViewModel.getSelectableCities(), onCitySelected)
    recyclerview.adapter = mAdapter

    countryCityViewModel.countryCityList.observe(this, Observer {
      mAdapter.notifyDataSetChanged()
    })

    text_search.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.suggested)
        else txtResult.text = getString(R.string.results)
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.suggested)
        else txtResult.text = getString(R.string.results)
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mAdapter.filter.filter(s.toString())
        if(s.toString().isEmpty()) txtResult.text = getString(R.string.suggested)
        else txtResult.text = getString(R.string.results)
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

  private val onCitySelected: (String) -> Unit = {
    countryCityViewModel.saveCheckedCities()
    val intent = Intent()
    intent.putExtra(NEW_CITY_NAME_RESULT, it)
    setResult(Activity.RESULT_OK, intent)
    this.finish()
  }
}