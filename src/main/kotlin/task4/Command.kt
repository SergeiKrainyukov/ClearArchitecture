package task4

sealed interface Command {
    data class Move(val distance: Double) : Command
    data class Turn(val angle: Double) : Command
    data class Set(val setParam: SetParams) : Command
    data object Start : Command
    data object Stop : Command
}