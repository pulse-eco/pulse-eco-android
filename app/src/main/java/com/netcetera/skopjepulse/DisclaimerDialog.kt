package com.netcetera.skopjepulse

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showDisclaimerDialog(context: Context) {
  AlertDialog.Builder(context)
    .setTitle(R.string.disclaimer_details_title)
    .setMessage(R.string.disclaimer_details_text)
    .setNeutralButton(R.string.close_action, null)
    .show()
}