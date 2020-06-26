package com.netcetera.skopjepulse.map

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.XAxisDateValueFormatter
import com.netcetera.skopjepulse.map.model.GraphBand
import com.netcetera.skopjepulse.map.model.GraphModel
import com.netcetera.skopjepulse.map.model.GraphSeries
import org.jetbrains.anko.withAlpha
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class GraphView(private val lineChart: LineChart) {

  init {
    lineChart.applyPulseStyling()
    clearGraph()
  }

  fun setGraphModel(graphModel: GraphModel) {
    lineChart.apply {
      // Add limit lines
      axisLeft.apply {
        removeAllLimitLines()
        graphModel.bands.forEach { band ->
          val limitLine = LimitLine(band.to.toFloat(), "").applyPulseStyling(band)
          addLimitLine(limitLine)
        }
        axisMinimum = graphModel.minYAxis()
        axisMaximum = graphModel.maxYAxis()
      }

      // Add data
      val lineDataSets = graphModel.data.map { graphData ->
        graphData.measurements.map { (timestamp, measuredValue) ->
          Entry(timestamp.toFloat(), measuredValue.toFloat())
        }.let { LineDataSet(it, graphData.title).applyPulseStyling(graphData) }
      }
      if (lineDataSets.isNotEmpty()) {
        data = LineData(lineDataSets)
        invalidate()
        lineChart.visibility = View.VISIBLE
      } else {
        clearGraph()
      }
    }
  }

  fun clearGraph() {
    lineChart.data = null
    lineChart.invalidate()
    lineChart.visibility = View.GONE
  }
}

private fun LineChart.applyPulseStyling() {
  setNoDataText(context.getString(R.string.no_sensors_selected))
  setNoDataTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
  setDrawBorders(false)
  extraBottomOffset = 16f
  isHighlightPerDragEnabled = false
  isHighlightPerTapEnabled = false
  isDoubleTapToZoomEnabled = false
  isScaleYEnabled = false
  description = null

  xAxis.apply {
    setDrawGridLines(false)
    setDrawAxisLine(true)
    axisLineColor = ContextCompat.getColor(context, R.color.axis_line_color)
    axisLineWidth = 1.5f
    position = XAxis.XAxisPosition.BOTTOM
    labelCount = 6
    granularity = TimeUnit.MINUTES.toMillis(30).toFloat()
    valueFormatter = XAxisDateValueFormatter()
  }

  axisLeft.apply {
    setDrawLimitLinesBehindData(true)
    setDrawGridLines(false)
    axisLineColor = ContextCompat.getColor(context, R.color.axis_line_color)
    axisLineWidth = 1.5f
  }

  axisRight.apply {
    isEnabled = false
  }

  legend.apply {
    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    textSize = 10f
    textColor = ContextCompat.getColor(context, R.color.bottom_sheet_text_secondary)
    xEntrySpace = 10f
    yEntrySpace = 8f
    formSize = 10f
    isWordWrapEnabled = true
  }
}

private fun LineDataSet.applyPulseStyling(graphSeries: GraphSeries): LineDataSet {
  setDrawCircles(false)
  mode = LineDataSet.Mode.CUBIC_BEZIER
  setDrawValues(false)
  color = graphSeries.color
  lineWidth = 1.5f
  return this
}


private fun LimitLine.applyPulseStyling(graphBand: GraphBand) : LimitLine {
  lineColor = graphBand.color.withAlpha(150)
  textColor = graphBand.color
  lineWidth = 1f
  textSize = 14f
  enableDashedLine(20f, 20f, 0f)
  return this
}

private fun GraphModel.minYAxis() : Float {
  val minBand = bands.minBy { it.from }!!.from.toFloat()
  val minReading = data.flatMap { it.measurements }.minBy { it.second }?.second?.toFloat() ?: minBand
  return min(minBand, minReading) * 1.1f
}

private fun GraphModel.maxYAxis() : Float {
  val maxBand = bands.maxBy { it.to }!!.from.toFloat()
  val minReading = data.flatMap { it.measurements }.maxBy { it.second }?.second?.toFloat() ?: maxBand
  return max(maxBand, minReading) * 1.1f
}