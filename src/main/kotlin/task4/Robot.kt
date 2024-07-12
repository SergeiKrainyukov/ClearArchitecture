package task4

import java.lang.Exception
import kotlin.math.cos
import kotlin.math.sin

class Robot(
    private var x: Double = 0.0,
    private var y: Double = 0.0,
    private var angleDegrees: Double = 0.0,
    private var setParam: SetParams = SetParams.WATER
) {
    fun make(commands: Array<String>) {
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
        val angleRadians = Math.toRadians(angleDegrees)

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

    private fun parseSetCommand(command: String) = when (command) {
        "water" -> SetParams.WATER
        "soap" -> SetParams.SOAP
        "brush" -> SetParams.BRUSH
        else -> throw Exception("incorrect command")
    }
}