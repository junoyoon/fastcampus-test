package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min

/**
 *
 * @param pets The pets owned by this individual including any booked vet visits.
 * @param id The ID of the pet owner.
 */
@JsonTypeName("Owner_allOf")
data class OwnerAllOfDto(

    @field:Valid
    @Schema(
        example = "null",
        required = true,
        readOnly = true,
        description = "The pets owned by this individual including any booked vet visits."
    )
    @get:JsonProperty("pets", required = true) val pets: List<PetDto>,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet owner.")
    @get:JsonProperty("id") val id: Int? = null
)

