package nl.openweb.commandhandler

import nl.openweb.data.Uuid
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

object Utils {

    fun getIban(): String {
        val builder = StringBuilder("0")
        val random = Random()
        repeat(9) {
            builder.append(random.nextInt(10))
        }
        return builder.toString().getIban()
    }

    private fun String.getIban(): String {
        val checkNr = BigInteger("24251423${this}232100").rem(BigInteger("97")).intValueExact()
        return if (checkNr < 10) {
            "NL0${checkNr}OPEN$this"
        } else {
            "NL${checkNr}OPEN$this"
        }
    }

    fun String.isValidOpenIban(): Boolean {
        if (this.length != 18) {
            return false
        }
        return this == this.substring(8, 18).getIban()
    }

    fun getToken(): String {
        val builder = StringBuilder("")
        val random = Random()
        repeat(20) {
            builder.append(random.nextInt(10))
        }
        return builder.toString()
    }

    fun Uuid.toUuid(): UUID {
        val byteBuffer = ByteBuffer.wrap(this.bytes())
        val high = byteBuffer.long
        val low = byteBuffer.long
        return UUID(high, low)
    }

    fun String.invalidFrom() = when (this) {
        "cash" -> false
        else -> !this.isValidOpenIban()
    }
}