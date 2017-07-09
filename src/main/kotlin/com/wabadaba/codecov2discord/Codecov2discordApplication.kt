package com.wabadaba.codecov2discord

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Codecov2discordApplication{

}

fun main(args: Array<String>) {
    SpringApplication.run(Codecov2discordApplication::class.java, *args)
}
