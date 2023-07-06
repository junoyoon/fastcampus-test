package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

/**
 * Editable fields of a pet.
 * @param name The name of the pet.
 * @param birthDate The date of birth of the pet.
 * @param type
 */
@Schema(name = "PetFields", description = "Editable fields of a pet.")
@JsonTypeName("PetFields")
data class PetFieldsDto(

    @get:Size(max = 30)
    @Schema(example = "Leo", required = true, description = "The name of the pet.")
    @get:JsonProperty("name", required = true) val name: String,

    @field:Valid
    @Schema(example = "Tue Sep 07 09:00:00 KST 2010", required = true, description = "The date of birth of the pet.")
    @get:JsonProperty("birthDate", required = true) val birthDate: java.time.LocalDate,

    @field:Valid
    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("type", required = true) val type: PetTypeDto
)

