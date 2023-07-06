package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

/**
 *
 * @param id The ID of the pet type.
 */
@JsonTypeName("PetType_allOf")
data class PetTypeAllOfDto(

    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the pet type.")
    @get:JsonProperty("id", required = true) val id: Int
)

