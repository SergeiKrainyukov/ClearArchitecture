package task11

import task5.*

data class State<S, out A>(val run: (S) -> Pair<A, S>) {
    // Базовая операция bind для монады состояний
    fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> = State { s ->
        val (a, newState) = run(s)
        f(a).run(newState)
    }

    // Операция map для удобства
    fun <B> map(f: (A) -> B): State<S, B> = flatMap { a -> state { s -> f(a) to s } }

    companion object {
        fun <S, A> state(run: (S) -> Pair<A, S>): State<S, A> = State(run)
        fun <S> get(): State<S, S> = state { it to it }
        fun <S> set(s: S): State<S, Unit> = state { Unit to s }
    }
}

fun move(dist: Int): State<RobotState, Unit> = State { state ->
    val newState = move(::transferToCleaner, dist, state)
    Unit to newState
}

fun turn(angle: Int): State<RobotState, Unit> = State { state ->
    val newState = turn(::transferToCleaner, angle, state)
    Unit to newState
}

fun setState(newInternalState: String): State<RobotState, Unit> = State { state ->
    val newState = setState(::transferToCleaner, newInternalState, state)
    Unit to newState
}

fun start(): State<RobotState, Unit> = State { state ->
    val newState = start(::transferToCleaner, state)
    Unit to newState
}

fun stop(): State<RobotState, Unit> = State { state ->
    val newState = stop(::transferToCleaner, state)
    Unit to newState
}

fun main() {
    val initialState = RobotState(0.0, 0.0, 0.0, WATER)

    val program = State.state<RobotState, Unit> { state -> Unit to state }
        .flatMap { move(100) }
        .flatMap { turn(-90) }
        .flatMap { setState("soap") }
        .flatMap { start() }
        .flatMap { move(50) }
        .flatMap { stop() }

    val (result, finalState) = program.run(initialState)
    println("Final State: x=${finalState.x}, y=${finalState.y}, angle=${finalState.angle}, state=${finalState.state}")
}