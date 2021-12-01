package com.denormans.adventofcode

import java.io.File

fun loadNumbers(dayNumber: Int, forTest: Boolean = false) = loadStrings(dayNumber, forTest).map { it.toInt() }

fun loadStrings(dayNumber: Int, forTest: Boolean = false) =
  File(buildFileName(dayNumber, forTest)).readLines()

private fun buildFileName(dayNumber: Int, forTest: Boolean) =
  if (forTest) {
    "input/day${dayNumber}test.txt"
  } else {
    "input/day$dayNumber.txt"
  }
