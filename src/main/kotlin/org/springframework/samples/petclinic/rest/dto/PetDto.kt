package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * A pet.
 * @param name The name of the pet.
 * @param birthDate The date of birth of the pet.
 * @param type
 * @param id The ID of the pet.
 * @param visits Vet visit bookings for this pet.
 * @param ownerId The ID of the pet's owner.
 */
@Schema(name = "Pet", description = "A pet.")
@JsonTypeName("Pet")
data class PetDto(

    @get:Size(max = 30)
    @Schema(example = "Leo", required = true, description = "The name of the pet.")
    @get:JsonProperty("name", required = true) val name: String,

    @field:Valid
    @Schema(example = "Tue Sep 07 09:00:00 KST 2010", required = true, description = "The date of birth of the pet.")
    @get:JsonProperty("birthDate", required = true) val birthDate: java.time.LocalDate,

    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("type", required = true) val type: PetTypeDto? = null,

    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the pet.")
    @get:JsonProperty("id", required = true) val id: Int,

    @field:Valid
    @Schema(example = "null", required = true, readOnly = true, description = "Vet visit bookings for this pet.")
    @get:JsonProperty("visits", required = true) val visits: List<VisitDto>? = null,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet's owner.")
    @get:JsonProperty("ownerId") val ownerId: Int? = null
)

