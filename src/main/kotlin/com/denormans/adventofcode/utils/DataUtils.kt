package com.denormans.adventofcode.utils

data class Point(val x: Int, val y: Int) {
  override fun toString() = "($x,$y)"
}

infix fun Int.by(y: Int) = Point(this, y)
