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
import org.springframework.samples.petclinic.mapper.PetTypeMapper
import org.springframework.samples.petclinic.model.PetType
import org.springframework.samples.petclinic.rest.api.PetTypesApi
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto
import org.springframework.samples.petclinic.rest.dto.PetTypeDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("api")
class PetTypeRestController(
    private val clinicService: ClinicService,
) : PetTypesApi {

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    override fun listPetTypes(): ResponseEntity<List<PetTypeDto>> {
        val petTypes: List<PetType> = clinicService.findAllPetTypes()
        return if (petTypes.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(
            PetTypeMapper.toPetTypeDtos(petTypes),
            HttpStatus.OK
        )
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    override fun getPetType(petTypeId: Int): ResponseEntity<PetTypeDto> {
        val petType: PetType = clinicService.findPetTypeById(petTypeId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(PetTypeMapper.toNotNullPetTypeDto(petType), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun addPetType(petTypeDto: PetTypeDto): ResponseEntity<PetTypeDto> {

        val type = PetTypeMapper.toPetType(petTypeDto)
        clinicService.savePetType(type!!)
        val headers = HttpHeaders().apply {
            location = UriComponentsBuilder
                .newInstance()
                .path("/api/pettypes/{id}")
                .buildAndExpand(type.id).toUri()
        }
        return ResponseEntity(PetTypeMapper.toPetTypeDto(type), headers, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    override fun updatePetType(petTypeId: Int, petFieldsDto: PetFieldsDto): ResponseEntity<PetTypeDto> {
        val currentPetType: PetType = clinicService.findPetTypeById(petTypeId)?.apply {
            this.name = petFieldsDto.name
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        clinicService.savePetType(currentPetType)
        return ResponseEntity(PetTypeMapper.toPetTypeDto(currentPetType), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    override fun deletePetType(petTypeId: Int): ResponseEntity<PetTypeDto> {
        val petType: PetType = clinicService.findPetTypeById(petTypeId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deletePetType(petType)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
