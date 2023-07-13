package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * A veterinarian.
 * @param firstName The first name of the vet.
 * @param lastName The last name of the vet.
 * @param specialties The specialties of the vet.
 * @param id The ID of the vet.
 */
@Schema(name = "Vet", description = "A veterinarian.")
@JsonTypeName("Vet")
data class VetDto(

    @field:Pattern(regexp = "[a-zA-Z]{1,50}")
    @field:Size(min = 1, max = 50)
    @Schema(example = "James", required = true, description = "The first name of the vet.")
    @field:JsonProperty("firstName", required = true) val firstName: String,

    @field:Pattern(regexp = "[a-zA-Z]{1,50}")
    @field:Size(min = 1, max = 50)
    @Schema(example = "Carter", required = true, description = "The last name of the vet.")
    @field:JsonProperty("lastName", required = true) val lastName: String,

    @field:Valid
    @Schema(example = "null", required = true, description = "The specialties of the vet.")
    @get:JsonProperty("specialties", required = true) val specialties: List<SpecialtyDto>,

    @field:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the vet.")
    @field:JsonProperty("id", required = true) val id: Int = 0
)

