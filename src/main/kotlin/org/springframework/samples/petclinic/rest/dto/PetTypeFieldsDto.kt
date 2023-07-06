package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

/**
 * Editable fields of a pet type.
 * @param name The name of the pet type.
 */
@Schema(name = "PetTypeFields", description = "Editable fields of a pet type.")
@JsonTypeName("PetTypeFields")
data class PetTypeFieldsDto(

    @get:Size(min = 1, max = 80)
    @Schema(example = "cat", required = true, description = "The name of the pet type.")
    @get:JsonProperty("name", required = true) val name: String
)

