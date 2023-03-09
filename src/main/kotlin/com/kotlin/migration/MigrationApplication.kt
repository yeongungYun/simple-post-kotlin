package com.kotlin.migration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MigrationApplication

fun main(args: Array<String>) {
    runApplication<MigrationApplication>(*args)
}
