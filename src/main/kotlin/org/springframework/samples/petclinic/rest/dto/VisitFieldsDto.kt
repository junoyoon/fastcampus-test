package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

/**
 * Editable fields of a vet visit.
 * @param description The description for the visit.
 * @param date The date of the visit.
 */
@Schema(name = "VisitFields", description = "Editable fields of a vet visit.")
@JsonTypeName("VisitFields")
data class VisitFieldsDto(

    @get:Size(min = 1, max = 255)
    @Schema(example = "rabies shot", required = true, description = "The description for the visit.")
    @get:JsonProperty("description", required = true) val description: String,

    @field:Valid
    @Schema(example = "Tue Jan 01 09:00:00 KST 2013", description = "The date of the visit.")
    @get:JsonProperty("date") val date: java.time.LocalDate? = null
)

