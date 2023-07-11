package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * A pet type.
 * @param name The name of the pet type.
 * @param id The ID of the pet type.
 */
@Schema(name = "PetType", description = "A pet type.")
@JsonTypeName("PetType")
data class PetTypeDto(
    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the pet type.")
    @get:JsonProperty("id", required = true) val id: Int,

    @get:Size(min = 1, max = 80)
    @Schema(example = "cat", required = true, description = "The name of the pet type.")
    @get:JsonProperty("name", required = true) val name: String,
)

