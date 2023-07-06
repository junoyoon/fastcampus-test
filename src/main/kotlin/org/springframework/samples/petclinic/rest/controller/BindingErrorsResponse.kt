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
package org.springframework.samples.petclinic.rest.controller

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.validation.BindingResult

/**
 * @author Vitaliy Fedoriv
 */
class BindingErrorsResponse(pathId: Int?, bodyId: Int?) {
    constructor(id: Int? = null) : this(null, id)

    private fun addBodyIdError(bodyId: Int?, message: String) {
        addError(
            BindingError(
                objectName = "body",
                fieldName = "id",
                fieldValue = bodyId?.or(0).toString(),
                errorMessage = message
            )
        )
    }

    private val bindingErrors = mutableListOf<BindingError>()

    init {
        val onlyBodyIdSpecified = pathId == null && bodyId != null
        if (onlyBodyIdSpecified) {
            addBodyIdError(bodyId, "must not be specified")
        }
        val bothIdsSpecified = pathId != null && bodyId != null
        if (bothIdsSpecified && pathId != bodyId) {
            addBodyIdError(bodyId, "does not match pathId: $pathId")
        }
    }

    private fun addError(bindingError: BindingError) {
        bindingErrors.add(bindingError)
    }

    fun addAllErrors(bindingResult: BindingResult) {
        bindingResult.fieldErrors.forEach {
            addError(
                BindingError(
                    objectName = it.objectName,
                    fieldName = it.field,
                    fieldValue = it.rejectedValue?.toString() ?: "",
                    errorMessage = it.defaultMessage.orEmpty()
                )
            )
        }
    }

    fun toJSON(): String {
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)

        return try {
            mapper.writeValueAsString(bindingErrors)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            ""
        }
    }

    override fun toString(): String {
        return "BindingErrorsResponse [bindingErrors=$bindingErrors]"
    }

    data class BindingError(
        val objectName: String = "",
        val fieldName: String = "",
        val fieldValue: String = "",
        val errorMessage: String = "",
    )
}
