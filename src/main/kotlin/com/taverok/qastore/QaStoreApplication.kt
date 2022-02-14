package com.taverok.qastore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QaStoreApplication

fun main(args: Array<String>) {
	runApplication<QaStoreApplication>(*args)
}
