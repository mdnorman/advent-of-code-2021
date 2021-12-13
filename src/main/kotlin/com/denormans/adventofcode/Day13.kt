package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Point
import com.denormans.adventofcode.utils.by
import com.denormans.adventofcode.utils.displayGrid
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println

fun main() {
  val values = loadStrings(13, forTest = false)
//  values.println()

  val points = values.filterNot { it.startsWith("fold along") || it.isBlank() }.map {
    val (x,y) = it.split(",").map { it.toInt() }
    x by y
  }.toSet()

  val folds = values.filter { it.startsWith("fold along") }.map {
    val (fold, value) = it.split("=")
    val foldType = if (fold == "fold along x") { FoldType.Vertical } else { FoldType.Horizontal}
    Fold(foldType, value.toInt())
  }

//  displayGrid(points)

  problemOne(points, folds)
  problemTwo(points, folds)
}

private enum class FoldType {
  Horizontal,
  Vertical,
}

private data class Fold(val type: FoldType, val value: Int) {
  fun fold(points: Set<Point>): Set<Point> = when (type) {
    FoldType.Horizontal -> {
      (points.filter { it.y <= value } + points.filter { it.y > value }.map { it.x by value - (it.y - value) }).toSet()
    }
    FoldType.Vertical -> {
      (points.filter { it.x <= value } + points.filter { it.x > value }.map { value - (it.x - value) by it.y }).toSet()
    }
  }
}

private fun problemOne(points: Set<Point>, folds: List<Fold>) {
  val afterFirstFold = folds.first().fold(points)

//  println("after fold")
//  displayGrid(afterFirstFold)

  println("Problem 1: ${afterFirstFold.size}")
}

private fun problemTwo(points: Set<Point>, folds: List<Fold>) {
  val afterFolds = folds.fold(points) { result, fold -> fold.fold(result) }

  displayGrid(afterFolds)

  println("Problem 2:")
}
