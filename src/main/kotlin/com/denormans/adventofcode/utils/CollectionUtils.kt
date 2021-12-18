package com.denormans.adventofcode.utils

fun <T> List<T>.toPair(): Pair<T, T> {
  val (a, b) = this
  return a to b
}

fun <T> Collection<T>.withCount(): MutableMap<T, Long> {
  val pointsWithCount = mutableMapOf<T, Long>()
  forEach { pointsWithCount[it] = pointsWithCount.getOrDefault(it, 0) + 1 }
  return pointsWithCount
}

fun <T> List<Set<T>>.unionAll(): Set<T> =
  when (size) {
    0 -> emptySet()
    1 -> first()
    else -> first() union subList(1, size).unionAll()
  }

fun <T> List<Set<T>>.intersectAll(): Set<T> =
  when (size) {
    0 -> emptySet()
    1 -> first()
    else -> first() intersect subList(1, size).intersectAll()
  }
