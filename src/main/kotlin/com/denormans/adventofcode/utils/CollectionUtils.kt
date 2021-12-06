package com.denormans.adventofcode.utils

fun <T> Collection<T>.withCount(): MutableMap<T, Long> {
  val pointsWithCount = mutableMapOf<T, Long>()
  forEach { pointsWithCount[it] = pointsWithCount.getOrDefault(it, 0) + 1 }
  return pointsWithCount
}
