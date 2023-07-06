package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

/**
 * A role.
 * @param name The role's name
 */
@Schema(name = "Role", description = "A role.")
@JsonTypeName("Role")
data class RoleDto(

    @get:Size(min = 1, max = 80)
    @Schema(example = "admin", required = true, description = "The role's name")
    @get:JsonProperty("name", required = true) val name: String
)

