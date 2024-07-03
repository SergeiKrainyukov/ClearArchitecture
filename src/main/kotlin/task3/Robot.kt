package task3

import java.lang.Exception
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class Robot(
    private var x: Double = 0.0,
    private var y: Double = 0.0,
    private var angleDegrees: Double = 0.0,
    private var setParam: SetParams = SetParams.WATER,
    private val commandParser: CommandParser = CommandParserImpl()
) {

    fun getCommands(commands: Array<String>) {
        commands.forEach {
            invokeCommand(commandParser.parseCommand(it))
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

        transferToCleaner("POS $x,$y")
    }

    private fun turn(angleDeltaDegrees: Double) {
        angleDegrees += angleDeltaDegrees
        transferToCleaner("ANGLE $angleDegrees")
    }

    private fun set(setParam: SetParams) {
        this.setParam = setParam
        transferToCleaner("STATE ".plus(setParam))
    }

    private fun start() {
        transferToCleaner("START WITH ".plus(setParam))
    }

    private fun stop() {
        transferToCleaner("STOP")
    }

    private fun transferToCleaner(message: String) {
        println(message)
    }
}

interface CommandParser {
    fun parseCommand(command: String): Command
}

class CommandParserImpl : CommandParser {
    override fun parseCommand(command: String): Command {

        val splittedCommandValue = command.split(" ")[1]

        return when {
            command.contains("move") ->
                Command.Move(splittedCommandValue.toDouble())

            command.contains("turn") ->
                Command.Turn(splittedCommandValue.toDouble())

            command.contains("set") ->
                (Command.Set(parseSetCommand(splittedCommandValue)))

            command == "start" -> Command.Start

            command == "stop" -> Command.Stop
            else -> throw Exception(message = "Unknown command")
        }
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