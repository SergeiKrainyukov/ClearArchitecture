package task9

import task5.*

class RobotApiV2 {

    private lateinit var operations: (String, String, RobotState) -> RobotState
    private lateinit var fTransfer: (String) -> Unit
    private lateinit var cleanerState: RobotState

    fun setup(
        operations: (String, String, RobotState) -> RobotState,
        fTransfer: (String) -> Unit
    ) {
        this.operations = operations
        this.fTransfer = fTransfer
    }

    private fun make(command: String): RobotState {
        if (!::cleanerState.isInitialized) {
            cleanerState = RobotState(0.0, 0.0, 0.0, WATER)
        }

        val cmd = command.split(' ')
        cleanerState = when (cmd[0]) {
            "move", "turn", "set", "start", "stop" -> operations(cmd[0], cmd.getOrNull(1) ?: "", cleanerState)
            else -> cleanerState
        }
        return cleanerState
    }

    operator fun invoke(command: String): RobotState {
        return make(command)
    }
}

fun operations(op: String, param: String, state: RobotState): RobotState {
    return when (op) {
        "move" -> move(::transferToCleaner, param.toInt(), state)
        "turn" -> turn(::transferToCleaner, param.toInt(), state)
        "set" -> setState(::transferToCleaner, param, state)
        "start" -> start(::transferToCleaner, state)
        "stop" -> stop(::transferToCleaner, state)
        else -> state
    }
}

fun main() {
    val api = RobotApiV2()
    api.setup(::operations, ::transferToCleaner)

    // Пример вызова
    api("move 100")
    api("turn -90")
    api("set soap")
    api("start")
    api("move 50")
    api("stop")
}
