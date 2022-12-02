package com.netcetera.skopjepulse

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showConfirmDialog(context: Context, message: String, onConfirmation: () -> Unit) {
  val builder = AlertDialog.Builder(context)

  builder.setTitle(context.getString(R.string.confirmation))
  builder.setMessage(message)

  builder.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
    onConfirmation()
  }

  builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ -> // Do nothing
    dialog.dismiss()
  }

  val alert = builder.create()
  alert.show()
}