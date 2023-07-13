package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * A booking for a vet visit.
 * @param description The description for the visit.
 * @param id The ID of the visit.
 * @param date The date of the visit.
 * @param petId The ID of the pet.
 */
@Schema(name = "Visit", description = "A booking for a vet visit.")
@JsonTypeName("Visit")
data class VisitDto(

    @get:Size(min = 1, max = 255)
    @Schema(example = "rabies shot", required = true, description = "The description for the visit.")
    @get:JsonProperty("description", required = true) val description: String,

    @get:Min(0)
    @Schema(example = "1", required = true, readOnly = true, description = "The ID of the visit.")
    @get:JsonProperty("id", required = true) val id: Int = 0,

    @field:Valid
    @Schema(example = "Tue Jan 01 09:00:00 KST 2013", description = "The date of the visit.")
    @get:JsonProperty("date") val date: java.time.LocalDate? = null,

    @get:Min(0)
    @Schema(example = "1", readOnly = true, description = "The ID of the pet.")
    @get:JsonProperty("petId") val petId: Int? = null
)

