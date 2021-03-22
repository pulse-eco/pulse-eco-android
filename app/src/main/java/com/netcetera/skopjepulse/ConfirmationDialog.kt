package com.netcetera.skopjepulse

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showConformationDialog(context: Context, message: String, onConfirmation: () -> Unit) {
  val builder = AlertDialog.Builder(context)

  builder.setTitle(context.getString(R.string.confirm_title))
  builder.setMessage(message)

  builder.setPositiveButton(context.getString(R.string.btn_yes)) { _, _ ->
    onConfirmation()
  }

  builder.setNegativeButton(context.getString(R.string.btn_no)) { dialog, _ -> // Do nothing
    dialog.dismiss()
  }

  val alert = builder.create()
  alert.show()
}