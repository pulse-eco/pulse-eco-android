package com.netcetera.skopjepulse.map.overallbanner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.netcetera.skopjepulse.base.dp2px
import com.netcetera.skopjepulse.base.sp2px
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class MeasurementLegendView : View {

  var legend : Legend? = null
    set(value) {
      field = value
      invalidateInternal()
    }

  // Maybe configurable in future
  private var config = Config(true)

  private val mLegendLineHeight = 7
  private val mLinePaint = Paint().apply {
    strokeWidth = mLegendLineHeight.dp2px
  }

  private val mCurrentValueIndicatorExtraHeight = 5
  private val mCurrentValueIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.WHITE
    strokeWidth = (mCurrentValueIndicatorExtraHeight * .8).toInt().dp2px
  }

  private val mTextSize = 9
  private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.WHITE
    textSize = mTextSize.sp2px
    strokeWidth = textSize / 5
  }
  private val mLegendMarkingExtraHeight = mCurrentValueIndicatorExtraHeight - 2

  // Pre-calculated values used for drawing
  private var legendLineDrawY = Float.NaN
  private var legendTextDrawY = Float.NaN

  // Animation of the current value indicator
  private var mInternalPointPosition: Float = Float.NaN
  private var mInternalPointVisibility = true
  private var mPointAnimation: Animator? = null
  private val mOverflowToggler by lazy { RepeatedBooleanToggler(TimeUnit.MILLISECONDS.toMillis(250), overflowToggleListener) }

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    setWillNotDraw(false)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val textHeight = if(config.showLegendBandLabels) mTextSize.sp2px else 0f
    val textBottomPadding = textHeight / 3
    val markingsExtraHeight = max(mCurrentValueIndicatorExtraHeight, if(config.showLegendBandLabels) mLegendMarkingExtraHeight else 0).dp2px
    val lineHeight = mLegendLineHeight.dp2px

    legendTextDrawY = textHeight
    legendLineDrawY = legendTextDrawY + textBottomPadding + markingsExtraHeight + lineHeight / 2

    val totalHeight = lineHeight + textHeight + textBottomPadding + markingsExtraHeight
    val totalWidth = if (MeasureSpec.getMode(widthMeasureSpec) == EXACTLY) {
      MeasureSpec.getSize(widthMeasureSpec)
    } else {
      suggestedMinimumWidth
    }

    setMeasuredDimension(totalWidth, totalHeight.toInt())
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawLegend()
    canvas.drawPointOnLegend()
  }

  private fun Canvas.drawLegend() {
    val legend = legend ?: return

    // The line and markings of the legend
    val minVal = 0
    val maxVal = 100
    val textBounds = Rect()

    var lastLegendBandEnd = 0f
    var lastLegendMarkingEnd: Float = Float.NaN
    legend.bands.forEach {
      val to = min(it.endAt, 100)
      val drawFrom = lastLegendBandEnd
      val drawTo = ((to - minVal).toFloat() / (maxVal - minVal)) * width
      if (drawFrom < drawTo) {
        mLinePaint.color = it.color
        drawLine(drawFrom, legendLineDrawY, drawTo, legendLineDrawY, mLinePaint)
        lastLegendBandEnd = drawTo

        // bands labels
        if (config.showLegendBandLabels) {
          // Skip the last label since it has different draw gravity
          mTextPaint.getTextBounds(it.labelStart, 0, it.labelStart.length, textBounds)
          val notOverlappingWithPrevious = lastLegendMarkingEnd.isNaN() || lastLegendMarkingEnd < drawFrom
          val hasSpaceForLabel = width > drawFrom + textBounds.width()
          if (notOverlappingWithPrevious && hasSpaceForLabel) {
            drawText(it.labelStart, drawFrom, legendTextDrawY, mTextPaint)
            drawLine(drawFrom, height - (mLegendLineHeight + mLegendMarkingExtraHeight).dp2px, drawFrom, height.toFloat(), mTextPaint)
            lastLegendMarkingEnd = drawFrom + textBounds.width() + mLegendMarkingExtraHeight.dp2px
          }
        }
      }
    }

    // Consider drawing the last band label
    if (config.showLegendBandLabels) {
      val text = legend.bands.last().labelEnd
      mTextPaint.getTextBounds(text, 0, text.length, textBounds)
      val legendMaxDrawX = width - textBounds.width() - 2.sp2px
      if (legendMaxDrawX > lastLegendMarkingEnd) {
        drawText(text, legendMaxDrawX, legendTextDrawY, mTextPaint)
      }
    }
  }

  private fun Canvas.drawPointOnLegend() {
    if (!mInternalPointVisibility) {
      return
    }
    val drawPointX = mCurrentValueIndicatorPaint.strokeWidth / 2 + mInternalPointPosition * (width - mCurrentValueIndicatorPaint.strokeWidth)
    val drawPointYStart = height - (mLegendLineHeight + mCurrentValueIndicatorExtraHeight).dp2px

    drawLine(drawPointX, drawPointYStart, drawPointX, height.toFloat(), mCurrentValueIndicatorPaint)
  }

  private fun invalidateInternal() {
    val legend = this.legend
    val startValue = if (mInternalPointPosition.isNaN()) 0f else mInternalPointPosition
    val endValue: Float
    val valueOverflow: Boolean
    if (legend != null) {
      val pointMin = 0
      val pointMax = 100
      val targetValue = min(max(legend.value, pointMin), pointMax).toFloat()
      endValue = (targetValue - pointMin) / (pointMax - pointMin)
      valueOverflow = legend.value.let { it < pointMin || it > pointMax }
    } else {
      endValue = 0f
      valueOverflow = false
    }

    mOverflowToggler.cancel()
    mPointAnimation?.cancel()
    mInternalPointVisibility = true

    // Animation
    if (!isVisible) {
      // Optimization: don't animate if not visible
      mInternalPointPosition = endValue
      return
    }
    mPointAnimation = ValueAnimator.ofFloat(startValue, endValue).apply {
      duration = (max(abs(endValue - startValue), 0.25f) * TimeUnit.SECONDS.toMillis(2)).toLong()
      interpolator = FastOutSlowInInterpolator()
      addUpdateListener { animation ->
        mInternalPointPosition = (animation.animatedValue as Float)
        invalidate()
      }
      if (valueOverflow) {
        addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            mOverflowToggler.start()
          }
        })
      }
      start()
    }
  }

  private val overflowToggleListener = object : RepeatedBooleanToggler.ToggleListener {
    override fun onToggle(newValue: Boolean) {
      mInternalPointVisibility = newValue
      invalidate()
    }
  }
}

data class Config(val showLegendBandLabels : Boolean)

private const val BOOLEAN_TOGGLE_MESSAGE_ID = 1
class RepeatedBooleanToggler(private val toggleDelay: Long, private val toggleListener: ToggleListener) {
  private var toggleValue : Boolean by Delegates.observable(false) { _, _, newValue ->
    toggleListener.onToggle(newValue)
  }

  private val mHandler: Handler = Handler(Handler.Callback {
    if (running && it.what == BOOLEAN_TOGGLE_MESSAGE_ID) {
      toggleValue = !toggleValue
      scheduleToggle()
      return@Callback true
    }
    return@Callback false
  })

  private var running: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
    if (!oldValue && newValue) {
      scheduleToggle()
    } else if (oldValue && !newValue) {
      mHandler.removeMessages(BOOLEAN_TOGGLE_MESSAGE_ID)
    }
  }

  fun start() {
    running = true
  }

  fun cancel() {
    running = false
  }

  private fun scheduleToggle() = mHandler.sendEmptyMessageDelayed(BOOLEAN_TOGGLE_MESSAGE_ID, toggleDelay)

  interface ToggleListener {
    fun onToggle(newValue : Boolean)
  }
}