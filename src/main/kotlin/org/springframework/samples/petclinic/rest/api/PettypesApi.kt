/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.springframework.samples.petclinic.rest.api

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.enums.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import io.swagger.v3.oas.annotations.security.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.rest.dto.RestErrorDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RequestMapping("\${api.base-path:/petclinic/api}")
interface PettypesApi {

    @Operation(
        summary = "Create a pet type",
        operationId = "addPetType",
        description = """Creates a pet type .""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet type created successfully.",
                content = [Content(schema = Schema(implementation = PetTypeDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet Type not found.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Server error.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            )
        ]
    )
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/pettypes"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addPetType(
        @Parameter(
            description = "The pet type",
            required = true
        ) @Valid @RequestBody petTypeDto: PetTypeDto
    ): ResponseEntity<PetTypeDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Delete a pet type by ID",
        operationId = "deletePetType",
        description = """Returns the pet type or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet type details found and returned.",
                content = [Content(schema = Schema(implementation = PetTypeDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet type not found.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Server error.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            )
        ]
    )
    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/pettypes/{petTypeId}"],
        produces = ["application/json"]
    )
    fun deletePetType(
        @Min(0) @Parameter(
            description = "The ID of the pet type.",
            required = true
        ) @PathVariable("petTypeId") petTypeId: Int
    ): ResponseEntity<PetTypeDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Get a pet type by ID",
        operationId = "getPetType",
        description = """Returns the pet type or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet type details found and returned.",
                content = [Content(schema = Schema(implementation = PetTypeDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet Type not found.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Server error.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            )
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/pettypes/{petTypeId}"],
        produces = ["application/json"]
    )
    fun getPetType(
        @Min(0) @Parameter(
            description = "The ID of the pet type.",
            required = true
        ) @PathVariable("petTypeId") petTypeId: Int
    ): ResponseEntity<PetTypeDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Lists pet types",
        operationId = "listPetTypes",
        description = """Returns an array of pet types.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet types found and returned.",
                content = [Content(schema = Schema(implementation = PetTypeDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "500",
                description = "Server error.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            )
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/pettypes"],
        produces = ["application/json"]
    )
    fun listPetTypes(): ResponseEntity<List<PetTypeDto>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Update a pet type by ID",
        operationId = "updatePetType",
        description = """Returns the pet type or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet type details found and returned.",
                content = [Content(schema = Schema(implementation = PetTypeDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet Type not found.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Server error.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            )
        ]
    )
    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/pettypes/{petTypeId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updatePetType(
        @Min(0) @Parameter(
            description = "The ID of the pet type.",
            required = true
        ) @PathVariable("petTypeId") petTypeId: Int,
        @Parameter(
            description = "The pet type",
            required = true
        ) @Valid @RequestBody petTypeDto: PetTypeDto
    ): ResponseEntity<PetTypeDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }
}
