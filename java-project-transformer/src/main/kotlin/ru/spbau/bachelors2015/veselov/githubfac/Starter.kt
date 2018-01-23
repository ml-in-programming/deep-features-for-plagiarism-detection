package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.application.ApplicationStarter

class Starter : ApplicationStarter {
    override fun getCommandName(): String {
        return "transform"
    }

    override fun premain(args: Array<out String>?) {
        Log.write(args!!.get(0))
        System.exit(0)
    }

    override fun main(args: Array<out String>?) {
        TODO("not implemented")
    }
}