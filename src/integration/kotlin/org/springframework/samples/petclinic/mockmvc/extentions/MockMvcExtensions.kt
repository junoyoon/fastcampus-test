package org.springframework.samples.petclinic.mockmvc.extentions

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.ResultActionsDsl

inline fun <reified T : Any> ResultActionsDsl.andReturnBodyAs(objectMapper: ObjectMapper) : T {
    return objectMapper.readValue(this.andReturn().response.contentAsString, object: TypeReference<T>() {})
}
