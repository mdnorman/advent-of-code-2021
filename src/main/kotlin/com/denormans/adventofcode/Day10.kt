package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println

fun main() {
  val values = loadStrings(10, forTest = false)

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(values: List<String>) {
  val badLines = values.map { findCorrupt(it) }.filterNotNull()

//  badLines.println()

  val result = badLines.map { badPoints.getValue(it) }.sum()

  println("Problem 1: $result")
}

private fun findCorrupt(value: String): Char? {
  val stack = mutableListOf<Char>()
  for (i in value.indices) {
    val ch = value[i]
    if (ch in pairs) {
      stack.add(ch)
    } else {
      val lastCh = stack.removeLast()
      if (ch != pairs[lastCh]) {
        return ch
      }
    }
  }
  return null
}

private fun problemTwo(values: List<String>) {
  val incompleteLines = values.map { fixIncomplete(it) }.filterNotNull()

//  incompleteLines.println()

  val results = incompleteLines.map { it.fold(0L) { acc, curr -> acc * 5 + goodPoints.getValue(curr) } }.sorted()
//  incompleteLines.zip(results).println()

  println("Problem 2: ${results.size}, ${results.size / 2} ${results[results.size / 2]}")
}

private fun fixIncomplete(value: String): String? {
  val stack = mutableListOf<Char>()
  for (i in value.indices) {
    val ch = value[i]
    if (ch in pairs) {
      stack.add(ch)
    } else {
      val lastCh = stack.removeLast()
      if (ch != pairs[lastCh]) {
        return null
      }
    }
  }
  return String(stack.map { pairs.getValue(it) }.reversed().toCharArray())
}

val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val badPoints = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val goodPoints = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
