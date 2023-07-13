package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * Fields of specialty of vets.
 * @param id The ID of the specialty.
 * @param name The name of the specialty.
 */
@Schema(name = "Specialty", description = "Fields of specialty of vets.")
@JsonTypeName("Specialty")
data class SpecialtyDto(

    @field:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the specialty.")
    @get:JsonProperty("id", required = true) val id: Int,

    @get:Size(min = 1, max = 80)
    @Schema(example = "radiology", required = true, description = "The name of the specialty.")
    @get:JsonProperty("name", required = true) val name: String
)

