package tech.gklijs.projector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinCommandHandlerApplication

fun main(args: Array<String>) {
    runApplication<KotlinCommandHandlerApplication>(*args)
}