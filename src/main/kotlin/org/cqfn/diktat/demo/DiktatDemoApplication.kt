package org.cqfn.diktat.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class DiktatDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(DiktatDemoApplication::class.java, *args)
}
