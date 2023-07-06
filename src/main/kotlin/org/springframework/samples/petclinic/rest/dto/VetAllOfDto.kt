package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

/**
 *
 * @param id The ID of the vet.
 */
@JsonTypeName("Vet_allOf")
data class VetAllOfDto(

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the vet.")
    @get:JsonProperty("id") val id: Int? = null
)

