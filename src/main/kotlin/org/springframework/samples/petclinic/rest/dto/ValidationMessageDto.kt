package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Messages describing a validation error.
 * @param message The valiation message.
 */
@Schema(name = "ValidationMessage", description = "Messages describing a validation error.")
@JsonTypeName("ValidationMessage")
data class ValidationMessageDto(

    @Schema(
        example = "[Path '/lastName'] Instance type (null) does not match any allowed primitive type (allowed: [\"string\"])",
        required = true,
        readOnly = true,
        description = "The valiation message."
    )
    @get:JsonProperty("message", required = true) val message: String
) : HashMap<String, Any>()

