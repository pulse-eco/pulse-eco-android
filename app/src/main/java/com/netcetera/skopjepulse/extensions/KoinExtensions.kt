package com.netcetera.skopjepulse.extensions

fun String.isInt(): Boolean{
    for(char in this){
      if(!char.isDigit())
        return false;
    }

  return true;
}