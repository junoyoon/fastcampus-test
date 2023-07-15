package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * Editable fields of a pet owner.
 * @param firstName The first name of the pet owner.
 * @param lastName The last name of the pet owner.
 * @param address The postal address of the pet owner.
 * @param city The city of the pet owner.
 * @param telephone The telephone number of the pet owner.
 */
@Schema(name = "OwnerFields", description = "Editable fields of a pet owner.")
@JsonTypeName("OwnerFields")
data class OwnerFieldsDto(

    @get:Pattern(regexp = "[a-zA-Z]*")
    @get:Size(min = 1, max = 30)
    @Schema(example = "George", required = true, description = "The first name of the pet owner.")
    @get:JsonProperty("firstName", required = true) val firstName: String,

    @get:Pattern(regexp = "[a-zA-Z]*")
    @get:Size(min = 1, max = 30)
    @Schema(example = "Franklin", required = true, description = "The last name of the pet owner.")
    @get:JsonProperty("lastName", required = true) val lastName: String,

    @get:Size(min = 1, max = 255)
    @Schema(example = "110 W. Liberty St.", required = true, description = "The postal address of the pet owner.")
    @get:JsonProperty("address", required = true) val address: String,

    @get:Size(min = 1, max = 80)
    @Schema(example = "Madison", required = true, description = "The city of the pet owner.")
    @get:JsonProperty("city", required = true) val city: String,

    @get:Pattern(regexp = "[0-9]*")
    @get:Size(min = 1, max = 20)
    @Schema(example = "6085551023", required = true, description = "The telephone number of the pet owner.")
    @get:JsonProperty("telephone", required = true) val telephone: String
)

