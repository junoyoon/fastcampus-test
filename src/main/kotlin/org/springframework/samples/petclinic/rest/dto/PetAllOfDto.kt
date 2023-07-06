package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min

/**
 *
 * @param id The ID of the pet.
 * @param visits Vet visit bookings for this pet.
 * @param ownerId The ID of the pet's owner.
 */
@JsonTypeName("Pet_allOf")
data class PetAllOfDto(

    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the pet.")
    @get:JsonProperty("id", required = true) val id: Int,

    @field:Valid
    @Schema(example = "null", required = true, readOnly = true, description = "Vet visit bookings for this pet.")
    @get:JsonProperty("visits", required = true) val visits: List<VisitDto>,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet's owner.")
    @get:JsonProperty("ownerId") val ownerId: Int? = null
)

