package task6

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Внешний апи
interface RobotCleanerApi {
    fun move(state: RobotState, distance: Int): RobotState
    fun turn(state: RobotState, angle: Int): RobotState
    fun setState(state: RobotState, mode: String): RobotState
    fun startCleaning(state: RobotState): RobotState
    fun stopCleaning(state: RobotState): RobotState
    fun executeCommands(state: RobotState, commands: List<String>): RobotState
}

class RobotCleanerApiImpl : RobotCleanerApi {

    private fun transfer(message: Any) {
        transferToCleaner(message)
    }

    override fun move(state: RobotState, distance: Int): RobotState {
        val angleRads = state.angle * (PI / 180.0)
        val newState = RobotState(
            state.x + distance * cos(angleRads),
            state.y + distance * sin(angleRads),
            state.angle,
            state.state
        )
        transfer("POS(${newState.x}, ${newState.y})")
        return newState
    }

    override fun turn(state: RobotState, angle: Int): RobotState {
        val newState = RobotState(
            state.x,
            state.y,
            state.angle + angle,
            state.state
        )
        transfer("ANGLE(${newState.angle})")
        return newState
    }

    override fun setState(state: RobotState, mode: String): RobotState {
        val selfState = when (mode) {
            "water" -> WATER
            "soap" -> SOAP
            "brush" -> BRUSH
            else -> return state
        }
        val newState = RobotState(
            state.x,
            state.y,
            state.angle,
            selfState
        )
        transfer("STATE($selfState)")
        return newState
    }

    override fun startCleaning(state: RobotState): RobotState {
        transfer("START WITH(${state.state})")
        return state
    }

    override fun stopCleaning(state: RobotState): RobotState {
        transfer("STOP")
        return state
    }

    override fun executeCommands(state: RobotState, commands: List<String>): RobotState {
        var currentState = state
        for (command in commands) {
            val cmd = command.split(' ')
            currentState = when (cmd[0]) {
                "move" -> move(currentState, cmd[1].toInt())
                "turn" -> turn(currentState, cmd[1].toInt())
                "set" -> setState(currentState, cmd[1])
                "start" -> startCleaning(currentState)
                "stop" -> stopCleaning(currentState)
                else -> currentState
            }
        }
        return currentState
    }
}

data class RobotState(val x: Double, val y: Double, val angle: Double, val state: Int)

// Режимы работы устройства очистки
const val WATER = 1 // полив водой
const val SOAP = 2 // полив мыльной пеной
const val BRUSH = 3 // чистка щётками

// Взаимодействие с роботом вынесено в отдельную функцию
fun transferToCleaner(message: Any) {
    println(message)
}

// Перемещение
fun move(transfer: (Any) -> Unit, dist: Int, state: RobotState): RobotState {
    val angleRads = state.angle * (PI / 180.0)
    val newState = RobotState(
        state.x + dist * cos(angleRads),
        state.y + dist * sin(angleRads),
        state.angle,
        state.state
    )
    transfer("POS(${newState.x}, ${newState.y})")
    return newState
}

// Поворот
fun turn(transfer: (Any) -> Unit, turnAngle: Int, state: RobotState): RobotState {
    val newState = RobotState(
        state.x,
        state.y,
        state.angle + turnAngle,
        state.state
    )
    transfer("ANGLE(${newState.angle})")
    return newState
}

// Установка режима работы
fun setState(transfer: (Any) -> Unit, newInternalState: String, state: RobotState): RobotState {
    val selfState = when (newInternalState) {
        "water" -> WATER
        "soap" -> SOAP
        "brush" -> BRUSH
        else -> return state
    }
    val newState = RobotState(
        state.x,
        state.y,
        state.angle,
        selfState
    )
    transfer("STATE($selfState)")
    return newState
}

// Начало чистки
fun start(transfer: (Any) -> Unit, state: RobotState): RobotState {
    transfer("START WITH(${state.state})")
    return state
}

// Конец чистки
fun stop(transfer: (Any) -> Unit, state: RobotState): RobotState {
    transfer("STOP")
    return state
}

// Интерпретация набора команд
fun make(transfer: (Any) -> Unit, code: List<String>, state: RobotState): RobotState {
    var currentState = state
    for (command in code) {
        val cmd = command.split(' ')
        currentState = when (cmd[0]) {
            "move" -> move(transfer, cmd[1].toInt(), currentState)
            "turn" -> turn(transfer, cmd[1].toInt(), currentState)
            "set" -> setState(transfer, cmd[1], currentState)
            "start" -> start(transfer, currentState)
            "stop" -> stop(transfer, currentState)
            else -> currentState
        }
    }
    return currentState
}
