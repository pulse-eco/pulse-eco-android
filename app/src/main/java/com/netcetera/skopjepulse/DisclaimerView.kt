package com.netcetera.skopjepulse

import android.content.Context
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.netcetera.skopjepulse.extensions.openUrl
import com.netcetera.skopjepulse.utils.Truss

class DisclaimerView : AppCompatTextView {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    gravity = Gravity.CENTER
    setTextColor(ContextCompat.getColor(context, R.color.bottom_sheet_text_secondary))
    setLinkTextColor(ContextCompat.getColor(context, R.color.bottom_sheet_text_primary))
    textSize = 12f
    text = Truss()
      .append(context.getString(R.string.disclaimer_short_message))
      .append(' ')
        .pushSpan(object : ClickableSpan() {
          override fun onClick(widget: View) {
            showDisclaimerDialog(context)
          }
        })
        .append(context.getString(R.string.details))
        .popSpan()
      .append("\n\n")
        .pushSpan(object : ClickableSpan() {
          override fun onClick(widget: View) {
            val lang = context.getSharedPreferences(Constants.LANGUAGE_CODE, Context.MODE_PRIVATE).getString(Constants.LANGUAGE_CODE, null)
            if (lang.equals("de")) {
              context.openUrl("https://www.netcetera.com/de/home/privacy-policy.html")
            } else {
              context.openUrl("https://www.netcetera.com/home/privacy-policy.html")
            }
          }
        })
        .append(context.getString(R.string.privacy_policy))
        .popSpan()
      .build()
    movementMethod = LinkMovementMethod.getInstance()
  }
}