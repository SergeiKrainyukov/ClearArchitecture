package task9

import task5.*
import task5.WATER

class RobotApi {

    private lateinit var fMove: ((String) -> Unit, Int, RobotState) -> RobotState
    private lateinit var fTurn: ((String) -> Unit, Int, RobotState) -> RobotState
    private lateinit var fSetState: ((String) -> Unit, String, RobotState) -> RobotState
    private lateinit var fStart: ((String) -> Unit, RobotState) -> RobotState
    private lateinit var fStop: ((String) -> Unit, RobotState) -> RobotState
    private lateinit var fTransfer: (String) -> Unit
    private lateinit var cleanerState: RobotState

    fun setup(
        fMove: ((String) -> Unit, Int, RobotState) -> RobotState,
        fTurn: ((String) -> Unit, Int, RobotState) -> RobotState,
        fSetState: ((String) -> Unit, String, RobotState) -> RobotState,
        fStart: ((String) -> Unit, RobotState) -> RobotState,
        fStop: ((String) -> Unit, RobotState) -> RobotState,
        fTransfer: (String) -> Unit
    ) {
        this.fMove = fMove
        this.fTurn = fTurn
        this.fSetState = fSetState
        this.fStart = fStart
        this.fStop = fStop
        this.fTransfer = fTransfer
    }

    private fun make(command: String): RobotState {
        if (!::cleanerState.isInitialized) {
            cleanerState = RobotState(0.0, 0.0, 0.0, WATER)
        }

        val cmd = command.split(' ')
        cleanerState = when (cmd[0]) {
            "move" -> fMove(fTransfer, cmd[1].toInt(), cleanerState)
            "turn" -> fTurn(fTransfer, cmd[1].toInt(), cleanerState)
            "set" -> fSetState(fTransfer, cmd[1], cleanerState)
            "start" -> fStart(fTransfer, cleanerState)
            "stop" -> fStop(fTransfer, cleanerState)
            else -> cleanerState
        }
        return cleanerState
    }

    operator fun invoke(command: String): RobotState {
        return make(command)
    }
}

fun transferToCleaner(message: String) {
    println(message)
}

fun doubleMove(transfer: (String) -> Unit, dist: Int, state: RobotState): RobotState {
    return move(transfer, dist * 2, state)
}

fun main() {
    val api = RobotApi()
    api.setup(
        ::move,
        ::turn,
        ::setState,
        ::start,
        ::stop,
        ::transferToCleaner
    )
}
