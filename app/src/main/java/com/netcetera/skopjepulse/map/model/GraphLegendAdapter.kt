package com.netcetera.skopjepulse.map.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.Sensor
import kotlinx.android.synthetic.main.graph_legdent_item.view.legendColor
import kotlinx.android.synthetic.main.graph_legdent_item.view.legendTitle

class GraphLegendAdapter(private val items: List<GraphLegendItem>) :
  RecyclerView.Adapter<GraphLegendAdapter.GraphLegendItemViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphLegendItemViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.graph_legdent_item, parent, false)
    return GraphLegendItemViewHolder(view)
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: GraphLegendItemViewHolder, position: Int) {
    holder.bind(items[position])
  }

  class GraphLegendItemViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    fun bind(graphLegendItem: GraphLegendItem) {
      itemView.legendColor.setBackgroundColor(graphLegendItem.color)
      itemView.legendTitle.text = graphLegendItem.sensor.description
    }
  }
}

data class GraphLegendItem(val sensor: Sensor, val color: Int)

