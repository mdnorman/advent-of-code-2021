package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import java.lang.Math.abs

fun main() {
  val values = loadStrings(7, forTest = false).first().split(",").map { it.toInt() }

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(values: List<Int>) {
  val moves = (values.minOf { it }..values.maxOf { it }).minOf { movesToN(values, it) }

  println("Problem 1: $moves")
}

fun movesToN(values: List<Int>, n: Int)= values.map { abs(it - n) }.sum()

private fun problemTwo(values: List<Int>) {
  val cost = (values.minOf { it }..values.maxOf { it }).minOf { costToN(values, it) }

  println("Problem 2: $cost")
}

fun costToN(values: List<Int>, n: Int) = values.map {
  val moves = abs(it - n)
  moves * (moves + 1) / 2
}.sum()
