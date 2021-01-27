package com.netcetera.skopjepulse.extensions

/**
 * Checks if a given string is integer.
 * @return [Boolean] true if integer, false otherwise
 */
fun String.isInt(): Boolean{
    for(char in this){
      if(!char.isDigit())
        return false
    }

  return true
}