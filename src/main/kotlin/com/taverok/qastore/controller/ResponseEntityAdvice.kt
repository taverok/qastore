package com.taverok.qastore.controller

import com.taverok.qastore.dto.JsonMessage
import com.taverok.qastore.exception.ClientSideException
import com.taverok.qastore.exception.NotFoundException
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest


@RestControllerAdvice
class ResponseEntityAdvice {
    val log = KotlinLogging.logger {  }

    @ExceptionHandler(value = [NotFoundException::class])
    private fun handleNotFoundException(ex: NotFoundException): JsonMessage<String> {
        val message = ex.message ?: "Resource not found"
        log.warn("{} {}", ex.javaClass.simpleName, ex.message)

        return JsonMessage.withError(message)
    }

    @ExceptionHandler(value = [ClientSideException::class])
    protected fun handleClientSideException(ex: ClientSideException, request: WebRequest): JsonMessage<String> {
        val message = ex.message ?: "Client error"
        log.info("{} {}", ex.javaClass.simpleName, ex.message)

        return JsonMessage.withError(message)
    }

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    protected fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException, request: WebRequest): JsonMessage<String> {
        log.error(ex) { "http request: $request" }

        return JsonMessage.withError("DataIntegrity error")
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    protected fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException, request: WebRequest): JsonMessage<String> {
        log.info(ex) { "http request: $request" }

        return JsonMessage.withError("${ex.fieldError?.field} ${ex.fieldError?.defaultMessage}")
    }

    @ExceptionHandler(value = [RuntimeException::class])
    protected fun handleRuntimeException(ex: RuntimeException, request: WebRequest): JsonMessage<String> {
        val message = ex.message ?: "RuntimeException"
        log.error(ex) { "http request: $request" }

        return JsonMessage.withError(message)
    }
}