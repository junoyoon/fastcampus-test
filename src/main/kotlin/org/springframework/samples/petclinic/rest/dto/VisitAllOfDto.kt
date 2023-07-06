package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

/**
 *
 * @param id The ID of the visit.
 * @param petId The ID of the pet.
 */
@JsonTypeName("Visit_allOf")
data class VisitAllOfDto(

    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the visit.")
    @get:JsonProperty("id", required = true) val id: Int,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet.")
    @get:JsonProperty("petId") val petId: Int? = null
)

