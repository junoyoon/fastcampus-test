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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.mapper.PetMapper
import org.springframework.samples.petclinic.mapper.PetTypeMapper
import org.springframework.samples.petclinic.model.Pet
import org.springframework.samples.petclinic.rest.api.PetsApi
import org.springframework.samples.petclinic.rest.dto.PetDto
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Vitaliy Fedoriv
 */
@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("api")
class PetRestController(
    private val clinicService: ClinicService,
) : PetsApi {

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun getPet(petId: Int): ResponseEntity<PetDto> {
        val pet = clinicService.findPetById(petId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(PetMapper.toPetDto(pet), HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun listPets(): ResponseEntity<List<PetDto>> {
        val pets = PetMapper.toPetsDto(clinicService.findAllPets())

        return if (pets.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(pets, HttpStatus.OK)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    override fun updatePet(petId: Int, petFieldsDto: PetFieldsDto): ResponseEntity<PetDto> {
        val currentPet: Pet = clinicService.findPetById(petId)?.apply {
            birthDate = petFieldsDto.birthDate
            name = petFieldsDto.name
            type = PetTypeMapper.toPetType(petFieldsDto.type)
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.savePet(currentPet)
        return ResponseEntity(PetMapper.toPetDto(currentPet), HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    override fun deletePet(petId: Int): ResponseEntity<PetDto> {
        val pet: Pet = clinicService.findPetById(petId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deletePet(pet)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
