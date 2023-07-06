package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

/**
 * The schema for all error responses.
 * @param status The HTTP status code.
 * @param error The short error message.
 * @param path The path of the URL for this request.
 * @param timestamp The time the error occured.
 * @param message The long error message.
 * @param schemaValidationErrors Validation errors against the OpenAPI schema.
 * @param trace The stacktrace for this error.
 */
@Schema(name = "RestError", description = "The schema for all error responses.")
@JsonTypeName("RestError")
data class RestErrorDto(

    @Schema(example = "400", required = true, readOnly = true, description = "The HTTP status code.")
    @get:JsonProperty("status", required = true) val status: Int,

    @Schema(example = "Bad Request", required = true, readOnly = true, description = "The short error message.")
    @get:JsonProperty("error", required = true) val error: String,

    @field:Valid
    @Schema(
        example = "/api/owners",
        required = true,
        readOnly = true,
        description = "The path of the URL for this request."
    )
    @get:JsonProperty("path", required = true) val path: java.net.URI,

    @Schema(example = "null", required = true, readOnly = true, description = "The time the error occured.")
    @get:JsonProperty("timestamp", required = true) val timestamp: java.time.OffsetDateTime,

    @Schema(
        example = "Request failed schema validation",
        required = true,
        readOnly = true,
        description = "The long error message."
    )
    @get:JsonProperty("message", required = true) val message: String,

    @field:Valid
    @Schema(example = "null", required = true, description = "Validation errors against the OpenAPI schema.")
    @get:JsonProperty(
        "schemaValidationErrors",
        required = true
    ) val schemaValidationErrors: List<ValidationMessageDto>,

    @Schema(
        example = "com.atlassian.oai.validator.springmvc.InvalidRequestException: ...",
        readOnly = true,
        description = "The stacktrace for this error."
    )
    @get:JsonProperty("trace") val trace: String? = null
)

