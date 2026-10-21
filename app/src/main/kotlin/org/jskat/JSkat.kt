package org.jskat

fun main(args: Array<String>) {
    if (org.jskat.tournament.TournamentCommand.handles(args)) {
        kotlin.system.exitProcess(org.jskat.tournament.TournamentCommand().execute(args, System.out, System.err))
    }
    JSkatFX.main(args, ApplicationVersion.VALUE)
}
