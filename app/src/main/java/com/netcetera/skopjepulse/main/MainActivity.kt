package com.netcetera.skopjepulse.main
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.netcetera.skopjepulse.PulseLoadingIndicator
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.cityselect.CitySelectFragment
import com.netcetera.skopjepulse.dashboard.DashboardFragment
import com.netcetera.skopjepulse.map.MapFragment
import com.netcetera.skopjepulse.pulseappbar.PulseAppBarView
import com.netcetera.skopjepulse.settings.SettingsActivity
import com.netcetera.skopjepulse.utils.Internationalisation
import com.squareup.leakcanary.RefWatcher
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pulse_app_bar.*
import kotlinx.android.synthetic.main.simple_error_layout.errorView
import kotlinx.android.synthetic.main.view_picker_dilog.*
import kotlinx.android.synthetic.main.view_picker_dilog.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
  private val refWatcher: RefWatcher by inject()
  private val mainViewModel: MainViewModel by viewModel()
  companion object {
    const val NEW_CITY_REQUEST_CODE = 12345
    const val NEW_CITY_NAME_RESULT = "cityName"
    const val SETTINGS_ACTIVITY_CODE = 666
    var SELECTED_FRAGMENT = ""
    const val DASHBOARD_BUTTON_ID = 2131296421
    const val MAP_BUTTON_ID = 2131296557
    const val SETTINGS_BUTTON_ID = 2131296685
    var activeTab = MAP_BUTTON_ID
    val dashFragment = DashboardFragment()

//    var lateinit pickerView: Layout
  }

  private val citySelectFragment: CitySelectFragment by lazy {
    CitySelectFragment()
  }

  private val appBarView: PulseAppBarView by lazy {
    PulseAppBarView(pulse_app_bar)
  }

  private val loadingIndicator: PulseLoadingIndicator by lazy {
    PulseLoadingIndicator(loadingIndicatorContainer)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Internationalisation.loadLocale(this)
    Internationalisation.loadLocale(applicationContext)
    setContentView(R.layout.activity_main)

    //mock read from SharedPreferences
    SELECTED_FRAGMENT = "map"
    //ovde setiraj inicijalno shto da e highlighted

    btn_menu.setOnClickListener {
     val  pickerView =
        LayoutInflater.from(this).inflate(R.layout.view_picker_dilog, null) as ViewGroup

      val popupWindow = PopupWindow(
        pickerView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
      )

//      val checkedSettingRadioButtonId = settingsRadioGroup.checkedRadioButtonId
//      var setting = findViewById<RadioButton>(checkedSettingRadioButtonId)
        pickerView.settingsRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
//        activeTab = radioGroup.checkedRadioButtonId
//     radioGroup.check(activeTab)

       when (checkedId) {
          R.id.dashboardView -> {
            activeTab = checkedId
//            radioGroup.check(activeTab)
//            setting = findViewById<RadioButton>(checkedSettingRadioButtonId)
            SELECTED_FRAGMENT = "dashboard"
            supportFragmentManager.beginTransaction().apply {
              replace(R.id.content, dashFragment)
              commit()
            }

          }

          R.id.mapView -> {
//              mapView.isChecked = true
           activeTab = checkedId
//            radioGroup.check(activeTab)
            SELECTED_FRAGMENT = "map"

                val actCity = mainViewModel.activeCity.value!!
                val existingMapFragment =
                  supportFragmentManager.findFragmentByTag(actCity.name) as? MapFragment
                if (existingMapFragment == null) {
                 refreshMap()
                }
             }

          R.id.settingsView -> {
//              settingsView.isChecked = true
            activeTab = checkedId
            SELECTED_FRAGMENT = "settings"
            val intent = Intent(this, SettingsActivity::class.java)
            val toast = "Od MapView vo Setting Activity"
            intent.putExtra("Ana", toast)
            startActivityForResult(intent, SETTINGS_ACTIVITY_CODE)
          }
        }
//          if (popupWindow.isShowing) {
//            popupWindow.dismiss()
//          }

          if(activeTab == DASHBOARD_BUTTON_ID) {
            Toast.makeText(applicationContext,"SELEKTIRAN DASHBOARD VIEW",Toast.LENGTH_SHORT).show()
           //dashboardView.isChecked = true
          } else if(activeTab == MAP_BUTTON_ID){
            Toast.makeText(applicationContext,"SELEKTIRAN MAP VIEW",Toast.LENGTH_SHORT).show()
            //settingsRadioGroup.check(MAP_BUTTON_ID)
          }else if(activeTab == MAP_BUTTON_ID){
          settingsRadioGroup.check(SETTINGS_BUTTON_ID)}

      }

      val fragmentInstance = supportFragmentManager.findFragmentById(R.id.content)

      if (fragmentInstance is DashboardFragment) {
        //settingsRadioGroup.check(R.id.dashboardView)
        Toast.makeText(applicationContext, "Dashboard view", Toast.LENGTH_SHORT).show()
        activeTab = DASHBOARD_BUTTON_ID
      } else if (fragmentInstance is MapFragment) {
        //settingsRadioGroup.check(R.id.mapView)
        Toast.makeText(applicationContext, "Map view", Toast.LENGTH_SHORT).show()
        activeTab = MAP_BUTTON_ID
      } else {
        Toast.makeText(applicationContext, "Settings view", Toast.LENGTH_SHORT).show()
        activeTab = SETTINGS_BUTTON_ID
      }

      if (popupWindow.isShowing) {
        popupWindow.dismiss()
      } else {
        popupWindow.showAsDropDown(it)
      }
    }



    mainViewModel.measurementTypeTabs.observe(this) {
      measurementTypeTabBarView.availableMeasurementTypes = it ?: emptyList()
    }
    measurementTypeTabBarView.selectedMeasurementType.observe(this) {
      mainViewModel.showForMeasurement(it)
    }

    appBarView.onCitySelectRequest {
      pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_up_24)
      val citySelectShown =
        supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment

      if (!citySelectShown) {
        supportFragmentManager
          .beginTransaction()
          .add(R.id.content, citySelectFragment)
          .addToBackStack(null)
          .commit()
      }
    }

    //pri start na app
    mainViewModel.activeCity.observe(this) { activeCity ->
      if (activeCity == null) {
        appBarView.displayNoCityName()
        showCitySelectIfNotShown()
      } else {
        appBarView.displayCityName(activeCity)
        pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_down_24)
        val existingMapFragment =
          supportFragmentManager.findFragmentByTag(activeCity.name) as? MapFragment
        if (existingMapFragment == null) {
           refreshMap()
        }
      }
    }
    mainViewModel.showLoading.observe(this, loadingIndicator)

    errorView?.let { errorTextView ->
      errorTextView.setOnClickListener {
        mainViewModel.refreshData(true)
      }
      mainViewModel.errorMessage.observe(this) {
        errorTextView.text = it
        if (it?.isNotBlank() == true) {
          errorTextView.visibility = View.VISIBLE
        } else {
          errorTextView.visibility = View.GONE
        }
      }
    }

    pulseAppbarLogo.setOnClickListener {
     refreshMap()
    }

  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.view_menu, menu)
    return true
  }

  private fun showCitySelectIfNotShown() {
    val someFragmentShown = supportFragmentManager.findFragmentById(R.id.content) != null
    val citySelectShown =
      supportFragmentManager.findFragmentById(R.id.content) is CitySelectFragment
    if (!citySelectShown) {
      supportFragmentManager.beginTransaction().apply {
        add(R.id.content, citySelectFragment)
        if (someFragmentShown) addToBackStack(null)
      }.commit()
    }
  }
//  fun checkButton() {
//    val radioId: Int = settingsRadioGroup.getCheckedRadioButtonId()
//      radioButton = findViewById(radioId)
//    Toast.makeText(
//      this, "Selected Radio Button: " + radioButton?.getText(),
//      Toast.LENGTH_SHORT
//    ).show()
//  }

  private fun refreshMap() {
    val actCit = mainViewModel.activeCity.value!!
    supportFragmentManager.beginTransaction().replace(
      R.id.content,
      MapFragment.newInstance(actCit),
      actCit.name,
    ).commit()
  }

  private fun changeLanguage(localeName: String) {
    Internationalisation.setLocale(this, localeName)
    Internationalisation.setLocale(applicationContext, localeName)
    mainViewModel.reloadDDPData()

    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
  }

  override fun onResume() {
    super.onResume()
    mainViewModel.refreshData(false)
  }

  override fun onDestroy() {
    super.onDestroy()
    refWatcher.watch(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == NEW_CITY_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        if (data != null) {
          val result = data.getStringExtra(NEW_CITY_NAME_RESULT)
          if (result != null) {
            mainViewModel.showForCity(result)
          }
        }
      }
    }

    else if (requestCode == SETTINGS_ACTIVITY_CODE && resultCode == Activity.RESULT_OK) {
      //tuka
//      val returnedResult = data?.data.toString()

      val value = data?.extras?.get("Ana")
      Toast.makeText(
        this,
        value.toString(),
        Toast.LENGTH_SHORT
      ).show()
    }

    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    pulseCityPicker.setImageResource(R.drawable.ic_arrow_drop_down_24)
  }


}
