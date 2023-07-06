/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.rest.advice

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.rest.controller.BindingErrorsResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest

/**
 * @author Vitaliy Fedoriv
 */
@ControllerAdvice
class ExceptionControllerAdvice {
    fun exception(e: Exception): ResponseEntity<String> {
        val mapper = ObjectMapper()
        val errorInfo = ErrorInfo(e)
        var respJSONstring = "{}"
        try {
            respJSONstring = mapper.writeValueAsString(errorInfo)
        } catch (e1: JsonProcessingException) {
            e1.printStackTrace()
        }
        return ResponseEntity.badRequest().body(respJSONstring)
    }

    /**
     * Handles exception thrown by Bean Validation on controller methods parameters
     *
     * @param ex      The thrown exception
     * @param request the current web request
     * @return an empty response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        @Suppress("UNUSED_PARAMETER") request: WebRequest
    ): ResponseEntity<Void> {
        val errors = BindingErrorsResponse()
        val bindingResult = ex.bindingResult
        val headers = HttpHeaders()
        if (bindingResult.hasErrors()) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
        }
        return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
    }

    private inner class ErrorInfo(ex: Exception) {
        val className: String
        val exMessage: String

        init {
            className = ex.javaClass.name
            exMessage = ex.localizedMessage
        }
    }
}
