package com.netcetera.skopjepulse.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.City
import com.netcetera.skopjepulse.cityselect.CurrentlyCitySelectAdapter
import com.netcetera.skopjepulse.map.GraphView
import com.netcetera.skopjepulse.map.MapViewModel
import com.netcetera.skopjepulse.map.model.GraphModel
import kotlinx.android.synthetic.main.city_select_fragment_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DashboardFragment : Fragment() {

  val viewModel: MapViewModel by viewModel { parametersOf(city) }
  //  lateinit var lineChart : LineChart
  val city: City by lazy { requireArguments().getParcelable("city")!! }

  private val graphView: GraphView by lazy {
    GraphView(dashboardGraph)
  }
  companion object{
    fun newInstance(city: City?) = DashboardFragment().apply {
      arguments = bundleOf(
        "city" to city
      )
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    currentlyCityRecyclerView
    return inflater.inflate(R.layout.fragment_dashboard, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.graphData.observe(viewLifecycleOwner) { showGraphData(it) }
  }

  private fun showGraphData(it: GraphModel?) {
    if (it != null) {
      graphView.setGraphModel(it)
    } else {
      graphView.clearGraph()
    }
  }

//
//  private fun setUpLineChart() {
//    with(dashboardGraph) {
//      animateX(1200, Easing.EaseInSine)
//      description.isEnabled = false
//
//      xAxis.setDrawGridLines(false)
//      xAxis.position = XAxis.XAxisPosition.BOTTOM
//      xAxis.granularity = 1F
//      xAxis.valueFormatter = MyAxisFormatter()
//
//      axisRight.isEnabled = false
//      extraRightOffset = 30f
//
//      legend.orientation = Legend.LegendOrientation.VERTICAL
//      legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//      legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//      legend.textSize = 15F
//      legend.form = Legend.LegendForm.LINE
//    }
//  }
//
//
//  inner class MyAxisFormatter : IndexAxisValueFormatter() {
//
//    private var items = arrayListOf("Milk", "Butter", "Cheese", "Ice cream", "Milkshake")
//
//    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
//      val index = value.toInt()
//      return if (index < items.size) {
//        items[index]
//      } else {
//        null
//      }
//    }
//  }
//
//
//  private fun week1(): ArrayList<Entry> {
//    val sales = ArrayList<Entry>()
//    sales.add(Entry(0f, 15f))
//    sales.add(Entry(1f, 16f))
//    sales.add(Entry(2f, 13f))
//    sales.add(Entry(3f, 22f))
//    sales.add(Entry(4f, 20f))
//    return sales
//  }
//
//  private fun week2(): ArrayList<Entry> {
//    val sales = ArrayList<Entry>()
//    sales.add(Entry(0f, 11f))
//    sales.add(Entry(1f, 13f))
//    sales.add(Entry(2f, 18f))
//    sales.add(Entry(3f, 16f))
//    sales.add(Entry(4f, 22f))
//    return sales
//  }
//
//
//  private fun setDataToLineChart() {
//
//    val weekOneSales = LineDataSet(week1(), "Week 1")
//    weekOneSales.lineWidth = 3f
//    weekOneSales.valueTextSize = 15f
//    weekOneSales.mode = LineDataSet.Mode.CUBIC_BEZIER
//    weekOneSales.color = ContextCompat.getColor(requireContext(), R.color.red_delete_button)
//    weekOneSales.valueTextColor = ContextCompat.getColor(requireContext(), R.color.red_delete_button)
//    weekOneSales.enableDashedLine(20F, 10F, 0F)
//
//    val weekTwoSales = LineDataSet(week2(), "Week 2")
//    weekTwoSales.lineWidth = 3f
//    weekTwoSales.valueTextSize = 15f
//    weekTwoSales.mode = LineDataSet.Mode.CUBIC_BEZIER
//    weekTwoSales.color = ContextCompat.getColor(requireContext(), R.color.history_and_forecast_green)
//    weekTwoSales.valueTextColor = ContextCompat.getColor(requireContext(), R.color.history_and_forecast_green)
//    weekTwoSales.enableDashedLine(20F, 10F, 0F)
//
//
//    val dataSet = ArrayList<ILineDataSet>()
//    dataSet.add(weekOneSales)
//    dataSet.add(weekTwoSales)
//
//    val lineData = LineData(dataSet)
//    lineChart.data = lineData
//
//    lineChart.invalidate()
//  }


}