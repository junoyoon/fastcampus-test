/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.rest.controller

import jakarta.transaction.Transactional
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.mapper.OwnerMapper
import org.springframework.samples.petclinic.mapper.PetMapper
import org.springframework.samples.petclinic.mapper.VisitMapper
import org.springframework.samples.petclinic.model.*
import org.springframework.samples.petclinic.rest.api.OwnersApi
import org.springframework.samples.petclinic.rest.dto.*
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

/**
 * @author Vitaliy Fedoriv
 */
@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api")
class OwnerRestController(
    private val clinicService: ClinicService,
) : OwnersApi {

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun listOwners(lastName: String?): ResponseEntity<List<OwnerDto>> {
        val owners = if (lastName != null) {
            clinicService.findOwnerByLastName(lastName)
        } else {
            clinicService.findAllOwners()
        }

        return if (owners.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(
            OwnerMapper.toOwnerDtoCollection(
                owners
            ), HttpStatus.OK
        )
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun getOwner(ownerId: Int): ResponseEntity<OwnerDto> {
        val owner: Owner = clinicService.findOwnerById(ownerId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(OwnerMapper.toOwnerDto(owner), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun addOwner(ownerFieldsDto: OwnerFieldsDto): ResponseEntity<OwnerDto> {

        val owner = OwnerMapper.toOwner(ownerFieldsDto)
        clinicService.saveOwner(owner)
        val ownerDto = OwnerMapper.toOwnerDto(owner)
        val headers = HttpHeaders().apply {
            location =
                UriComponentsBuilder.newInstance()
                    .path("/api/owners/{id}").buildAndExpand(owner.id).toUri()
        }
        return ResponseEntity(ownerDto, headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun updateOwner(ownerId: Int, ownerFieldsDto: OwnerFieldsDto): ResponseEntity<OwnerDto> {
        val currentOwner: Owner = clinicService.findOwnerById(ownerId)?.apply {
            address = ownerFieldsDto.address
            city = ownerFieldsDto.city
            firstName = ownerFieldsDto.firstName
            lastName = ownerFieldsDto.lastName
            telephone = ownerFieldsDto.telephone
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.saveOwner(currentOwner)
        return ResponseEntity(OwnerMapper.toOwnerDto(currentOwner), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    override fun deleteOwner(ownerId: Int): ResponseEntity<OwnerDto> {
        val owner: Owner = clinicService.findOwnerById(ownerId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteOwner(owner)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun addPetToOwner(ownerId: Int, petFieldsDto: PetFieldsDto): ResponseEntity<PetDto> {

        val owner = Owner().apply {
            id = ownerId
        }

        val pet = PetMapper.toPet(petFieldsDto).apply {
            this.owner = owner
        }

        clinicService.savePet(pet)
        val petDto = PetMapper.toPetDto(pet)
        val headers = HttpHeaders().apply {
            this.location =
                UriComponentsBuilder.newInstance().path("/api/pets/{id}")
                    .buildAndExpand(pet.id).toUri()
        }
        return ResponseEntity(petDto, headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun addVisitToOwner(ownerId: Int, petId: Int, visitFieldsDto: VisitFieldsDto): ResponseEntity<VisitDto> {
        val pet = Pet().apply {
            this.id = petId
        }
        val visit = VisitMapper.toVisit(visitFieldsDto).apply {
            this.pet = pet
        }
        clinicService.saveVisit(visit)
        val visitDto = VisitMapper.toVisitDto(visit)

        val headers = HttpHeaders().apply {
            location =
                UriComponentsBuilder.newInstance().path("/api/visits/{id}")
                    .buildAndExpand(visit.id).toUri()
        }
        return ResponseEntity(visitDto, headers, HttpStatus.CREATED)
    }
}
