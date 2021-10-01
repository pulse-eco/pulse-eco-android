package com.netcetera.skopjepulse

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showDisclaimerDialog(context: Context) {
  AlertDialog.Builder(context)
    .setTitle(R.string.disclaimer)
    .setMessage(R.string.disclaimer_message)
    .setNeutralButton(R.string.cancel, null)
    .show()
}