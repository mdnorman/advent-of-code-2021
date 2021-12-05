package com.denormans.adventofcode.utils

fun <T> Collection<T>.withCount(): MutableMap<T, Int> {
  val pointsWithCount = mutableMapOf<T, Int>()
  forEach { pointsWithCount[it] = pointsWithCount.getOrDefault(it, 0) + 1 }
  return pointsWithCount
}
