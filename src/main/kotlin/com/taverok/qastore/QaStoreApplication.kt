package com.taverok.qastore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class QaStoreApplication

fun main(args: Array<String>) {
	runApplication<QaStoreApplication>(*args)
}
