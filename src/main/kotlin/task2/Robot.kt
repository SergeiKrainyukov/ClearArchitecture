package task2

import java.lang.Exception
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class Robot(
    private var x: Double = 0.0,
    private var y: Double = 0.0,
    private var angleDegrees: Double = 0.0,
    private var setParam: SetParams = SetParams.WATER
) {
    fun getCommands(commands: Array<String>) {
        commands.forEach(::parseCommand)
    }

    private fun parseCommand(command: String) {
        when {
            command.contains("move") ->
                invokeCommand(Command.Move(command.split(" ")[1].toDouble()))

            command.contains("turn") ->
                invokeCommand(Command.Turn(command.split(" ")[1].toDouble()))

            command.contains("set") ->
                invokeCommand(Command.Set(parseSetCommand(command.split(" ")[1])))

            command == "start" -> invokeCommand(Command.Start)

            command == "stop" -> invokeCommand(Command.Stop)

        }
    }

    private fun invokeCommand(command: Command) {
        when (command) {
            is Command.Move -> move(command.distance)
            is Command.Set -> set(command.setParam)
            Command.Start -> start()
            Command.Stop -> stop()
            is Command.Turn -> turn(command.angle)
        }
    }

    private fun move(distance: Double) {
        // Переводим угол из градусов в радианы
        val angleRadians = toRadians(angleDegrees)

        // Вычисляем смещение по осям x и y
        val deltaX = distance * cos(angleRadians)
        val deltaY = distance * sin(angleRadians)

        // Обновляем координаты робота
        x += deltaX
        y += deltaY

        println("POS $x,$y")
    }

    private fun turn(angleDeltaDegrees: Double) {
        angleDegrees += angleDeltaDegrees
        println("ANGLE $angleDegrees")
    }

    private fun set(setParam: SetParams) {
        this.setParam = setParam
        println("STATE ".plus(setParam))
    }

    private fun start() {
        println("START WITH ".plus(setParam))
    }

    private fun stop() {
        println("STOP")
    }

    private fun parseSetCommand(command: String) = when (command) {
        "water" -> SetParams.WATER
        "soap" -> SetParams.SOAP
        "brush" -> SetParams.BRUSH
        else -> throw Exception("incorrect command")
    }
}

sealed interface Command {
    data class Move(val distance: Double) : Command
    data class Turn(val angle: Double) : Command
    data class Set(val setParam: SetParams) : Command
    data object Start : Command
    data object Stop : Command
}

enum class SetParams {
    WATER,
    SOAP,
    BRUSH
}