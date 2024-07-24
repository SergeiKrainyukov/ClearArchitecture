package task10

import task5.*

// Интерпретация набора команд в конкатенативном стиле
fun executeCommands(
    transfer: (String) -> Unit,
    commandStream: String,
    initialState: RobotState = RobotState(0.0, 0.0, 0.0, WATER)
): RobotState {
    val stack = mutableListOf<Any>()
    var currentState = initialState

    val commands = commandStream.split(" ")
    for (command in commands) {
        when (command) {
            "move" -> {
                val dist = stack.removeLast() as Int
                currentState = move(transfer, dist, currentState)
            }

            "turn" -> {
                val angle = stack.removeLast() as Int
                currentState = turn(transfer, angle, currentState)
            }

            "set" -> {
                val state = stack.removeLast() as String
                currentState = setState(transfer, state, currentState)
            }

            "start" -> currentState = start(transfer, currentState)
            "stop" -> currentState = stop(transfer, currentState)
            else -> {
                try {
                    stack.add(command.toInt())
                } catch (e: NumberFormatException) {
                    stack.add(command)
                }
            }
        }
    }
    return currentState
}

fun main() {
    val commandStream = "100 move -90 turn soap set start 50 move stop"

    val finalState = executeCommands(::transferToCleaner, commandStream)

    println("Final State: x=${finalState.x}, y=${finalState.y}, angle=${finalState.angle}, state=${finalState.state}")
}
