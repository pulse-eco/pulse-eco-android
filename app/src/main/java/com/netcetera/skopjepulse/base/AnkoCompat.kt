package com.netcetera.skopjepulse.base

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Minimal in-project replacements for the two Anko (org.jetbrains.anko) helpers that were
 * used across the app. Anko is discontinued and no longer resolvable from any live Maven
 * repository, so these equivalents are provided locally to preserve the original behaviour.
 */

fun Int.withAlpha(alpha: Int): Int = this and 0x00ffffff or (alpha shl 24)

val Context.defaultSharedPreferences: SharedPreferences
  get() = PreferenceManager.getDefaultSharedPreferences(this)
