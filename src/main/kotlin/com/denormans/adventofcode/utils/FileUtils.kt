package com.denormans.adventofcode.utils

import java.io.File

fun loadNumbers(dayNumber: Int, forTest: Boolean) = loadStrings(dayNumber, forTest).map { it.toInt() }

fun loadStringPairs(dayNumber: Int, forTest: Boolean) = loadStringLists(dayNumber, forTest).map {  it.toPair() }

fun loadStringTriples(dayNumber: Int, forTest: Boolean) = loadStringLists(dayNumber, forTest).map { it.toTriple() }

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
