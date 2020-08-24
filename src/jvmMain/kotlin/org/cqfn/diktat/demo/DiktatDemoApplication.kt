package org.cqfn.diktat.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DiktatDemoApplication

fun main(vararg args: String) {
    runApplication<DiktatDemoApplication>(*args)
}
