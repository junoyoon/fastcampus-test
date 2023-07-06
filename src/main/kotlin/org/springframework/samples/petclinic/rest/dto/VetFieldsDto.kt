package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * Editable fields of a veterinarian.
 * @param firstName The first name of the vet.
 * @param lastName The last name of the vet.
 * @param specialties The specialties of the vet.
 */
@Schema(name = "VetFields", description = "Editable fields of a veterinarian.")
@JsonTypeName("VetFields")
data class VetFieldsDto(

    @get:Pattern(regexp = "^[a-zA-Z]*$")
    @get:Size(min = 1, max = 30)
    @Schema(example = "James", required = true, description = "The first name of the vet.")
    @get:JsonProperty("firstName", required = true) val firstName: String,

    @get:Pattern(regexp = "^[a-zA-Z]*$")
    @get:Size(min = 1, max = 30)
    @Schema(example = "Carter", required = true, description = "The last name of the vet.")
    @get:JsonProperty("lastName", required = true) val lastName: String,

    @field:Valid
    @Schema(example = "null", required = true, description = "The specialties of the vet.")
    @get:JsonProperty("specialties", required = true) val specialties: List<SpecialtyDto>
)

