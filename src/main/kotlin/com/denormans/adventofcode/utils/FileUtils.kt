package com.denormans.adventofcode.utils

import java.io.File

fun loadNumbers(dayNumber: Int, forTest: Boolean) = loadStrings(dayNumber, forTest).map { it.toInt() }

fun loadStringPairs(dayNumber: Int, forTest: Boolean) = loadStringLists(dayNumber, forTest).map {
  val (first, second) = it
  first to second
}

fun loadStringTriples(dayNumber: Int, forTest: Boolean) = loadStringLists(dayNumber, forTest).map {
  val (first, second, third) = it
  Triple(first, second, third)
}

fun loadStringLists(dayNumber: Int, forTest: Boolean) = loadStrings(dayNumber, forTest).map { it.split(" ") }

fun loadString(dayNumber: Int, forTest: Boolean) = loadStrings(dayNumber, forTest).first()

fun loadStrings(dayNumber: Int, forTest: Boolean) =
  File(buildFileName(dayNumber, forTest)).readLines()

private fun buildFileName(dayNumber: Int, forTest: Boolean) =
  if (forTest) {
    "input/day${dayNumber}test.txt"
  } else {
    "input/day$dayNumber.txt"
  }
