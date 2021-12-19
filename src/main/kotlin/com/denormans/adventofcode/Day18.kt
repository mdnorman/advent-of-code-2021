package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import java.lang.Integer.max

fun main() {
  val values = loadStrings(18, forTest = false).map { it.parseSnailNumber() }
  values.println()

  problemOne(values)
  problemTwo(values)
}

private fun problemOne(values: List<PairSnailNumber>) {
//  values.forEach { value ->
//    println("##### Reducing $value (${value.depth})")
//    println("##### Reduced: ${value.reduce()}")
//  }

  val sum = values.reduce { acc, current -> acc + current }
  println("sum: $sum")

  println("Problem 1: ${sum.magnitude}")
}

private fun problemTwo(values: List<PairSnailNumber>) {
  val max = values.flatMap { value1 -> values.flatMap { value2 -> listOf(value1 + value2, value2 + value1) }}.maxOf { it.magnitude }

  println("Problem 2: $max")
}

private sealed class SnailNumberPart {
  abstract val magnitude: Int

  abstract fun reduce(): SnailNumberPart
}

// note: should have done this as a binary tree encoded in array since there's a limit to the depth!
private data class PairSnailNumber(val part1: SnailNumberPart, val part2: SnailNumberPart) : SnailNumberPart() {
  override val magnitude by lazy { 3 * part1.magnitude + 2 * part2.magnitude }

  val depth: Int by lazy {
    max(((part1 as? PairSnailNumber)?.depth ?: 0) + 1, ((part2 as? PairSnailNumber)?.depth ?: 0) + 1)
  }

  operator fun get(index: Int) = when (index) {
    0 -> part1
    1 -> part2
    else -> throw IndexOutOfBoundsException("$index must be 0 or 1")
  }

  operator fun plus(other: PairSnailNumber) = PairSnailNumber(this, other).reduce()

  override fun reduce(): PairSnailNumber = when {
    anyNeedsExploding(0) -> explode(emptyList()).reduce()
    needsSplitting() -> split(emptyList()).reduce()
    else -> this
  }

  private fun explode(parents: List<Pair<PairSnailNumber, Int>>): PairSnailNumber {
    val parentIndexes = parents.map { it.second }
//    println("explode $this ($depth): $parentIndexes")
    return when {
      needsExploding(parents.size) -> {
//        println("Exploding $this with parents: $parents")
        val number1 = part1 as RegularSnailNumber
        val number2 = part2 as RegularSnailNumber
        val firstToLeft = parents.first().first.findRegularNumberBeforePair(parentIndexes)
        val firstToRight = parents.first().first.findRegularNumberAfterPair(parentIndexes)

        var result = parents.first().first
        if (firstToRight != null) {
          val newNumberToRight = RegularSnailNumber(number2.value + firstToRight.first.value)
//          println("replacing right $firstToRight with $newNumberToRight, before: $result")
          result = result.replace(firstToRight.second, newNumberToRight)
//          println("replacing after: $result")
        }

//        println("replacing $parentIndexes with zero, before: $result")
        result = result.replace(parentIndexes, RegularSnailNumber.ZERO)
//        println("replacing after: $result")

        if (firstToLeft != null) {
          val newNumberToLeft = RegularSnailNumber(number1.value + firstToLeft.first.value)
//          println("replacing left $firstToLeft with $newNumberToLeft, before: $result")
          result = result.replace(firstToLeft.second, newNumberToLeft)
//          println("replacing after: $result")
        }

//        println("result: $result")
        result
      }

      part1 is PairSnailNumber && part1.anyNeedsExploding(parents.size + 1) -> part1.explode(parents + (this to 0))
      part2 is PairSnailNumber && part2.anyNeedsExploding(parents.size + 1) -> part2.explode(parents + (this to 1))
      else -> error("Nothing to explode: $this ($depth), parents: $parents")
    }
  }

  private fun replace(indexes: List<Int>, part: SnailNumberPart): PairSnailNumber {
    val index = indexes.first()
    val myPart = this[index]

    val rest = indexes.subList(1, indexes.size)
    return when {
      rest.isEmpty() -> {
//        println("Replacing $index with $part")
        replace(index, part)
      }
      myPart is PairSnailNumber -> {
//        println("Replacing $index recursively with $rest")
        val newPart = myPart.replace(rest, part)
//        println("New part: $newPart")
        replace(index, newPart)
      }
      else -> error("Bad part at $indexes: $myPart")
    }
  }

  private fun replace(index: Int, withPart: SnailNumberPart) = when (index) {
    0 -> copy(part1 = withPart)
    1 -> copy(part2 = withPart)
    else -> throw IndexOutOfBoundsException("$index must be 0 or 1")
  }

  fun getRegularNumbers() = listRegularNumbers(emptyList()).map { it.first }

  private fun listRegularNumbers(indexes: List<Int>): List<Pair<RegularSnailNumber, List<Int>>> {
    val leftNumbers = when (part1) {
      is PairSnailNumber -> part1.listRegularNumbers(indexes + 0)
      is RegularSnailNumber -> listOf(part1 to indexes + 0)
    }

    val rightNumbers = when (part2) {
      is PairSnailNumber -> part2.listRegularNumbers(indexes + 1)
      is RegularSnailNumber -> listOf(part2 to indexes + 1)
    }

    return leftNumbers + rightNumbers
  }

  private fun findRegularNumberBeforePair(indexes: List<Int>): Pair<RegularSnailNumber, List<Int>>? {
    val regularNumbers = listRegularNumbers(emptyList())
    val index = regularNumbers.indexOfFirst { it.second == indexes + 0 }
    require(index >= 0) { "Indexes not found: $indexes" }

    if (index > 0) {
      return regularNumbers[index - 1]
    }

    return null
  }

  private fun findRegularNumberAfterPair(indexes: List<Int>): Pair<RegularSnailNumber, List<Int>>? {
    val regularNumbers = listRegularNumbers(emptyList())
    val index = regularNumbers.indexOfFirst { it.second == indexes + 1 }
    require(index >= 0) { "Indexes not found: $indexes" }

    if (index < regularNumbers.lastIndex) {
      return regularNumbers[index + 1]
    }

    return null
  }

  private fun split(parents: List<Pair<PairSnailNumber, Int>>): PairSnailNumber {
    return when {
      part1 is PairSnailNumber && part1.needsSplitting() -> copy(part1 = part1.split(parents + (this to 0)))
      part1 is RegularSnailNumber && part1.needsSplitting() -> copy(part1 = part1.split())
      part2 is PairSnailNumber && part2.needsSplitting() -> copy(part2 = part2.split(parents + (this to 1)))
      part2 is RegularSnailNumber && part2.needsSplitting() -> copy(part2 = part2.split())
      else -> this
    }
  }

  private fun needsExploding(parentSize: Int) = parentSize >= 4

  private fun anyNeedsExploding(parentsSize: Int) = depth + parentsSize > 4

  private fun needsSplitting(): Boolean = when {
    part1 is PairSnailNumber && part1.needsSplitting() -> true
    part1 is RegularSnailNumber && part1.needsSplitting() -> true
    part2 is PairSnailNumber && part2.needsSplitting() -> true
    part2 is RegularSnailNumber && part2.needsSplitting() -> true
    else -> false
  }

  override fun toString() = "[$part1,$part2]"
}

private data class RegularSnailNumber(val value: Int) : SnailNumberPart() {
  override val magnitude
    get() = value

  override fun reduce(): SnailNumberPart = when {
    value >= 10 -> split()
    else -> this
  }

  fun split() = PairSnailNumber(RegularSnailNumber(value / 2), RegularSnailNumber(value / 2 + (value % 2)))

  fun needsSplitting() = value > 9

  override fun toString() = value.toString()

  companion object {
    val ZERO = RegularSnailNumber(0)
  }
}

private fun String.parseSnailNumber(): PairSnailNumber {
  val parts = mutableListOf<SnailNumberPart>()
  this.forEach { ch ->
    when (ch) {
      ']' -> {
        val p2 = parts.removeAt(parts.lastIndex)
        val p1 = parts.removeAt(parts.lastIndex)
        parts.add(PairSnailNumber(p1, p2))
      }
      '[', ' ', ',' -> {}
      else -> {
        // must be a number
        parts.add(RegularSnailNumber(ch.toString().toInt()))
      }
    }
  }

  require(parts.size == 1) { "Invalid parts left: $parts" }

  return parts.first() as PairSnailNumber
}
