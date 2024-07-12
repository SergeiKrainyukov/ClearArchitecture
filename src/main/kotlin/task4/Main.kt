package task4

fun main() {
    val robot = Robot()
    robot.make(arrayOf(
        "move 100",
        "turn -90",
        "set soap",
        "start",
        "move 50",
        "stop"
    ))
}