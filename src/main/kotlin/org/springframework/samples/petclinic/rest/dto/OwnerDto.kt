package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * A pet owner.
 * @param firstName The first name of the pet owner.
 * @param lastName The last name of the pet owner.
 * @param address The postal address of the pet owner.
 * @param city The city of the pet owner.
 * @param telephone The telephone number of the pet owner.
 * @param pets The pets owned by this individual including any booked vet visits.
 * @param id The ID of the pet owner.
 */
data class OwnerDto(

    @get:Pattern(regexp = "^[a-zA-Z]*$")
    @get:Size(min = 1, max = 30)
    @Schema(example = "George", required = true, description = "The first name of the pet owner.")
    @get:JsonProperty("firstName", required = true) val firstName: String,

    @get:Pattern(regexp = "^[a-zA-Z]*$")
    @get:Size(min = 1, max = 30)
    @Schema(example = "Franklin", required = true, description = "The last name of the pet owner.")
    @get:JsonProperty("lastName", required = true) val lastName: String,

    @get:Size(min = 1, max = 255)
    @Schema(example = "110 W. Liberty St.", required = true, description = "The postal address of the pet owner.")
    @get:JsonProperty("address", required = true) val address: String,

    @get:Size(min = 1, max = 80)
    @Schema(example = "Madison", required = true, description = "The city of the pet owner.")
    @get:JsonProperty("city", required = true) val city: String,

    @get:Pattern(regexp = "^[0-9]*$")
    @get:Size(min = 1, max = 20)
    @Schema(example = "6085551023", required = true, description = "The telephone number of the pet owner.")
    @get:JsonProperty("telephone", required = true) val telephone: String,

    @field:Valid
    @Schema(
        example = "null",
        required = true,
        readOnly = true,
        description = "The pets owned by this individual including any booked vet visits."
    )
    @get:JsonProperty("pets", required = true) val pets: List<PetDto>? = null,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet owner.")
    @get:JsonProperty("id") val id: Int? = null
)

