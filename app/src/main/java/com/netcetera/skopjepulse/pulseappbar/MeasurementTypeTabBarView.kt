package com.netcetera.skopjepulse.pulseappbar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.MeasurementType

/**
 * Represents a navigation toolbar wit [TabLayout]. Use [MeasurementTypeTabBarView.availableMeasurementTypes] to set
 * the [MeasurementTypeTab].
 */
class MeasurementTypeTabBarView : FrameLayout, Observer<MeasurementType> {

  private val _selectedMeasurementType = MutableLiveData<MeasurementType>()

  val selectedMeasurementType: LiveData<MeasurementType>
    get() = _selectedMeasurementType

  private val tabLayout: TabLayout
  private val tabs: MutableList<TabLayout.Tab> = ArrayList()

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    tabLayout = TabLayout(context, attrs, defStyleAttr).apply {
      setTabTextColors(
        ContextCompat.getColor(context, R.color.measurement_type_tab_text_inactive),
        ContextCompat.getColor(context, R.color.measurement_type_tab_text_active)
      )
      setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.measurement_type_tab_selected_indicator))
      tabMode = TabLayout.MODE_SCROLLABLE
      addOnTabSelectedListener(tabSelectedListener)
    }

    addView(tabLayout)
  }

  private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {
      // no-op
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
      // no-op
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
      _selectedMeasurementType.value = tab?.tag as MeasurementType?
    }
  }

  override fun onChanged(measurementType: MeasurementType?) {
    if (measurementType != null) {
      tabLayout.removeOnTabSelectedListener(tabSelectedListener)
      tabs.firstOrNull { it.tag == measurementType }?.select()
      tabLayout.addOnTabSelectedListener(tabSelectedListener)
    }
  }

  var availableMeasurementTypes: List<MeasurementTypeTab> = emptyList()
    set(value) {
      tabLayout.removeOnTabSelectedListener(tabSelectedListener)
      field = value
      tabLayout.removeAllTabs()
      tabs.clear()
      tabs.addAll(value.map { measurementTypeTab ->
        tabLayout.newTab().apply {
          text = measurementTypeTab.title
          tag = measurementTypeTab.measurementType
          tabLayout.addTab(this)
        }
      })
      tabLayout.addOnTabSelectedListener(tabSelectedListener)
    }
}
