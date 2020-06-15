package com.netcetera.skopjepulse.map.mapvisualization

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.drawable.toIcon
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.netcetera.skopjepulse.R
import com.netcetera.skopjepulse.base.model.Sensor
import com.netcetera.skopjepulse.map.model.MapMarkerModel
import org.jetbrains.anko.withAlpha
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapMarkerProvider(val context: Context, private val mapView: org.osmdroid.views.MapView) {

//  fun generateMarker(mapMarkerModel: MapMarkerModel): MarkerOptions {
//    val markerBackground = ContextCompat.getDrawable(context, R.drawable.ic_map_marker)!!
//    markerBackground.colorFilter = PorterDuffColorFilter(mapMarkerModel.markerColor, PorterDuff.Mode.SRC_IN)
//    val markerIcon = markerBackground.toBitmap()
//      .addForegroundText(mapMarkerModel.measuredValue.toString())
//      .addShadow()
//
//    val marker = MarkerOptions()
//      .position(mapMarkerModel.sensor.position!!.let { LatLng(it.latitude, it.longitude) })
//      .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
//      .title(mapMarkerModel.sensor.description)
//      .flat(true)
//    markerIcon.recycle()
//    return marker
//  }


  @RequiresApi(Build.VERSION_CODES.M)
  fun generateMarker(mapMarkerModel: MapMarkerModel, onSensorClicked: OnSensorClicked): Marker {
    val markerBackground = ContextCompat.getDrawable(context, R.drawable.ic_map_marker)!!
    markerBackground.colorFilter = PorterDuffColorFilter(mapMarkerModel.markerColor, PorterDuff.Mode.SRC_IN)
    val markerIcon = markerBackground.toBitmap()
      .addForegroundText(mapMarkerModel.measuredValue.toString())
      .addShadow()

//    val marker1 = MarkerOptions()
//      .position(mapMarkerModel.sensor.position!!.let { LatLng(it.latitude, it.longitude) })
//      .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
//      .title(mapMarkerModel.sensor.description)
//      .flat(true)
//    markerIcon.recycle()



    val markerIconCopy = markerIcon.copy(markerIcon.config, true)

    val marker = Marker(mapView)
    marker.setPosition(GeoPoint(mapMarkerModel.sensor.position!!.latitude, mapMarkerModel.sensor.position!!.longitude))
    marker.setTextIcon(mapMarkerModel.sensor.description)
    marker.setTitle(mapMarkerModel.sensor.description)
    //marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP)
    marker.icon = markerIconCopy.toIcon().loadDrawable(context)
    marker.title = mapMarkerModel.sensor.description
    markerIcon.recycle()
    //marker.closeInfoWindow()
    marker.isEnabled = true

    marker.setOnMarkerClickListener(Marker.OnMarkerClickListener { markerr, mapView ->
      markerr.title = mapMarkerModel.sensor.description
      onSensorClicked.invoke(mapMarkerModel.sensor)
      if (markerr.isEnabled) {
        markerr.showInfoWindow()
      }
      return@OnMarkerClickListener false
    })

    mapView.getOverlays().add(marker)
    mapView.invalidate()
    return marker
  }

//        googleMap.setOnMarkerClickListener { selectedMarker ->
//          val selectedSensor = selectedMarker.tag as Sensor
//          onSensorClicked.invoke(selectedSensor)
//          return@setOnMarkerClickListener false



  private fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(canvas)
    return bitmap
  }

  private fun Bitmap.addForegroundText(text: String): Bitmap {
    val result = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(result)
    canvas.drawBitmap(this, 0f, 0f, null)
    val paint = Paint().apply {
      textSize = width / 3.5f
      color = Color.WHITE
      typeface = Typeface.DEFAULT_BOLD
    }

    val textMeasurement = paint.measureText(text)

    canvas.drawText(text, width / 2 - textMeasurement / 2, height * .40f, paint)
    recycle()
    return result
  }

  private fun Bitmap.addShadow(): Bitmap {
    val dx = 0f
    val dy = height * .03f
    val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)

    val scaleToFit = Matrix()
    val src = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val dst = RectF(0f, 0f, width - dx, height - dy)
    scaleToFit.setRectToRect(src, dst, ScaleToFit.CENTER)

    val dropShadow = Matrix(scaleToFit)
    dropShadow.postTranslate(dx, dy)

    val maskCanvas = Canvas(mask)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    maskCanvas.drawBitmap(this, scaleToFit, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    maskCanvas.drawBitmap(this, dropShadow, paint)

    paint.apply {
      reset()
      isAntiAlias = true
      color = Color.BLACK.withAlpha(200)
      maskFilter = BlurMaskFilter(height * .09f, BlurMaskFilter.Blur.NORMAL)
      isFilterBitmap = true
    }

    val ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val retCanvas = Canvas(ret)
    retCanvas.drawBitmap(mask, 0f, 0f, paint)
    retCanvas.drawBitmap(this, scaleToFit, null)
    mask.recycle()
    recycle()
    return ret
  }
}