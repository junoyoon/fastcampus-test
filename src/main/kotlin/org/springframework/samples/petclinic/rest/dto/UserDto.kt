package org.springframework.samples.petclinic.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

/**
 * An user.
 * @param username The username
 * @param password The password
 * @param enabled Indicates if the user is enabled
 * @param roles The roles of an user
 */
@Schema(name = "User", description = "An user.")
@JsonTypeName("User")
data class UserDto(

    @get:Size(min = 1, max = 80)
    @Schema(example = "john.doe", required = true, description = "The username")
    @get:JsonProperty("username", required = true) val username: String,

    @get:Size(min = 1, max = 80)
    @Schema(example = "1234abc", description = "The password")
    @get:JsonProperty("password") val password: String? = null,

    @Schema(example = "true", description = "Indicates if the user is enabled")
    @get:JsonProperty("enabled") val enabled: Boolean? = null,

    @field:Valid
    @Schema(example = "null", description = "The roles of an user")
    @get:JsonProperty("roles") val roles: List<RoleDto>? = null
)

