package com.denormans.adventofcode

import com.denormans.adventofcode.utils.loadString
import java.math.BigInteger

fun main() {
  val bits = Bits(("1" + loadString(16, forTest = false)).toBigInteger(16).toString(2).substring(1))
  println(bits)

  val (packet) = bits.decodePacket()
  println(packet)

  problemOne(packet)
  problemTwo(packet)
}

private data class Bits(val data: String) {
  val size by lazy { data.length }

  fun subBits(start: Int, end: Int = size) = Bits(data.substring(start, end))

  operator fun get(index: Int) = data[index] == '1'

  operator fun plus(other: Bits) = Bits(data + other.data)

  fun isEmpty() = data.isEmpty()
  fun isNotEmpty() = data.isNotEmpty()

  fun toBigInteger() = data.toBigInteger(2)

  fun toInt() = data.toInt(2)

  override fun toString() = data
}

private sealed class Packet(val version: Int, val type: Int) {
  abstract fun calc(): BigInteger

  open fun visit(fn: (packet: Packet) -> Unit) = fn(this)

  override fun toString() = mapOf("version" to version, "type" to type).toString()
}

private class LiteralValue(version: Int, val value: BigInteger) : Packet(version, Type) {
  companion object {
    const val Type = 4
  }

  override fun calc() = value

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LiteralValue) return false

    if (value != other.value) return false

    return true
  }

  override fun hashCode(): Int {
    return value.hashCode()
  }

  override fun toString() = value.toString()
}

private class Operator(version: Int, type: Int, val packets: List<Packet>) : Packet(version, type) {
  override fun calc(): BigInteger {
    return when (type) {
      Sum -> packets.sumOf { it.calc() }
      Product -> packets.fold(BigInteger.ONE) { result, packet -> result * packet.calc() }
      Minimum -> packets.minOf { it.calc() }
      Maximum -> packets.maxOf { it.calc() }
      GreaterThan -> if (packets.first().calc() > packets[1].calc()) { BigInteger.ONE } else { BigInteger.ZERO }
      LessThan -> if (packets.first().calc() < packets[1].calc()) { BigInteger.ONE } else { BigInteger.ZERO }
      EqualTo -> if (packets.first().calc() == packets[1].calc()) { BigInteger.ONE } else { BigInteger.ZERO }
      else -> throw IllegalStateException("invalid type: $type")
    }
  }

  override fun visit(fn: (packet: Packet) -> Unit) {
    super.visit(fn)

    packets.forEach { it.visit(fn) }
  }

  override fun toString() = mapOf("version" to version, "type" to type, "packets" to packets).toString()

  companion object {
    const val Sum = 0
    const val Product = 1
    const val Minimum = 2
    const val Maximum = 3
    const val GreaterThan = 5
    const val LessThan = 6
    const val EqualTo = 7
  }
}

private fun problemOne(packet: Packet) {
  var versionSum = 0
  packet.visit { versionSum += it.version }

  println("Problem 1: $versionSum")
}

// 011 000 1 00000000010 000 000 0 000000000010110 0001000101010110001011 001000100000000010000100011000111000110100

private fun Bits.decodePacket(): Pair<Packet, Bits> = when (type) {
  LiteralValue.Type -> {
    val value = packetStart().decodeLiteralValue()
//    println("literal value: $value")
    LiteralValue(version, value) to packetStart().skipLiteralValueBits()
  }
  else -> {
    val packetBits = packetStart()
    val restBits = packetBits.subBits(1)
    val lengthTypeBit = packetBits[0]
    if (lengthTypeBit) {
//      println("operator with num packets")
      val lengthBits = restBits.subBits(0, 11)
//      println("lengthBits: $lengthBits")
      val numPackets = lengthBits.toInt()
//      println("numPackets: $numPackets")

      val subPacketBits = restBits.subBits(11)
      val (subPackets, afterSubPacketsBits) = (1..numPackets).fold(mutableListOf<Packet>() to subPacketBits) { (accSubPackets, nextBits), _ ->
        val (subPacket, afterSubPacketBits) = nextBits.decodePacket()
        accSubPackets.add(subPacket)
        accSubPackets to afterSubPacketBits
      }

      Operator(version, type, subPackets) to afterSubPacketsBits
    } else {
//      println("operator with packets length")
      val lengthBits = restBits.subBits(0, 15)
//      println("lengthBits: $lengthBits")
      val numPacketBits = lengthBits.toInt()
//      println("numPacketBits: $numPacketBits")

      val packets = mutableListOf<Packet>()

      var subPacketBits = restBits.subBits(15, 15 + numPacketBits)
      while (subPacketBits.isNotEmpty()) {
        val (packet, moreBits) = subPacketBits.decodePacket()
        packets.add(packet)
        subPacketBits = moreBits
      }

      Operator(version, type, packets) to restBits.subBits(15 + numPacketBits)
    }
  }
}

private val Bits.version
  get() = subBits(0, 3).toInt()

private val Bits.type
  get() = subBits(3, 6).toInt()

private fun Bits.packetStart() = subBits(6)

private fun Bits.decodeLiteralValue() = extractLiteralValueBits().toBigInteger()

private fun Bits.extractLiteralValueBits(): Bits {
  val result = subBits(1, 5)
  return if (this[0]) {
    result + subBits(5).extractLiteralValueBits()
  } else {
    result
  }
}

// don't like this, but it's worse to keep this info inline with the literal value bits
private fun Bits.skipLiteralValueBits(): Bits {
  val rest = subBits(5)
  return if (this[0]) {
    rest.skipLiteralValueBits()
  } else {
    rest
  }
}

private fun problemTwo(packet: Packet) {


  println("Problem 2: ${packet.calc()}")
}
