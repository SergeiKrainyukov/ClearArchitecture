package task8

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

interface ICleaner {
    companion object {
        const val WATER = 1  // полив водой
        const val SOAP = 2   // полив мыльной пеной
        const val BRUSH = 3  // чистка щётками
    }

    fun move(dist: Int, transfer: (String) -> Unit)
    fun turn(turnAngle: Int, transfer: (String) -> Unit)
    fun setState(newState: String, transfer: (String) -> Unit)
    fun start(transfer: (String) -> Unit)
    fun stop(transfer: (String) -> Unit)
    fun make(code: List<String>, transfer: (String) -> Unit)
}

open class Cleaner : ICleaner {
    var x: Double = 0.0
        private set
    var y: Double = 0.0
        private set
    var angle: Int = 0
        private set
    var state: Int = ICleaner.WATER
        private set

    override fun move(dist: Int, transfer: (String) -> Unit) {
        val angleRads = angle * (PI / 180.0)
        x += dist * cos(angleRads)
        y += dist * sin(angleRads)
        transfer("POS($x, $y)")
    }

    override fun turn(turnAngle: Int, transfer: (String) -> Unit) {
        angle += turnAngle
        transfer("ANGLE $angle")
    }

    override fun setState(newState: String, transfer: (String) -> Unit) {
        state = when (newState) {
            "water" -> ICleaner.WATER
            "soap" -> ICleaner.SOAP
            "brush" -> ICleaner.BRUSH
            else -> state
        }
        transfer("STATE $state")
    }

    override fun start(transfer: (String) -> Unit) {
        transfer("START WITH $state")
    }

    override fun stop(transfer: (String) -> Unit) {
        transfer("STOP")
    }

    override fun make(code: List<String>, transfer: (String) -> Unit) {
        for (command in code) {
            val cmd = command.split(' ')
            when (cmd[0]) {
                "move" -> move(cmd[1].toInt(), transfer)
                "turn" -> turn(cmd[1].toInt(), transfer)
                "set" -> setState(cmd[1], transfer)
                "start" -> start(transfer)
                "stop" -> stop(transfer)
            }
        }
    }
}

class HalfCleaner : Cleaner() {
    override fun move(dist: Int, transfer: (String) -> Unit) {
        super.move(dist / 2, transfer)
    }
}


class CleanerApi(
    private val cleaner: Cleaner,
    private val transfer: (String) -> Unit
) {
    fun activateCleaner(code: List<String>) {
        cleaner.make(code, transfer)
    }

    fun getX(): Double {
        return cleaner.x
    }

    fun getY(): Double {
        return cleaner.y
    }

    fun getAngle(): Int {
        return cleaner.angle
    }

    fun getState(): Int {
        return cleaner.state
    }
}

fun main() {
    val transferToCleaner: (String) -> Unit = { message -> println(message) }
    val cleanerApi = CleanerApi(HalfCleaner(), transferToCleaner)
    cleanerApi.activateCleaner(
        listOf(
            "move 100",
            "turn -90",
            "set soap",
            "start",
            "move 50",
            "stop"
        )
    )
    println("${cleanerApi.getX()}, ${cleanerApi.getY()}, ${cleanerApi.getAngle()}, ${cleanerApi.getState()}")
}


