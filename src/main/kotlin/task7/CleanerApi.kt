package task7

import task6.RobotState
import task6.WATER

// Внешний апи
interface CleanerApi {
    fun getX(): Double
    fun getY(): Double
    fun getAngle(): Double
    fun getState(): Int
    fun activateCleaner(code: List<String>)
}

// Класс для взаимодействия с API чистильщика
class CleanerApiImpl : CleanerApi {

    private var cleanerState: RobotState = RobotState(0.0, 0.0, 0.0, WATER)

    // Функция для передачи сообщений чистильщику
    private fun transferToCleaner(message: String) {
        println(message)
    }

    override fun getX(): Double {
        return cleanerState.x
    }

    override fun getY(): Double {
        return cleanerState.y
    }

    override fun getAngle(): Double {
        return cleanerState.angle
    }

    override fun getState(): Int {
        return cleanerState.state
    }

    override fun activateCleaner(code: List<String>) {
        for (command in code) {
            val cmd = command.split(' ')
            when (cmd[0]) {
                "move" -> cleanerState = move(transferToCleaner, cmd[1].toInt(), cleanerState)
                "turn" -> cleanerState = turn(transferToCleaner, cmd[1].toInt(), cleanerState)
                "set" -> cleanerState = setState(transferToCleaner, cmd[1], cleanerState)
                "start" -> cleanerState = start(transferToCleaner, cleanerState)
                "stop" -> cleanerState = stop(transferToCleaner, cleanerState)
            }
        }
    }
}

