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
import org.springframework.samples.petclinic.rest.dto.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Validated
@RequestMapping("\${api.base-path:/petclinic/api}")
interface OwnersApi {

    @Operation(
        summary = "Adds a pet owner",
        operationId = "addOwner",
        description = """Records the details of a new pet owner.""",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "The pet owner was sucessfully added.",
                content = [Content(schema = Schema(implementation = OwnerDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
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
        value = ["/owners"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addOwner(
        @Parameter(
            description = "The pet owner",
            required = true
        ) @Valid @RequestBody ownerFieldsDto: OwnerFieldsDto
    ): ResponseEntity<OwnerDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Adds a pet to an owner",
        operationId = "addPetToOwner",
        description = """Records the details of a new pet.""",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "The pet was sucessfully added.",
                content = [Content(schema = Schema(implementation = PetDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet not found.",
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
        value = ["/owners/{ownerId}/pets"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addPetToOwner(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int,
        @Parameter(
            description = "The details of the new pet.",
            required = true
        ) @Valid @RequestBody petFieldsDto: PetFieldsDto
    ): ResponseEntity<PetDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Adds a vet visit",
        operationId = "addVisitToOwner",
        description = """Records the details of a new vet visit.""",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "The vet visit was sucessfully added.",
                content = [Content(schema = Schema(implementation = VisitDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet not found for this owner.",
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
        value = ["/owners/{ownerId}/pets/{petId}/visits"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addVisitToOwner(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int,
        @Min(0) @Parameter(
            description = "The ID of the pet.",
            required = true
        ) @PathVariable("petId") petId: Int,
        @Parameter(
            description = "The details of the new vet visit.",
            required = true
        ) @Valid @RequestBody visitFieldsDto: VisitFieldsDto
    ): ResponseEntity<VisitDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Delete an owner by ID",
        operationId = "deleteOwner",
        description = """Returns the owner or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Owner details found and returned.",
                content = [Content(schema = Schema(implementation = OwnerDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Owner  not found.",
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
        value = ["/owners/{ownerId}"],
        produces = ["application/json"]
    )
    fun deleteOwner(
        @Min(0) @Parameter(
            description = "The ID of the owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int
    ): ResponseEntity<OwnerDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Get a pet owner by ID",
        operationId = "getOwner",
        description = """Returns the pet owner or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Owner details found and returned.",
                content = [Content(schema = Schema(implementation = OwnerDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Owner not found.",
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
        value = ["/owners/{ownerId}"],
        produces = ["application/json"]
    )
    fun getOwner(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int
    ): ResponseEntity<OwnerDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/owners-webflux/{ownerId}"],
        produces = ["application/json"],
    )
    fun getOwnerByWebFlux(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int
    ): ResponseEntity<Mono<OwnerDto>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }


    @Operation(
        summary = "Get a pet by ID",
        operationId = "getOwnersPet",
        description = """Returns the pet or a 404 error.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Pet details found and returned.",
                content = [Content(schema = Schema(implementation = PetDto::class))]
            ),
            ApiResponse(responseCode = "304", description = "Not modified."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet not found.",
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
        value = ["/owners/{ownerId}/pets/{petId}"],
        produces = ["application/json"]
    )
    fun getOwnersPet(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int,
        @Min(0) @Parameter(
            description = "The ID of the pet.",
            required = true
        ) @PathVariable("petId") petId: Int
    ): ResponseEntity<PetDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Lists pet owners",
        operationId = "listOwners",
        description = """Returns an array of pet owners.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Owner details found and returned.",
                content = [Content(schema = Schema(implementation = OwnerDto::class))]
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
        value = ["/owners"],
        produces = ["application/json"]
    )
    fun listOwners(
        @Parameter(description = "Last name.") @Valid @RequestParam(
            value = "lastName",
            required = false
        ) lastName: String?
    ): ResponseEntity<List<OwnerDto>> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Update a pet owner's details",
        operationId = "updateOwner",
        description = """Updates the pet owner record with the specified details.""",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Update successful.",
                content = [Content(schema = Schema(implementation = OwnerDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Owner not found.",
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
        value = ["/owners/{ownerId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateOwner(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int,
        @Parameter(
            description = "The pet owner details to use for the update.",
            required = true
        ) @Valid @RequestBody ownerFieldsDto: OwnerFieldsDto
    ): ResponseEntity<OwnerDto> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @Operation(
        summary = "Update a pet's details",
        operationId = "updateOwnersPet",
        description = """Updates the pet record with the specified details.""",
        responses = [
            ApiResponse(responseCode = "204", description = "Update successful."),
            ApiResponse(
                responseCode = "400",
                description = "Bad request.",
                content = [Content(schema = Schema(implementation = RestErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Pet not found for this owner.",
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
        value = ["/owners/{ownerId}/pets/{petId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateOwnersPet(
        @Min(0) @Parameter(
            description = "The ID of the pet owner.",
            required = true
        ) @PathVariable("ownerId") ownerId: Int,
        @Min(0) @Parameter(
            description = "The ID of the pet.",
            required = true
        ) @PathVariable("petId") petId: Int,
        @Parameter(
            description = "The pet details to use for the update.",
            required = true
        ) @Valid @RequestBody petFieldsDto: PetFieldsDto
    ): ResponseEntity<Unit> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }
}
