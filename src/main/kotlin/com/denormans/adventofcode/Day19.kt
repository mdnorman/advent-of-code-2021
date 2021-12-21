package com.denormans.adventofcode

import com.denormans.adventofcode.utils.Orientation
import com.denormans.adventofcode.utils.TriplePoint
import com.denormans.adventofcode.utils.loadStrings
import com.denormans.adventofcode.utils.println
import com.denormans.adventofcode.utils.withCount

fun main() {
  val lines = loadStrings(19, forTest = false)
//  lines.println()

  val scanners = mutableListOf<ScannerData>()
  var currentScannerName = ""
  var currentScannerPoints: MutableList<TriplePoint> = mutableListOf()
  for (line in lines) {
    if (line.startsWith("---")) {
      if (currentScannerName.isNotEmpty()) {
        scanners.add(ScannerData(currentScannerName, currentScannerPoints.sorted()))
      }

      currentScannerName = line.substringAfter("--- ").substringBeforeLast(" ---")
      currentScannerPoints = mutableListOf()

      continue
    }

    if (line.isBlank()) {
      continue
    }

    val (x, y, z) = line.split(",").map { it.toInt() }
    currentScannerPoints.add(TriplePoint(x, y, z))
  }

  scanners.add(ScannerData(currentScannerName, currentScannerPoints.sorted()))
  scanners.println()

  problemOne(scanners)
  problemTwo(scanners)
}

private fun problemOne(scanners: List<ScannerData>) {
  val beaconPoints = scanners.first().beaconPoints.toSortedSet()
  val scannersLeft = scanners.subList(1, scanners.size).toMutableList()
  while (scannersLeft.isNotEmpty()) {
    val (nextScanner, foundOrientation) = scannersLeft.map { scanner ->
      scanner to scanner.findOrientationWithOverlap(beaconPoints.toList())
    }.first { (scanner, overlap) -> overlap != null }

    val (orientation, scannerPoints) = foundOrientation!!

    println("Found ${nextScanner.name} at orientation $orientation")

    beaconPoints.addAll(scannerPoints)
    scannersLeft.remove(nextScanner)
  }

  println("all points: $beaconPoints")

  println("Problem 1: ${beaconPoints.size}")
}

private fun problemTwo(scanners: List<ScannerData>) {

  println("Problem 2:")
}

/*
initial: (+x, +y, +z)
rotate around x1: (+x, -z, +y)
rotate around x2: (+x, -y, -z)
rotate around x3: (+x, +z, -y)
rotate around y1: (-z, +y, +x)
rotate around y2: (-x, +y, -z)
rotate around y3: (+z, +y, -x)
rotate around z1: (-y, +x, +z)
rotate around z2: (-x, -y, +z)
rotate around z3: (+y, -x, +z)

rotate around x1z1: (-z, +x, -y)
 */

private data class ScannerData(val name: String, val beaconPoints: List<TriplePoint>) {
  val orientations by lazy {
    Orientation.all.map { orientation ->
      orientation to beaconPoints.map { point -> point.withOrientation(orientation) }
    }
  }

  fun findOrientationWithOverlap(otherPoints: List<TriplePoint>) =
    orientations.firstNotNullOfOrNull { (orientation, myPoints) ->
      val offset = getOverlapOffset(otherPoints, myPoints) ?: return@firstNotNullOfOrNull null
      orientation to myPoints.map { p -> p - offset }
    }
}

private fun getOverlapOffset(points1: List<TriplePoint>, points2: List<TriplePoint>, neededCount: Int = 12): TriplePoint? {
  val offsetCounts = points1.flatMap { p1 -> points2.map { p2 -> p2 - p1 } }.withCount()
  val (offset, maxCount) = offsetCounts.maxByOrNull { (offset, count) -> count } ?: error("No offsets found")

  return if (maxCount >= neededCount) {
    offset
  } else {
    null
  }
}

// assumes sorted - no need for this
private fun hasOverlap(points1: List<TriplePoint>, points2: List<TriplePoint>, neededCount: Int = 12): Boolean {
  println("Checking overlap of $points1")
  println(" with $points2")

  var count = 0

  var i1 = 0
  var i2 = 0
  while (i1 < points1.size && i2 < points2.size && count < neededCount) {
    val p1 = points1[i1]

    while (i2 < points2.size && points2[i2] < p1) {
      i2 += 1
    }

    if (i2 == points2.size) {
      // got to the end of points2
      break
    }

    val p2 = points2[i2]
    if (p1 == p2) {
      // found one!
      count += 1
    }

    // prepare for next iteration
    i1 += 1
    i2 += 1
  }

  println("found $count")
  return count >= neededCount
}
